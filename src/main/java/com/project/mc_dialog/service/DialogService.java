package com.project.mc_dialog.service;

import com.project.mc_dialog.exception.DialogAlreadyExistException;
import com.project.mc_dialog.exception.DialogNotFoundException;
import com.project.mc_dialog.mapper.DialogMapper;
import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.repository.DialogRepository;
import com.project.mc_dialog.repository.MessageRepository;
import com.project.mc_dialog.web.dto.*;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.dialogDto.PageDialogDto;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import com.project.mc_dialog.web.dto.messageDto.PageMessageShortDto;
import com.project.mc_dialog.web.dto.messageDto.ReadStatus;
import com.project.mc_dialog.web.dto.messageDto.UnreadCountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DialogService {

    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final DialogMapper dialogMapper;

    public void updateMessageStatus(UUID dialogId) {
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
        Message newMessage = new Message();
        newMessage.setDeleted(false);
        newMessage.setConversationPartner1(messageDto.getConversationPartner1());
        newMessage.setConversationPartner2(messageDto.getConversationPartner2());
        newMessage.setReadStatus(ReadStatus.SENT);
        newMessage.setMessageText(messageDto.getMessageText());
        newMessage.setDialog(getExistingDialog(messageDto.getDialogId()));

        return dialogMapper.messageToDto(messageRepository.save(newMessage));
    }

    public DialogDto createDialog(DialogDto dialogDto) {
        if (getExistingDialog(dialogDto.getConversationPartner1(), dialogDto.getConversationPartner2()) != null) {
            throw new DialogAlreadyExistException(
                    MessageFormat.format("Dialog with user {0} already exist", dialogDto.getConversationPartner2()));
        }

        Dialog newDialog = new Dialog();
        newDialog.setDeleted(false);
        newDialog.setUnreadCount(0);
        newDialog.setConversationPartner1(dialogDto.getConversationPartner1());
        newDialog.setConversationPartner2(dialogDto.getConversationPartner2());

        return dialogMapper.dialogToDto(dialogRepository.save(newDialog));
    }

    public PageDialogDto getDialogs(Pageable pageable) {
        return new PageDialogDto();
    }

    public UnreadCountDto getUnreadCount() {
        return new UnreadCountDto();
    }

    public DialogDto getDialog(UUID dialogId) {
        return dialogMapper.dialogToDto(getExistingDialog(dialogId));
    }

    public PageMessageShortDto getMessages(UUID recipientId, Pageable pageable) {
        return new PageMessageShortDto();
    }

    private Dialog getExistingDialog(UUID dialogId) {
        return dialogRepository.findById(dialogId)
                .orElseThrow(() -> new DialogNotFoundException(MessageFormat.format("Dialog with id {0} not found", dialogId)));
    }

    private Dialog getExistingDialog(UUID conversationPartner1, UUID conversationPartner2){

//  Зависит от реализации getDialog()
//        return dialogRepository.findByConversationPartner1AndAndConversationPartner2(conversationPartner1, conversationPartner2)
//                .orElseThrow(() -> new DialogNotFoundException(
//                        MessageFormat.format("Dialog with user {0} not found", conversationPartner2)));

        return dialogRepository.findByConversationPartner1AndConversationPartner2(conversationPartner1, conversationPartner2)
                .orElse(null);

    }
}
