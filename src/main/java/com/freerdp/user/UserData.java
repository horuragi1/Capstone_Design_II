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
    
    public BufferedImage previousImage;
    
    private final Lock imageLock = new ReentrantLock(); // 락 객체

    public UserData() {
        isConnected = false;
        ws = null;
        instance = 0;
        bitmap = null;
        BufferedImage image = null;
        
        previousImage = null;

    }
    
    public void setPreviousImage(BufferedImage previousImage) {
        imageLock.lock(); // 락을 획득
        try {
            this.previousImage = previousImage;
        } finally {
            imageLock.unlock(); // 락을 해제
        }
    }

    public BufferedImage getPreviousImage() {
        imageLock.lock();
        try {
            return previousImage;
        } finally {
            imageLock.unlock();
        }
    }

    public void setImage(BufferedImage image) {
        imageLock.lock();
        try {
            this.image = image;
        } finally {
            imageLock.unlock();
        }
    }

    public BufferedImage getImage() {
        imageLock.lock();
        try {
            return image;
        } finally {
            imageLock.unlock();
        }
    }
}