package com.freerdp.user;

import org.springframework.web.socket.WebSocketSession;

import com.freerdp.services.LibFreeRDP;

import java.awt.image.BufferedImage;

public class UserData {
    public static long instance = 0;
    public static byte[] bitmap = null;
    public static BufferedImage image = null;
    public static WebSocketSession ws;
}
