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
    private static native boolean freerdp_send_key_event(long inst, int virtualCode, boolean isDown, boolean repeat);

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

    public static boolean send_wheel_event(long inst, int x, int y, int deltaX)
    {
        int flags = InputMapper.WheelEventToFlags(deltaX);
        return freerdp_send_cursor_event(inst, x, y, flags);
    }

    public static boolean send_key_event(long inst, int virtualCode, boolean isDown, boolean repeat)
    {
        return freerdp_send_key_event(inst, virtualCode, isDown, repeat);
    }

    private static void OnGraphicsUpdate(long inst, int x, int y, int width, int height) {
        UserData userData = UserDataManager.getUserDataOf(inst);
        if (userData == null || userData.ws == null)
            return;
        UserDataManager.updateBitmap(inst, x, y, x + width - 1, y + height - 1);
    }

    public static void sendDelta(WebSocketSession ws, int minX, int minY, int maxX, int maxY, byte[] bitmap, int totalWidth) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            //UserDataManager.stopWatch.start();
        	dos.writeShort(minX);
        	dos.writeShort(minY);
        	dos.writeShort(maxX);
        	dos.writeShort(maxY);

            for (int yy = minY; yy <= maxY; yy++) {
                for (int xx = minX; xx <= maxX; xx++) {
                    int index = (yy * totalWidth + xx) * 4;
                    int r = bitmap[index + 2] & 0xFF;
                    int g = bitmap[index + 1] & 0xFF;
                    int b = bitmap[index] & 0xFF;

                    // Save bounding box to (r, g, b)
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