package com.freerdp.user;

import org.springframework.web.socket.WebSocketSession;
import java.awt.image.BufferedImage;

public class UserData {
    public boolean isConnected;
    public WebSocketSession ws;
    public long instance;
    public byte[] bitmap;
    public BufferedImage image;

    public UserData() {
        isConnected = false;
        ws = null;
        instance = 0;
        bitmap = null;
        BufferedImage image = null;
    }
}