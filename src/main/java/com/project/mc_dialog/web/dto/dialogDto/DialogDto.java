package com.project.mc_dialog.web.dto.dialogDto;

import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DialogDto {

    private UUID id;

    private boolean isDeleted;

    private Integer unreadCount;

    private UUID conversationPartner1;

    private UUID conversationPartner2;

    private List<MessageDto> lastMessage;
}
