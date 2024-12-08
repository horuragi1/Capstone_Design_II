package com.freerdp.services;

import com.freerdp.user.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.socket.WebSocketSession;

@Component
public class UserDataManager {
    private static final Logger logger = LoggerFactory.getLogger(UserDataManager.class);
    final static int MAX_CLIENT_COUNT = 5;
    static UserData[] userData = new UserData[MAX_CLIENT_COUNT];
    public static StopWatch stopWatch = new StopWatch();
    public static void CreateFreeRDPInstances() {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            userData[i] = new UserData();
            userData[i].instance = LibFreeRDP.newInstance();
            logger.info("Create Instance of User{}({})", i, userData[i].instance);
        }
        logger.info("CreateFreeRDPInstances");
    }

    public static void DeleteFreeRDPInstances() {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            if(userData[i].isConnected) {
                userData[i].isConnected = false;
                userData[i].ws = null;
                if(LibFreeRDP.disconnect(userData[i].instance))
                    logger.info("User{} disconnect success", i);
                else
                    logger.info("User{} disconnect fail", i);
            }

            LibFreeRDP.freeInstance(userData[i].instance);
            userData[i].instance = 0;
            logger.info("Delete Instance of User{}", i);
        }
        logger.info("DeleteFreeRDPInstances");
    }

    public static boolean login(WebSocketSession session, String args) {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            if (!userData[i].isConnected) {
                logger.info("User{} Login, {}", i, userData[i].instance);
                logger.info("input args: {}", args);
                if (!LibFreeRDP.parseArgs(userData[i].instance, args)) {
                    logger.info("Parse Args Fail");
                    return false;
                }
                logger.info("Parse Args Success");

                if (!LibFreeRDP.connect(userData[i].instance)) {
                    logger.info("Connect Fail");
                    return false;
                }

                logger.info("Connect Success");
                userData[i].isConnected = true;
                userData[i].ws = session;

                int width = LibFreeRDP.get_width(userData[i].instance);
                int height = LibFreeRDP.get_height(userData[i].instance);
                userData[i].bitmap = new byte[4 * width * height];

                return true;
            }
        }
        return false;
    }

    public static boolean logout(WebSocketSession session) {
        boolean ret = true;
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            if(userData[i].isConnected && (userData[i].ws.getId().equals(session.getId()))) {
                if(userData[i].instance == 0)
                    return true;
                logger.info("User{} Logout, {}", i, userData[i].instance);
                userData[i].initBitmapUpdateData();
                userData[i].isConnected = false;
                userData[i].ws = null;
                if(LibFreeRDP.disconnect(userData[i].instance))
                    logger.info("disconnect success");
                else {
                    logger.info("disconnect fail");
                    ret = false;
                }
            }
        }
        return ret;
    }

    public static long getInstance(WebSocketSession session) {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            if(userData[i].isConnected && (userData[i].ws.getId().equals(session.getId()))) {
                if(userData[i].instance == 0)
                    userData[i].instance = LibFreeRDP.newInstance();
                return userData[i].instance;
            }
        }
        return 0;
    }

    public static UserData getUserDataOf(long instance) {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            if(userData[i].isConnected && (userData[i].instance == instance)) {
                return userData[i];
            }
        }
        return null;
    }

    public static void updateBitmap(long instance, int minX, int minY, int maxX, int maxY) {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            if(userData[i].isConnected && (userData[i].instance == instance)) {
                int idx = userData[i].bitmapIndex;
                if(minX < userData[i].minX[idx])
                    userData[i].minX[idx] = minX;
                if(minY < userData[i].minY[idx])
                    userData[i].minY[idx] = minY;
                if(userData[i].maxX[idx] < maxX)
                    userData[i].maxX[idx] = maxX;
                if(userData[i].maxY[idx] < maxY)
                    userData[i].maxY[idx] = maxY;
                userData[i].isUpdatedBitmap.set(true);
            }
        }
    }

    @Scheduled(fixedRate = 200)
    public static void sendBitmap() {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            if(userData[i].isConnected && userData[i].isUpdatedBitmap.get()) {
                stopWatch.start();
                int idx = userData[i].startSendBitmap();
                int w = (userData[i].maxX[idx] - userData[i].minX[idx] + 1);
                int h = (userData[i].maxY[idx] - userData[i].minY[idx] + 1);
                int size =  w * h * 3;
                LibFreeRDP.copy_bitmap(userData[i].instance, userData[i].bitmap, userData[i].minX[idx], userData[i].minY[idx],
                        userData[i].maxX[idx] - userData[i].minX[idx] + 1, userData[i].maxY[idx] - userData[i].minY[idx] + 1);
                int totalWidth = LibFreeRDP.get_width(userData[i].instance);
                LibFreeRDP.sendDelta(userData[i].ws, userData[i].minX[idx], userData[i].minY[idx], userData[i].maxX[idx], userData[i].maxY[idx], userData[i].bitmap, totalWidth);
                userData[i].endSendBitmap();
                stopWatch.stop();
                logger.info("Send Message (w, h, size) : ({}, {}, {}), time: {}", w, h, size, stopWatch.getLastTaskTimeMillis());
            }
        }
    }
}