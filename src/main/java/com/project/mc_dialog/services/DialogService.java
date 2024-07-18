package com.project.mc_dialog.services;

import com.project.mc_dialog.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DialogService {

    public void updateMessageStatus(UUID dialogId) {
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
