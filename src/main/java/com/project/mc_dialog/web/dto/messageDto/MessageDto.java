package com.project.mc_dialog.web.dto.messageDto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageDto {

    private UUID id;

    private boolean isDeleted;

    private LocalDateTime time;

    private UUID conversationPartner1;

    private UUID conversationPartner2;

    private String messageText;

    private ReadStatus readStatus;

    private UUID dialogId;
}
