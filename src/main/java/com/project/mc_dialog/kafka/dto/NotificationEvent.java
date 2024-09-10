package com.project.mc_dialog.kafka.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationEvent {

    private UUID uuid;

    private UUID authorId;

    private UUID receiverId;

    private String content;

    private NotificationType notificationType;

    private LocalDateTime sentTime;

    private MicroServiceName serviceName;

    private UUID eventId;

    private Boolean isReaded;
}
