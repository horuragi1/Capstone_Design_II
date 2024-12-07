package com.freerdp.user;

import org.springframework.web.socket.WebSocketSession;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UserData {
    public boolean isConnected;
    public AtomicBoolean isUpdatedBitmap = new AtomicBoolean();
    public int bitmapIndex;
    public WebSocketSession ws;
    public long instance;
    public byte[] bitmap;
    public int[] minX = new int[2];
    public int[] minY = new int[2];
    public int[] maxX = new int[2];
    public int[] maxY = new int[2];

    private final Lock lock = new ReentrantLock(); // 락 객체

    public UserData() {
        isConnected = false;
        ws = null;
        instance = 0;
        bitmap = null;
        initBitmapUpdateData();
    }

    public void initBitmapUpdateData() {
        minX[0] = minX[1] = Integer.MAX_VALUE;
        minY[0] = minY[1] = Integer.MAX_VALUE;
        maxX[0] = maxX[1] = Integer.MIN_VALUE;
        maxY[0] = maxY[1] = Integer.MIN_VALUE;
        bitmapIndex = 0;
        isUpdatedBitmap.set(false);
    }
    public int startSendBitmap() {
        lock.lock();
        try {
            isUpdatedBitmap.set(false);
            bitmapIndex = bitmapIndex == 0 ? 1 : 0;
            return bitmapIndex == 0 ? 1 : 0;
        } finally {
            lock.unlock();
        }
    }
    public void endSendBitmap() {
        int prevIndex = bitmapIndex == 0 ? 1 : 0;

        minX[prevIndex] = Integer.MAX_VALUE;
        minY[prevIndex] = Integer.MAX_VALUE;
        maxX[prevIndex] = Integer.MIN_VALUE;
        maxY[prevIndex] = Integer.MIN_VALUE;
    }
}