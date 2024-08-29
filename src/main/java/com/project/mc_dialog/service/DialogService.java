package com.project.mc_dialog.service;

import com.project.mc_dialog.exception.DialogNotFoundException;
import com.project.mc_dialog.mapper.Mapper;
import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.repository.DialogRepository;
import com.project.mc_dialog.repository.MessageRepository;
import com.project.mc_dialog.security.JwtUtils;
import com.project.mc_dialog.utils.SortUtils;
import com.project.mc_dialog.web.dto.*;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.dialogDto.PageDialogDto;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import com.project.mc_dialog.web.dto.messageDto.PageMessageShortDto;
import com.project.mc_dialog.model.ReadStatus;
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

    private final AuthenticationService authenticationService;
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final Mapper mapper;
    private final JwtUtils jwtUtils;

    public void updateMessageStatus(UUID dialogId) {
        log.info("DialogService: updateMessageStatus() start method: dialogId - {}", dialogId);

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
        log.info("DialogService: createMessage() start method: messageDto - {}", messageDto);

        Message newMessage = new Message();
        newMessage.setDeleted(false);
        newMessage.setConversationPartner1(messageDto.getConversationPartner1());
        newMessage.setConversationPartner2(messageDto.getConversationPartner2());
        newMessage.setReadStatus(ReadStatus.SENT);
        newMessage.setMessageText(messageDto.getMessageText());

        Dialog existingDialog = getExistingDialog(messageDto.getDialogId());
        existingDialog.increaseUnreadCount();
        newMessage.setDialog(existingDialog);

        return mapper.messageToDto(messageRepository.save(newMessage));
    }

    public DialogDto createDialog(DialogDto dialogDto) {
        log.info("DialogService: createDialog() start method: dialogDto - {}", dialogDto);

        Dialog existingDialog = getExistingDialog(dialogDto.getConversationPartner1(), dialogDto.getConversationPartner2());
        if (existingDialog != null){
            return mapper.dialogToDto(existingDialog);
        }
        Dialog newDialog = new Dialog();
        newDialog.setDeleted(false);
        newDialog.setUnreadCount(0);
        newDialog.setConversationPartner1(dialogDto.getConversationPartner1());
        newDialog.setConversationPartner2(dialogDto.getConversationPartner2());

        return mapper.dialogToDto(dialogRepository.save(newDialog));
    }

    public PageDialogDto getDialogs(String token, Pageable pageableDto) {
        log.info("DialogService: getDialogs start method: token - {}, pageableDto - {}",
                token, pageableDto);
        UUID ownerId = UUID.fromString(jwtUtils.getId(token));

        org.springframework.data.domain.Sort sort = SortUtils.getSortFromList(pageableDto.getSort());
        org.springframework.data.domain.Pageable pageable = getPageable(pageableDto, sort);

        Page<Dialog> dialogPage = dialogRepository.findAllByConversationPartner1OrConversationPartner2(ownerId, ownerId, pageable);
        List<Dialog> dialogs = dialogPage.getContent();
        int totalPages = dialogPage.getTotalPages();
        long totalElements = dialogPage.getTotalElements();
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
                .content(mapper.dialogListToDialogDtoList(dialogs))
                .number(number)
                .empty(empty)
                .build();
    }

    public UnreadCountDto getUnreadCount(String token) {
        log.info("DialogService: getUnreadCount start method: token - {}", token);

        UUID ownerId = authenticationService.getCurrentUserId();
        List<Dialog> userDialogs = dialogRepository.findAllByConversationPartner1OrConversationPartner2(ownerId, ownerId);
        if (userDialogs.isEmpty()) {
            return new UnreadCountDto(0);
        }
        int count = userDialogs.stream().mapToInt(Dialog::getUnreadCount).sum();
        return new UnreadCountDto(count);
    }

    public DialogDto getDialog(String token, UUID recipientId) {
        log.info("DialogService: getDialog start method: token - {}, recipientId - {}",
                token, recipientId);

        UUID ownerId = UUID.fromString(jwtUtils.getId(token));
        Dialog exsitingDialog = getExistingDialog(ownerId, recipientId);
        if (exsitingDialog != null) {
            return mapper.dialogToDto(exsitingDialog);
        } else {
            return createDialog(DialogDto.builder()
                    .conversationPartner1(ownerId)
                    .conversationPartner2(recipientId)
                    .build());
        }
    }

    public PageMessageShortDto getMessages(String token, UUID recipientId, Pageable pageableDto) {
        log.info("DialogService: getMessages start method: token - {}, recipientId - {}, pageableDto - {}",
                token, recipientId, pageableDto);

        UUID ownerId = UUID.fromString(jwtUtils.getId(token));

        org.springframework.data.domain.Sort sort = SortUtils.getSortFromList(pageableDto.getSort());
        org.springframework.data.domain.Pageable pageable = getPageable(pageableDto, sort);

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

    private Dialog getExistingDialog(UUID dialogId) {
        log.info("DialogService: getExistingDialog start method: dialogId - {}", dialogId);

        return dialogRepository.findById(dialogId)
                .orElseThrow(() -> new DialogNotFoundException(MessageFormat.format("Dialog with id {0} not found", dialogId)));
    }

    private Dialog getExistingDialog(UUID conversationPartner1, UUID conversationPartner2){
        log.info("DialogService: getExistingDialog start method: conversationPartner1 - {}, conversationPartner2 - {}",
                conversationPartner1, conversationPartner2);

        return dialogRepository.findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(conversationPartner1,
                        conversationPartner2,
                        conversationPartner2,
                        conversationPartner1)
                .orElse(null);
    }

    private static org.springframework.data.domain.Pageable getPageable(Pageable pageableDto, org.springframework.data.domain.Sort sort) {
        org.springframework.data.domain.Pageable pageable;

        if (pageableDto.getSize() == null && pageableDto.getPage() == null) {
            pageable = PageRequest.of(0,10, sort);
        } else if (pageableDto.getSize() == null) {
            pageable = PageRequest.of(pageableDto.getPage(), 10, sort);
        } else if (pageableDto.getPage() == null) {
            pageable = PageRequest.of(0, pageableDto.getSize(), sort);
        } else {
            pageable = PageRequest.of(pageableDto.getPage(),
                    pageableDto.getSize(), sort);
        }
        return pageable;
    }
}
