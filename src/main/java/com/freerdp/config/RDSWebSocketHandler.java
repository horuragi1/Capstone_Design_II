package com.freerdp.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freerdp.services.InputMapper;
import com.freerdp.services.LibFreeRDP;
import com.freerdp.services.UserDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class RDSWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(RDSWebSocketHandler.class);
    private final ObjectMapper objectMapper;
    static int mouseState = InputMapper.MOUSE_HOLD;
    public RDSWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        logger.info("Connection Established");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);
        String type = jsonNode.get("type").asText();

        if("login".equals(type)) {
            handleLoginEvent(session, jsonNode);
        }
        else if("keyboard".equals(type)){
            handleKeyboardEvent(session, jsonNode);
        } else if("mouse".equals(type)){
            handleMouseEvent(session, jsonNode);
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        UserDataManager.logout(session);
        super.afterConnectionClosed(session, status);
        logger.info("Connection Closed");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }

    private void handleLoginEvent(WebSocketSession session, JsonNode jsonNode) {
        String user = jsonNode.get("user").asText();
        String pw = jsonNode.get("pw").asText();
        String ip = jsonNode.get("ip").asText();
        String args = String.format("./freerdp /u:%s /p:%s /v:%s", user, pw, ip);

        if(UserDataManager.login(session, args))
            logger.info("Login Success");
        else
            logger.info("Login Fail");
    }


    private void handleKeyboardEvent(WebSocketSession session, JsonNode jsonNode) {
        long freerdpInstance = UserDataManager.getInstance(session);
        if(freerdpInstance == 0)
            return;

        String action = jsonNode.get("action").asText();
        String code = jsonNode.get("code").asText();
        String key = jsonNode.get("key").asText();
        boolean isDown = false;
<<<<<<< Updated upstream
        int virtualCode = InputMapper.KeyboardEventToVirtualcode(key);
=======
        int virtualCode = InputMapper.KeyboardEventToScancode(key);
>>>>>>> Stashed changes

        if('0' <= virtualCode && virtualCode <= '9' && key.charAt(0) == 'N')
            virtualCode += 0x30;

        if(action.equals("keydown")) {
            isDown = true;
<<<<<<< Updated upstream
            //logger.info("action {} -> code: {}, key: {}, unicodeChar: {}", action, code, key, virtualCode);
        }

        LibFreeRDP.send_key_event(UserData.instance, virtualCode, isDown);
=======
            //logger.info("action {} -> code: {}, key: {}, virtualCode: {}", action, code, key, virtualCode);
        }

        LibFreeRDP.send_key_event(freerdpInstance, virtualCode, isDown);
>>>>>>> Stashed changes
    }

    private void handleMouseEvent(WebSocketSession session, JsonNode jsonNode) {
        long freerdpInstance = UserDataManager.getInstance(session);
        if(freerdpInstance == 0)
            return;

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

       if(!LibFreeRDP.send_cursor_event(freerdpInstance, x, y, button, mouseState))
           logger.info("FAIL: mouse event");
    }
}
