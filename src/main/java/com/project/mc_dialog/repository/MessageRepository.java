package com.project.mc_dialog.repository;

import com.project.mc_dialog.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findAllByConversationPartner1(UUID conversationPartner1, Pageable pageable);
}
