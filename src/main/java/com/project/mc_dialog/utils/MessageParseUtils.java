package com.project.mc_dialog.utils;

import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import com.project.mc_dialog.model.ReadStatus;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class MessageParseUtils {

    public static MessageDto parseMessage(TextMessage message) {
        JSONObject request = new JSONObject(message.getPayload());
        JSONObject messageJson = (JSONObject) request.get("data");

        String time = messageJson.getString("time");
        String dateStringWithoutZ = time.substring(0, time.length() - 1);
        LocalDateTime dateTime = LocalDateTime.parse(dateStringWithoutZ, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        ReadStatus readStatus = !messageJson.get("readStatus").toString().equals("null")
                ? ReadStatus.valueOf(messageJson.getString("readStatus")) : null;

        return MessageDto.builder()
                .id(UUID.fromString(messageJson.getString("id")))
                .time(dateTime)
                .conversationPartner1(UUID.fromString(messageJson.getString("conversationPartner1")))
                .conversationPartner2(UUID.fromString(messageJson.getString("conversationPartner2")))
                .messageText(messageJson.getString("messageText"))
                .readStatus(readStatus)
                .dialogId(UUID.fromString(messageJson.getString("dialogId")))
                .build();
    }
}
