package com.project.mc_dialog.service;

import com.project.mc_dialog.exception.DialogNotFoundException;
import com.project.mc_dialog.model.Dialog;
import com.project.mc_dialog.model.Message;
import com.project.mc_dialog.repository.DialogRepository;
import com.project.mc_dialog.repository.MessageRepository;
import com.project.mc_dialog.web.dto.*;
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

    public void updateMessageStatus(UUID dialogId) {
        Dialog dialog = dialogRepository.findById(dialogId)
                .orElseThrow(() -> new DialogNotFoundException(MessageFormat.format("Dialog with id {0} not found", dialogId)));

        List<Message> messages = dialog.getMessages();
        messages.sort((m1, m2) -> m2.getTime().compareTo(m1.getTime()));
        for (Message message : messages) {
            if (message.getReadStatus().equals(ReadStatus.READ)) {
                break;
            }
            message.setReadStatus(ReadStatus.READ);
        }
    }

    public MessageDto createMessage(MessageDto message) {
        return message;
    }

    public DialogDto createDialog(DialogDto dialog) {
        return dialog;
    }

    public PageDialogDto getDialogs(Pageable pageable) {
        return new PageDialogDto();
    }

    public UnreadCountDto getUnreadCount() {
        return new UnreadCountDto();
    }

    public DialogDto getDialog(UUID dialogId) {
        return new DialogDto();
    }

    public PageMessageShortDto getMessages(UUID recipientId, Pageable pageable) {
        return new PageMessageShortDto();
    }
}
