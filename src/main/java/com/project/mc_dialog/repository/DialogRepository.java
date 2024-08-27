package com.project.mc_dialog.repository;

import com.project.mc_dialog.model.Dialog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DialogRepository extends JpaRepository<Dialog, UUID> {

    Optional<Dialog> findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(UUID conversationPartner1,
                                                                                                                       UUID conversationPartner2,
                                                                                                                       UUID conversationPartner2Reverse,
                                                                                                                       UUID conversationPartner1Reverse
                                                                                                                       );

    Page<Dialog> findAllByConversationPartner1OrConversationPartner2(UUID conversationPartner1, UUID conversationPartner2, Pageable pageable);

    List<Dialog> findAllByConversationPartner1OrConversationPartner2(UUID conversationPartner1, UUID conversationPartner1Reverse);
}
