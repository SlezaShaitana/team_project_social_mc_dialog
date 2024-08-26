package com.project.mc_dialog.websocket;

import com.project.mc_dialog.security.JwtUtils;
import com.project.mc_dialog.service.DialogService;
import com.project.mc_dialog.utils.MessageParseUtils;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
@Slf4j
@Service
public class WebSocketHandler extends TextWebSocketHandler {

    private final JwtUtils jwtUtils;
    private final DialogService messageService;
//    private final AuthenticationService authenticationService;

    List<WebSocketSession> webSocketSessions
            = Collections.synchronizedList(new ArrayList<>());
    //ConcurrentMap<UUID, List<WebSocketSession>> sessionMap = new ConcurrentHashMap<>();
    public static final String TYPE_NOTIFICATION = "NOTIFICATION";
    public static final String TYPE_MESSAGE = "MESSAGE";

//    private KafkaDialogServiceImpl kafkaMessageService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocketHandler: afterConnectionEstablished() startMethod: sessionId: {}, sessionMap: {}",
                session.getId(), webSocketSessions);

        //UUID uuid = getCurrentUserId(session);
        session.sendMessage(new TextMessage("{ \"connection\": \"established\"}"));
//        List<WebSocketSession> list = sessionMap.getOrDefault(uuid, new ArrayList<>());//поменять на нормальный uuid
        webSocketSessions.add(session);

//        boolean isNew = list.isEmpty();
//        list.add(session);
//        if (isNew) {
//            sessionMap.put(uuid, list);
//        } else {
//            sessionMap.replace(uuid, list);
//        }

//не сделано         kafkaMessageService.sendAccountDTO(notificationsMapper.getAccountOnlineDto(uuid, true));

        log.info("WebSocketHandler: afterConnectionEstablished(): итоговый для id: {} sessionMap: {}",
                session.getId(), webSocketSessions);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("WebSocketHandler: handleTextMessage() startMethod: получен TextMessage: {}", message.getPayload());
        UUID uuid = null;
        log.info("Handle new message from " + uuid);

        JSONObject messageJSON = new JSONObject(message.getPayload());
        if (TYPE_MESSAGE.equals(messageJSON.getString("type"))) {
            uuid = UUID.fromString(messageJSON.getString("recipientId"));

            MessageDto messageDto = MessageParseUtils.parseMessage(message);
            messageService.createMessage(messageDto);
            log.info("Сервис обработал сообщение и отправил в бд");
        }


        for (WebSocketSession webSocketSession :
                webSocketSessions) {
            if (session == webSocketSession)
                continue;

            // sendMessage is used to send the message to
            // the session
            webSocketSession.sendMessage(message);
        }
    }

    private void SendingList(TextMessage message, UUID id) throws IOException {
        log.info("Session map");

        log.info("Sending List. Направить сообщения по веб-сокету получателю - " + id);
//        List<WebSocketSession> sendingList = sessionMap.getOrDefault(id, new ArrayList<>());
//        for (WebSocketSession registerSession : sendingList) {
//            log.info("Отправка сообщения для получателя с номером сессии" + registerSession.getId());
//            registerSession.sendMessage(message);
//        }


    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocketHandler: afterConnectionClosed() startMethod: sessionId: {}, closeStatus: {}",
                session.getId(), status);
        //UUID uuid = getCurrentUserId(session);
//        List<WebSocketSession> list = sessionMap.getOrDefault(uuid, new ArrayList<>());
//        list.remove(session);
//        sessionMap.replace(uuid, list);

        webSocketSessions.remove(session);
    }

    private UUID getCurrentUserId(WebSocketSession session) {
//        Principal principalSession = session.getPrincipal();
//        String accountIdString = (String) ((UsernamePasswordAuthenticationToken) principalSession).getPrincipal();
//        return UUID.fromString(accountIdString);
        String header = session.getHandshakeHeaders().get("Authorization").get(0);
        String token = header.substring(7);

        return UUID.fromString(jwtUtils.getId(token));
    }
}