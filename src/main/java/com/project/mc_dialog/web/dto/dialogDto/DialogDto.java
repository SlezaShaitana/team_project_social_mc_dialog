package com.project.mc_dialog.web.dto.dialogDto;

import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DialogDto {

    private UUID id;

    private boolean isDeleted;

    private Integer unreadCount;

    private UUID conversationPartner1;

    private UUID conversationPartner2;

    private List<MessageDto> lastMessage;
}
