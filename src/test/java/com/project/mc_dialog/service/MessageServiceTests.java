package com.project.mc_dialog.service;

import com.project.mc_dialog.kafka.KafkaProducer;
import com.project.mc_dialog.mapper.Mapper;
import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.model.ReadStatus;
import com.project.mc_dialog.repository.MessageRepository;
import com.project.mc_dialog.testContainer.PostgresContainer;
import com.project.mc_dialog.web.dto.Pageable;
import com.project.mc_dialog.web.dto.PageableObject;
import com.project.mc_dialog.web.dto.Sort;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import com.project.mc_dialog.web.dto.messageDto.PageMessageShortDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Tests for MessageService")
public class MessageServiceTests  extends PostgresContainer {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private DialogService dialogService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private MessageService messageService;

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test updateMessageStatus")
    public void testUpdateMessageStatus() {
        UUID dialogId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        Message message1 = new Message();
        Message message2 = new Message();
        message1.setReadStatus(ReadStatus.SENT);
        message1.setTime(LocalDateTime.now());
        message2.setReadStatus(ReadStatus.SENT);
        message2.setTime(LocalDateTime.now().minusSeconds(2));

        List<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);
        Dialog dialog = Dialog.builder().id(dialogId).messages(messages).build();

        when(dialogService.getExistingDialog(dialogId)).thenReturn(dialog);

        messageService.updateMessageStatus(dialogId);

        assertEquals(ReadStatus.READ, message1.getReadStatus());
        assertEquals(ReadStatus.READ, message2.getReadStatus());
        verify(messageRepository, times(2)).save(any(Message.class));

    }

    @Test
    @DisplayName("Test createMessage")
    public void testCreateMessage() {
        UUID conversationPartner1 = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        UUID conversationPartner2 = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa2");

        MessageDto messageDto = MessageDto.builder()
                .conversationPartner1(conversationPartner1)
                .conversationPartner2(conversationPartner2)
                .messageText("Test message")
                .build();
        Dialog dialog = Dialog.builder()
                .unreadCount(0)
                .build();

        when(dialogService.getExistingDialog(dialog.getId())).thenReturn(dialog);

        messageService.createMessage(messageDto);

        assertEquals(1, dialog.getUnreadCount());
        verify(messageRepository, times(1)).save(any(Message.class));

    }

    @Test
    @DisplayName("Test getMessages")
    public void testGetMessages() {
        UUID ownerId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        UUID recipientId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa1");
        Pageable pageableDto = Pageable.builder()
                .page(0)
                .size(5)
                .sort(null)
                .build();
        Sort sortDto = Sort.builder()
                .sorted(false)
                .unsorted(true)
                .empty(true)
                .build();
        PageableObject pageableObject = PageableObject.builder()
                .sort(sortDto)
                .unpaged(false)
                .paged(true)
                .pageSize(5)
                .pageNumber(0)
                .offset(0)
                .build();
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 5, org.springframework.data.domain.Sort.unsorted());

        List<Message> content = List.of(
                Message.builder().id(UUID.randomUUID()).build(),
                Message.builder().id(UUID.randomUUID()).build()
        );
        Page<Message> messagePage = new PageImpl<>(content);
        PageMessageShortDto pageMessageShortDto = PageMessageShortDto.builder()
                .totalPages(1)
                .totalElements(2L)
                .sort(sortDto)
                .numberOfElements(2)
                .pageable(pageableObject)
                .first(true)
                .last(true)
                .size(2)
                .content(mapper.messageListToMessageShortDtoList(content))
                .number(0)
                .empty(false)
                .build();

        when(authenticationService.getCurrentUserId()).thenReturn(ownerId);
        when(messageRepository.findAllByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(
                ownerId,
                recipientId,
                recipientId,
                ownerId,
                pageable))
                .thenReturn(messagePage);

        PageMessageShortDto result = messageService.getMessages(recipientId, pageableDto);

        assertEquals(pageMessageShortDto, result);
        verify(authenticationService, times(1)).getCurrentUserId();
        verify(messageRepository, times(1)).findAllByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(
                ownerId,
                recipientId,
                recipientId,
                ownerId,
                pageable);


    }


}
