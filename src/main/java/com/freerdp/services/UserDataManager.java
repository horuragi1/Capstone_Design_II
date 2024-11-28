package com.freerdp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.freerdp.controller.LoginController;
import com.freerdp.user.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.awt.image.BufferedImage;

public class UserDataManager {
    private static final Logger logger = LoggerFactory.getLogger(UserDataManager.class);
    final static int MAX_CLIENT_COUNT = 5;
    static UserData[] userData = new UserData[MAX_CLIENT_COUNT];

    public static void CreateFreeRDPInstances() {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            userData[i] = new UserData();
            userData[i].instance = LibFreeRDP.newInstance();
            logger.info("User{} instance: {}", i, userData[i].instance);
        }
        logger.info("CreateFreeRDPInstances");
    }

    public static void DeleteFreeRDPInstances() {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++)
            LibFreeRDP.freeInstance(userData[i].instance);
        logger.info("DeleteFreeRDPInstances");
    }

    public static boolean login(WebSocketSession session, String args) {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            if (!userData[i].isConnected) {
                logger.info("User{} Select, {}", i, userData[i].instance);
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
                userData[i].image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                return true;
            }
        }
        return false;
    }

    public static boolean logout(WebSocketSession session) {
        for(int i = 0; i < MAX_CLIENT_COUNT; i++) {
            if(userData[i].isConnected && (userData[i].ws.getId().equals(session.getId()))) {
                userData[i].isConnected = false;
                userData[i].ws = null;
                if(LibFreeRDP.disconnect(userData[i].instance))
                    logger.info("disconnect success");
                else
                    logger.info("disconnect fail");
            }
        }
        return true;
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
}
