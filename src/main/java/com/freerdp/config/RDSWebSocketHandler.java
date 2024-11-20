package com.freerdp.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freerdp.services.LibFreeRDP;
import com.freerdp.user.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class RDSWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(RDSWebSocketHandler.class);
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public RDSWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserData.ws = session;
        super.afterConnectionEstablished(session);

        /*
        scheduler.scheduleAtFixedRate(() -> {
            try {
                byte[] bitmapData = LibFreeRDP.createBitmapImage();
                if(bitmapData != null) {
                    session.sendMessage(new BinaryMessage(bitmapData));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
         */
    }

    /*
    * @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Client Connected: " + session.getId());

        scheduler.scheduleAtFixedRate(() -> {
            try {
                byte[] bitmapData = BitmapUtil.createBitmapImage(LocalTime.now().toString());
                if(bitmapData != null) {
                    System.out.println("전송 데이터 길이: " + bitmapData.length);
                    session.sendMessage(new BinaryMessage(bitmapData));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    *
    * */

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);
        String type = jsonNode.get("type").asText();

        if("keyboard".equals(type)){
            handleKeyboardEvent(jsonNode);
        } else if("mouse".equals(type)){
            handleMouseEvent(jsonNode);
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserData.ws = null;
        super.afterConnectionClosed(session, status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    private void handleKeyboardEvent(JsonNode jsonNode) {
        String action = jsonNode.get("action").asText();
        String code = jsonNode.get("code").asText();
        String key = jsonNode.get("key").asText();

        /*logger.info("{}", action);
        logger.info("{}", code);*/
    }

    private void handleMouseEvent(JsonNode jsonNode) {
        String action = jsonNode.get("action").asText();
        int x = jsonNode.get("x").asInt();
        int y = jsonNode.get("y").asInt();
        int button = jsonNode.has("button") ? jsonNode.get("button").asInt() : -1;

        /*logger.info(action);
        logger.info("{}", x);
        logger.info("{}", y);
        logger.info("{}", button);*/
    }
}
