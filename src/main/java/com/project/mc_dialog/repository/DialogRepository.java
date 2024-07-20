package com.project.mc_dialog.repository;

import com.project.mc_dialog.model.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DialogRepository extends JpaRepository<Dialog, UUID> {

    Optional<Dialog> findByConversationPartner1AndConversationPartner2(UUID conversationPartner1, UUID conversationPartner2);
}
