/*
   Android FreeRDP JNI Wrapper

   Copyright 2013 Thincast Technologies GmbH, Author: Martin Fleisz

   This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
   If a copy of the MPL was not distributed with this file, You can obtain one at
   http://mozilla.org/MPL/2.0/.
*/

package com.freerdp.services;

import com.freerdp.user.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LibFreeRDP
{
    private static final Logger logger = LoggerFactory.getLogger(LibFreeRDP.class);

    static
    {
        try
        {
            System.loadLibrary("JNI");
            logger.info("Successfully loaded native library.");
        }
        catch (UnsatisfiedLinkError e)
        {
            logger.error("Failed to load library: {}", e.toString());
            throw e;
        }
    }
    private static native long freerdp_new();
    private static native void freerdp_free(long inst);
    private static native boolean freerdp_parse_args(long inst, String s);
    private static native boolean freerdp_connect(long inst);
    private static native boolean freerdp_disconnect(long inst);
    private static native int get_freerdp_desktop_height(long inst);
    private static native int get_freerdp_desktop_width(long inst);
    private static native boolean freerdp_copy_bitmap(long inst, byte[] bitmap, int x, int y, int width, int height);
    private static native boolean freerdp_send_cursor_event(long inst, int x, int y, int flags);
    private static native boolean freerdp_send_key_event(long inst, int virtualCode, boolean isDown);

    public static long newInstance() { return freerdp_new(); }
    public static void freeInstance(long inst) { freerdp_free(inst); }
    public static boolean parseArgs(long inst, String s) {
        return freerdp_parse_args(inst, s);
    }
    public static boolean connect(long inst) {
        return freerdp_connect(inst);
    }
    public static boolean disconnect(long inst) { return freerdp_disconnect(inst); }
    public static int get_height(long inst) { return get_freerdp_desktop_height(inst); }
    public static int get_width(long inst) { return get_freerdp_desktop_width(inst); }

    // require implementation
    public static boolean copy_bitmap(long inst, byte[] bitmap, int x, int y, int width, int height) {
        return freerdp_copy_bitmap(inst, bitmap, x, y, width, height);
    }


    public static boolean send_cursor_event(long inst, int x, int y, int buttonNum, int mouseState)
    {
        int flags = InputMapper.MouseEventToFlags(buttonNum, mouseState);
        return freerdp_send_cursor_event(inst, x, y, flags);
    }

    public static boolean send_key_event(long inst, int virtualCode, boolean isDown)
    {
        return freerdp_send_key_event(inst, virtualCode, isDown);
    }

    private static void OnGraphicsUpdate(long inst, int x, int y, int width, int height) {
        UserData userData = UserDataManager.getUserDataOf(inst);
        if (userData == null || userData.ws == null)
            return;

        // 현재 비트맵 복사
        copy_bitmap(inst, userData.bitmap, x, y, width, height);

        if (userData.bitmap != null) {
            int total_width = LibFreeRDP.get_width(userData.instance);
            int total_height = LibFreeRDP.get_height(userData.instance);

            // 이전 프레임이 없다면 초기화
            if (userData.previousBitmap == null) {
                userData.previousBitmap = new byte[userData.bitmap.length];
                System.arraycopy(userData.bitmap, 0, userData.previousBitmap, 0, userData.bitmap.length);
                return; // 첫 번째 프레임에서는 비교할 필요 없음
            }

            // 변경된 영역의 바운딩 박스를 찾기
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;

            // 바뀐 부분만 찾아서 바운딩 박스 계산
            for (int yy = y; yy < y + height; yy++) {
                for (int xx = x; xx < x + width; xx++) {
                    int index = (yy * total_width + xx) * 4;

                    // 현재 픽셀과 이전 픽셀 비교
                    boolean isDifferent =
                            userData.bitmap[index] != userData.previousBitmap[index] ||       // Blue
                            userData.bitmap[index + 1] != userData.previousBitmap[index + 1] || // Green
                            userData.bitmap[index + 2] != userData.previousBitmap[index + 2];  // Red

                    if (isDifferent) {
                        minX = Math.min(minX, xx);
                        maxX = Math.max(maxX, xx);
                        minY = Math.min(minY, yy);
                        maxY = Math.max(maxY, yy);
                    }
                }
            }

            // 바운딩 박스를 계산했으면, 해당 영역만 delta로 보냄
            if (minX <= maxX && minY <= maxY) {
                sendDelta(userData.ws, minX, minY, maxX, maxY, userData.bitmap, total_width);
            }

            // 현재 프레임을 이전 프레임으로 저장
            System.arraycopy(userData.bitmap, 0, userData.previousBitmap, 0, userData.bitmap.length);
        }
    }


    private static void sendDelta(WebSocketSession ws, int minX, int minY, int maxX, int maxY, byte[] bitmap, int totalWidth) {
        // delta data 계산
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
        	
        	dos.writeShort(minX);
        	dos.writeShort(minY);
        	dos.writeShort(maxX);
        	dos.writeShort(maxY);
        	
        	
            // 바운딩 박스 좌표와 색상 데이터를 전송
            for (int yy = minY; yy <= maxY; yy++) {
                for (int xx = minX; xx <= maxX; xx++) {
                    int index = (yy * totalWidth + xx) * 4;
                    int r = bitmap[index + 2] & 0xFF;
                    int g = bitmap[index + 1] & 0xFF;
                    int b = bitmap[index] & 0xFF;

                    // Delta 데이터로 (x, y, r, g, b) 저장
                    dos.writeShort(xx);
                    dos.writeShort(yy);
                    dos.writeByte(r);
                    dos.writeByte(g);
                    dos.writeByte(b);
                }
            }
            ws.sendMessage(new BinaryMessage(baos.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
}