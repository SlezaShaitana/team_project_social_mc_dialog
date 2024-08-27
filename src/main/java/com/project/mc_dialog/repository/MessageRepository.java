package com.project.mc_dialog.repository;

import com.project.mc_dialog.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

//    @Query("SELECT m FROM Message m WHERE " +
//            "(:partner1 IN (m.conversationPartner1, m.conversationPartner2) AND " +
//            ":partner2 IN (m.conversationPartner1, m.conversationPartner2)) " +
//            "ORDER BY m.time DESC")
//    Page<Message> findMessages(@Param("partner1") UUID partner1,
//                               @Param("partner2") UUID partner2,
//                               Pageable pageable);


    Page<Message> findAllByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(UUID conversationPartner1,
                                                                                                                    UUID conversationPartner2,
                                                                                                                    UUID conversationPartner2Reverse,
                                                                                                                    UUID conversationPartner1Reverse,
                                                                                                                    Pageable pageable);
}
