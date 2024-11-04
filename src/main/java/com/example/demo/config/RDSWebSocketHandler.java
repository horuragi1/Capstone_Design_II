package com.example.demo.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class RDSWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    public RDSWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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

        System.out.println(action);
        System.out.println(code);
    }

    private void handleMouseEvent(JsonNode jsonNode) {
        String action = jsonNode.get("action").asText();
        int x = jsonNode.get("x").asInt();
        int y = jsonNode.get("y").asInt();
        int button = jsonNode.has("button") ? jsonNode.get("button").asInt() : -1;

        System.out.println(action);
        System.out.println(x);
        System.out.println(y);
        System.out.println(button);
    }
}
