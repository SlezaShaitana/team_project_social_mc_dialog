package com.project.mc_dialog.web.dto.messageDto;

import com.project.mc_dialog.model.ReadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
