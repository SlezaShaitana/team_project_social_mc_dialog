package com.project.mc_dialog.service;

import com.project.mc_dialog.kafka.KafkaProducer;
import com.project.mc_dialog.kafka.dto.MicroServiceName;
import com.project.mc_dialog.kafka.dto.NotificationEvent;
import com.project.mc_dialog.kafka.dto.NotificationType;
import com.project.mc_dialog.mapper.Mapper;
import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.model.ReadStatus;
import com.project.mc_dialog.repository.MessageRepository;
import com.project.mc_dialog.utils.PageableUtils;
import com.project.mc_dialog.utils.SortUtils;
import com.project.mc_dialog.web.dto.Pageable;
import com.project.mc_dialog.web.dto.PageableObject;
import com.project.mc_dialog.web.dto.Sort;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import com.project.mc_dialog.web.dto.messageDto.PageMessageShortDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final DialogService dialogService;
    private final AuthenticationService authenticationService;
    private final Mapper mapper;
    private final KafkaProducer kafkaProducer;

    public void updateMessageStatus(UUID dialogId) {
        log.info("DialogService: updateMessageStatus() start method: dialogId - {}", dialogId);

        Dialog dialog = dialogService.getExistingDialog(dialogId);
        List<Message> messages = dialog.getMessages();
        messages.sort((m1, m2) -> m2.getTime().compareTo(m1.getTime()));
        for (Message message : messages) {
            if (message.getReadStatus().equals(ReadStatus.READ)) {
                break;
            }
            message.setReadStatus(ReadStatus.READ);
            messageRepository.save(message);
        }
    }

    public void createMessage(MessageDto messageDto) {
        log.info("DialogService: createMessage() start method: messageDto - {}", messageDto);

        Message newMessage = new Message();
        newMessage.setDeleted(false);
        newMessage.setConversationPartner1(messageDto.getConversationPartner1());
        newMessage.setConversationPartner2(messageDto.getConversationPartner2());
        newMessage.setReadStatus(ReadStatus.SENT);
        newMessage.setMessageText(messageDto.getMessageText());

        Dialog existingDialog = dialogService.getExistingDialog(messageDto.getDialogId());
        existingDialog.increaseUnreadCount();
        newMessage.setDialog(existingDialog);

        messageRepository.save(newMessage);
        log.info("DialogService: createMessage() end method: message created");

        NotificationEvent event = NotificationEvent.builder()
                .authorId(messageDto.getConversationPartner1())
                .receiverId(messageDto.getConversationPartner2())
                .content(messageDto.getMessageText())
                .notificationType(NotificationType.MESSAGE)
                .sentTime(LocalDateTime.now())
                .serviceName(MicroServiceName.DIALOG)
                .eventId(newMessage.getId())
                .isReaded(false)
                .build();
        kafkaProducer.sendNotificationMessage(event);
        log.info("DialogService: createMessage() end method: message send to notification microservice");
    }

    public PageMessageShortDto getMessages(UUID recipientId, Pageable pageableDto) {
        UUID ownerId = authenticationService.getCurrentUserId();
        log.info("DialogService: getMessages start method: userId - {}, recipientId - {}, pageableDto - {}",
                ownerId, recipientId, pageableDto);

        org.springframework.data.domain.Sort sort = SortUtils.getSortFromList(pageableDto.getSort());
        org.springframework.data.domain.Pageable pageable = PageableUtils.getPageable(pageableDto, sort);

        Page<Message> messagePage = messageRepository.findAllByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(ownerId,
                recipientId, recipientId, ownerId, pageable);
        List<Message> messages = messagePage.getContent();
        int totalPages = messagePage.getTotalPages();
        long totalElements = messagePage.getTotalElements();
        int numberOfElements = messagePage.getNumberOfElements();
        Sort sortDto = Sort.builder()
                .sorted(sort.isSorted())
                .unsorted(sort.isUnsorted())
                .empty(sort.isEmpty())
                .build();
        PageableObject pageableObject = PageableObject.builder()
                .sort(sortDto)
                .unpaged(pageable.isUnpaged())
                .paged(pageable.isPaged())
                .pageSize(pageable.getPageSize())
                .pageNumber(pageable.getPageNumber())
                .offset((int) pageable.getOffset())
                .build();
        boolean isFirst = messagePage.isFirst();
        boolean isLast = messagePage.isLast();
        int size = messages.size();
        int number = messagePage.getNumber();
        boolean empty = messagePage.isEmpty();

        return PageMessageShortDto.builder()
                .totalPages(totalPages)
                .totalElements(totalElements)
                .sort(sortDto)
                .numberOfElements(numberOfElements)
                .pageable(pageableObject)
                .first(isFirst)
                .last(isLast)
                .size(size)
                .content(mapper.messageListToMessageShortDtoList(messages))
                .number(number)
                .empty(empty)
                .build();
    }

}
