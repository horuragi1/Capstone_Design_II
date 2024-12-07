package com.freerdp.user;

import org.springframework.web.socket.WebSocketSession;
import java.awt.image.BufferedImage;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UserData {
    public boolean isConnected;
    public WebSocketSession ws;
    public long instance;
    public byte[] bitmap;
    public BufferedImage image;
    
    public byte[] previousBitmap; // 이전 프레임의 비트맵 데이터

    

}