package com.project.mc_dialog.service;

import com.project.mc_dialog.exception.DialogNotFoundException;
import com.project.mc_dialog.mapper.DialogMapper;
import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.repository.DialogRepository;
import com.project.mc_dialog.repository.MessageRepository;
import com.project.mc_dialog.utils.SortUtils;
import com.project.mc_dialog.web.dto.*;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.dialogDto.PageDialogDto;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import com.project.mc_dialog.web.dto.messageDto.PageMessageShortDto;
import com.project.mc_dialog.web.dto.messageDto.ReadStatus;
import com.project.mc_dialog.web.dto.messageDto.UnreadCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DialogService {

    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final DialogMapper dialogMapper;

    public void updateMessageStatus(UUID dialogId) {
        log.info("Updating message status for dialog {}", dialogId);
        Dialog dialog = getExistingDialog(dialogId);
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

    public MessageDto createMessage(MessageDto messageDto) {
        log.info("Creating message: {}", messageDto);
        Message newMessage = new Message();
        newMessage.setDeleted(false);
        newMessage.setConversationPartner1(messageDto.getConversationPartner1());
        newMessage.setConversationPartner2(messageDto.getConversationPartner2());
        newMessage.setReadStatus(ReadStatus.SENT);
        newMessage.setMessageText(messageDto.getMessageText());

        Dialog existingDialog = getExistingDialog(messageDto.getDialogId());
        existingDialog.setUnreadCount(existingDialog.getUnreadCount() + 1);
        newMessage.setDialog(existingDialog);

        return dialogMapper.messageToDto(messageRepository.save(newMessage));
    }

    public DialogDto createDialog(DialogDto dialogDto) {
        log.info("Creating dialog: {}", dialogDto);
        Dialog existingDialog = getExistingDialog(dialogDto.getConversationPartner1(), dialogDto.getConversationPartner2());
        if (existingDialog != null){
            return dialogMapper.dialogToDto(existingDialog);
        }
        Dialog newDialog = new Dialog();
        newDialog.setDeleted(false);
        newDialog.setUnreadCount(0);
        newDialog.setConversationPartner1(dialogDto.getConversationPartner1());
        newDialog.setConversationPartner2(dialogDto.getConversationPartner2());

        return dialogMapper.dialogToDto(dialogRepository.save(newDialog));
    }

    public PageDialogDto getDialogs(Pageable pageableDto) {
        log.info("Getting user dialogs");
        UUID ownerId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

        org.springframework.data.domain.Sort sort = SortUtils.getSortFromList(pageableDto.getSort());
        org.springframework.data.domain.Pageable pageable = PageRequest.of(pageableDto.getPage(),
                pageableDto.getSize(), sort);

        Page<Dialog> dialogPage = dialogRepository.findAllByConversationPartner1(ownerId, pageable);
        List<Dialog> dialogs = dialogPage.getContent();
        int totalPages = dialogPage.getTotalPages();
        int totalElements = dialogPage.getNumberOfElements();
        int numberOfElements = dialogPage.getNumberOfElements();
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
        boolean isFirst = dialogPage.isFirst();
        boolean isLast = dialogPage.isLast();
        int size = dialogs.size();
        int number = dialogPage.getNumber();
        boolean empty = dialogPage.isEmpty();

        return PageDialogDto.builder()
                .totalPages(totalPages)
                .totalElements(totalElements)
                .sort(sortDto)
                .numberOfElements(numberOfElements)
                .pageable(pageableObject)
                .first(isFirst)
                .last(isLast)
                .size(size)
                .content(dialogMapper.dialogListToDialogDtoList(dialogs))
                .number(number)
                .empty(empty)
                .build();
    }

    public UnreadCountDto getUnreadCount() {
        log.info("Getting unread count messages by user {}", "testUser");
        UUID ownerId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        List<Dialog> userDialogs = dialogRepository.findAllByConversationPartner1(ownerId);
        if (userDialogs.isEmpty()) {
            return new UnreadCountDto(0);
        }
        int count = userDialogs.stream().mapToInt(Dialog::getUnreadCount).sum();
        return new UnreadCountDto(count);
    }

    public DialogDto getDialog(UUID recipientId) {
        log.info("Getting dialog by user {} with user {}", "userTest", recipientId);
        UUID ownerId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
        return dialogMapper.dialogToDto(getExistingDialog(ownerId, recipientId));
    }

    public PageMessageShortDto getMessages(UUID recipientId, Pageable pageableDto) {
        log.info("Getting messages by user {} with user {}", "userTest", recipientId);
        UUID ownerId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");

        org.springframework.data.domain.Sort sort = SortUtils.getSortFromList(pageableDto.getSort());
        org.springframework.data.domain.Pageable pageable = PageRequest.of(pageableDto.getPage(),
                pageableDto.getSize(), sort);

        Page<Message> messagePage = messageRepository.findAllByConversationPartner1(ownerId, pageable);
        List<Message> messages = messagePage.getContent();
        int totalPages = messagePage.getTotalPages();
        int totalElements = messagePage.getNumberOfElements();
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
                .content(dialogMapper.messageListToMessageShortDtoList(messages))
                .number(number)
                .empty(empty)
                .build();
    }

    private Dialog getExistingDialog(UUID dialogId) {
        log.info("Get existing dialog by dialogId {}", dialogId);
        return dialogRepository.findById(dialogId)
                .orElseThrow(() -> new DialogNotFoundException(MessageFormat.format("Dialog with id {0} not found", dialogId)));
    }

    private Dialog getExistingDialog(UUID conversationPartner1, UUID conversationPartner2){
        log.info("Get existing dialog by users ids {}, {}", conversationPartner1, conversationPartner2);
        return dialogRepository.findByConversationPartner1AndConversationPartner2(conversationPartner1, conversationPartner2)
                .orElse(null);

    }
}
