package com.freerdp.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freerdp.services.InputMapper;
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

import java.awt.event.KeyEvent;

public class RDSWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(RDSWebSocketHandler.class);
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static int mouseState = InputMapper.MOUSE_HOLD;
    public RDSWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        UserData.ws = session;
        super.afterConnectionEstablished(session);
    }

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
        boolean isDown = false;
        int virtualCode = InputMapper.KeyboardEventToVirtualcode(key);

        if('0' <= virtualCode && virtualCode <= '9' && key.charAt(0) == 'N')
            virtualCode += 0x30;

        if(action.equals("keydown")) {
            isDown = true;
            //logger.info("action {} -> code: {}, key: {}, unicodeChar: {}", action, code, key, virtualCode);
        }

        LibFreeRDP.send_key_event(UserData.instance, virtualCode, isDown);
    }

    private void handleMouseEvent(JsonNode jsonNode) {
        String action = jsonNode.get("action").asText();
        int x = jsonNode.get("x").asInt();
        int y = jsonNode.get("y").asInt();
        int button = jsonNode.has("button") ? jsonNode.get("button").asInt() : -1;

        if(action.equals("mousemove"))
            mouseState = InputMapper.MOUSE_MOVE;
        else if (action.equals("mouseup"))
            mouseState = InputMapper.MOUSE_UP;
        else if (action.equals("mousedown"))
            mouseState = InputMapper.MOUSE_DOWN;
        else
            mouseState = InputMapper.MOUSE_HOLD;

        //logger.info("action {} -> (x, y): ({}, {}), button: {}", action, x, y, button);

       if(!LibFreeRDP.send_cursor_event(UserData.instance, x, y, button, mouseState))
           logger.info("FAIL: mouse event");
    }
}
