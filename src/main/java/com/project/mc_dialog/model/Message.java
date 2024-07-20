package com.project.mc_dialog.model;

import com.project.mc_dialog.web.dto.messageDto.ReadStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime time;

    @Column(name = "conversation_partner_1", nullable = false)
    private UUID conversationPartner1;

    @Column(name = "conversation_partner_2", nullable = false)
    private UUID conversationPartner2;

    @Column(name = "message_text", columnDefinition = "TEXT")
    private String messageText;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "read_status")
    private ReadStatus readStatus;

    @ManyToOne
    @JoinColumn(name = "dialog_id")
    private Dialog dialog;

}
