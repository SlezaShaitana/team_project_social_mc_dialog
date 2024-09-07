package com.project.mc_dialog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dialogs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dialog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "unread_count")
    private Integer unreadCount;

    @Column(name = "conversation_partner_1", nullable = false)
    private UUID conversationPartner1;

    @Column(name = "conversation_partner_2", nullable = false)
    private UUID conversationPartner2;

    @OneToMany(mappedBy = "dialog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    public void increaseUnreadCount() {
        unreadCount++;
    }
}
