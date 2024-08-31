package com.project.mc_dialog.service;

import com.project.mc_dialog.exception.DialogNotFoundException;
import com.project.mc_dialog.mapper.Mapper;
import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.repository.DialogRepository;
import com.project.mc_dialog.utils.PageableUtils;
import com.project.mc_dialog.utils.SortUtils;
import com.project.mc_dialog.web.dto.*;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.dialogDto.PageDialogDto;
import com.project.mc_dialog.web.dto.messageDto.UnreadCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final Mapper mapper;

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

    public PageDialogDto getDialogs(Pageable pageableDto) {
        UUID ownerId = authenticationService.getCurrentUserId();
        log.info("DialogService: getDialogs start method: userId - {}, pageableDto - {}",
                ownerId, pageableDto);

        org.springframework.data.domain.Sort sort = SortUtils.getSortFromList(pageableDto.getSort());
        org.springframework.data.domain.Pageable pageable = PageableUtils.getPageable(pageableDto, sort);

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

    public UnreadCountDto getUnreadCount() {
        UUID ownerId = authenticationService.getCurrentUserId();
        log.info("DialogService: getUnreadCount start method: userId - {}", ownerId);

        List<Dialog> userDialogs = dialogRepository.findAllByConversationPartner1OrConversationPartner2(ownerId, ownerId);
        if (userDialogs.isEmpty()) {
            return new UnreadCountDto(0);
        }
        int count = userDialogs.stream().mapToInt(Dialog::getUnreadCount).sum();
        return new UnreadCountDto(count);
    }

    public DialogDto getDialog(UUID recipientId) {
        UUID ownerId = authenticationService.getCurrentUserId();
        log.info("DialogService: getDialog start method: userId - {}, recipientId - {}",
                ownerId, recipientId);

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

    public Dialog getExistingDialog(UUID dialogId) {
        log.info("DialogService: getExistingDialog start method: dialogId - {}", dialogId);

        return dialogRepository.findById(dialogId)
                .orElseThrow(() -> new DialogNotFoundException(MessageFormat.format("Dialog with id {0} not found", dialogId)));
    }

    public Dialog getExistingDialog(UUID conversationPartner1, UUID conversationPartner2){
        log.info("DialogService: getExistingDialog start method: conversationPartner1 - {}, conversationPartner2 - {}",
                conversationPartner1, conversationPartner2);

        return dialogRepository.findByConversationPartner1AndConversationPartner2OrConversationPartner1AndConversationPartner2(conversationPartner1,
                        conversationPartner2,
                        conversationPartner2,
                        conversationPartner1)
                .orElse(null);
    }

}
