package com.project.mc_dialog.websocket;

import com.project.mc_dialog.kafka.KafkaProducer;
import com.project.mc_dialog.service.MessageService;
import com.project.mc_dialog.utils.MessageParseUtils;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {


    private final MessageService messageService;

    private final ConcurrentMap<UUID, List<WebSocketSession>> sessionMap= new ConcurrentHashMap<>();

    public static final String TYPE_MESSAGE = "MESSAGE";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocketHandler: afterConnectionEstablished() startMethod: sessionId: {}, sessionMap: {}",
                session.getId(), sessionMap);

        UUID uuid = getCurrentUserId(session);
        session.sendMessage(new TextMessage("{ \"connection\": \"established\"}"));
        List<WebSocketSession> list = sessionMap.getOrDefault(uuid, new ArrayList<>());//поменять на нормальный uuid
        boolean isNew = list.isEmpty();
        list.add(session);
        if (isNew) {
            sessionMap.put(uuid, list);
        } else {
            sessionMap.replace(uuid, list);
        }

        log.info("WebSocketHandler: afterConnectionEstablished(): итоговый для id: {} sessionMap: {}",
                session.getId(), sessionMap);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("WebSocketHandler: handleTextMessage() startMethod: получен TextMessage: {}", message.getPayload());

        UUID uuid = getCurrentUserId(session);
        log.info("Handle new message from " + uuid);

        if (TYPE_MESSAGE.equals(new JSONObject(message.getPayload()).getString("type"))) {
            uuid = UUID.fromString(new JSONObject(message.getPayload()).getString("recipientId"));

            MessageDto messageDto = MessageParseUtils.parseMessage(message);
            messageService.createMessage(messageDto);
            log.info("Сервис обработал сообщение и отправил в бд");
        }
        SendingList(message,uuid);
    }

    private void SendingList(TextMessage message,UUID id) throws IOException {
        log.info("Sending List. Направить сообщения по веб-сокету получателю - " + id);

        List<WebSocketSession> sendingList = sessionMap.getOrDefault(id, new ArrayList<>());
        for (WebSocketSession registerSession : sendingList) {
            log.info("Отправка сообщения для получателя с номером сессии" + registerSession.getId());
            registerSession.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UUID uuid = getCurrentUserId(session);
        List<WebSocketSession> list = sessionMap.getOrDefault(uuid, new ArrayList<>());
        list.remove(session);
        sessionMap.replace(uuid, list);
    }

    private UUID getCurrentUserId(WebSocketSession session) {
        Principal principalSession = session.getPrincipal();
        String accountIdString = (String) ((UsernamePasswordAuthenticationToken) principalSession).getPrincipal();
        return UUID.fromString(accountIdString);
    }

}