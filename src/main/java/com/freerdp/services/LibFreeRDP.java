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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalTime;

public class LibFreeRDP
{
    private static final Logger logger = LoggerFactory.getLogger(LibFreeRDP.class);
/*
    public static final long VERIFY_CERT_FLAG_NONE = 0x00;
    public static final long VERIFY_CERT_FLAG_LEGACY = 0x02;
    public static final long VERIFY_CERT_FLAG_REDIRECT = 0x10;
    public static final long VERIFY_CERT_FLAG_GATEWAY = 0x20;
    public static final long VERIFY_CERT_FLAG_CHANGED = 0x40;
    public static final long VERIFY_CERT_FLAG_MISMATCH = 0x80;
    public static final long VERIFY_CERT_FLAG_MATCH_LEGACY_SHA1 = 0x100;
    public static final long VERIFY_CERT_FLAG_FP_IS_PEM = 0x200;
*/
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
    private static native boolean freerdp_login(long inst, String s);
    private static native boolean freerdp_connect(long inst);
    private static native boolean freerdp_disconnect(long inst);
    private static native int get_freerdp_desktop_height(long inst);
    private static native int get_freerdp_desktop_width(long inst);
    private static native boolean freerdp_copy_bitmap(long inst, byte[] bitmap, int x, int y, int width, int height);
    private static native boolean freerdp_send_cursor_event(long inst, int x, int y, int flags);
    private static native boolean freerdp_send_key_event(long inst, int keycode, boolean isDown);

    public static long newInstance() { return freerdp_new(); }
    public static void freeInstance(long inst) { freerdp_free(inst); }
    public static boolean login(long inst, String s) {
        return freerdp_login(inst, s);
    }
    public static boolean connect(long inst) {
        int width = LibFreeRDP.get_width(inst);
        int height = LibFreeRDP.get_height(inst);
        UserData.bitmap = new byte[4 * width * height];
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

    public static boolean send_key_event(long inst, int keycode, boolean isDown)
    {
        return freerdp_send_key_event(inst, keycode, isDown);
    }

    private static void OnGraphicsUpdate(long inst, int x, int y, int width, int height)
    {
        if(UserData.ws == null)
            return;

        //logger.info("{}, {}, {}, {}, {}", inst, x, y, width, height);
        copy_bitmap(inst, UserData.bitmap, x, y, width, height);

        if(UserData.bitmap != null) {
            int total_width = LibFreeRDP.get_width(UserData.instance);
            int total_height = LibFreeRDP.get_height(UserData.instance);
            if(UserData.image == null)
                UserData.image = new BufferedImage(total_width, total_height, BufferedImage.TYPE_INT_RGB);

            int index = 0;
            for (int yy = y; yy < y + height; yy++) {
                for (int xx = x; xx < x + width; xx++) {
                    int b = UserData.bitmap[(yy*total_width + xx)*4] & 0xFF;
                    int g = UserData.bitmap[(yy*total_width + xx)*4 + 1] & 0xFF;
                    int r = UserData.bitmap[(yy*total_width + xx)*4 + 2] & 0xFF;
                    int pixel = (r << 16) | (g << 8) | b;
                    UserData.image.setRGB(xx, yy, pixel);
                    index += 4;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(UserData.image, "bmp", baos);
                byte[] bitmapData = baos.toByteArray();
                if (bitmapData != null)
                    UserData.ws.sendMessage(new BinaryMessage(bitmapData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}