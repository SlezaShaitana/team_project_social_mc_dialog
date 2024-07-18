package com.project.mc_dialog.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DialogDto {

    private UUID id;

    private boolean isDeleted;

    private Integer unreadCount;

    private UUID conversationPartner1;

    private UUID conversationPartner2;

    private MessageDto lastMessage;
}
