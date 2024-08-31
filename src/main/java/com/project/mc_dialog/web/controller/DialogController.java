package com.project.mc_dialog.web.controller;

import com.project.mc_dialog.service.DialogService;
import com.project.mc_dialog.service.MessageService;
import com.project.mc_dialog.web.dto.*;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.dialogDto.PageDialogDto;
import com.project.mc_dialog.web.dto.messageDto.PageMessageShortDto;
import com.project.mc_dialog.web.dto.messageDto.UnreadCountDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogController {

    private final DialogService dialogService;
    private final MessageService messageService;

    @PutMapping("/{dialogId}")
    public ResponseEntity<String> updateMessageStatus(@PathVariable(name = "dialogId") UUID dialogId) {
        messageService.updateMessageStatus(dialogId);
        return ResponseEntity.ok("Successful operation");
    }

    @GetMapping
    public ResponseEntity<PageDialogDto> getDialogs(@ModelAttribute @Valid Pageable pageable) {
        return ResponseEntity.ok(dialogService.getDialogs(pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<UnreadCountDto> getUnreadMessages() {
        return ResponseEntity.ok(dialogService.getUnreadCount());
    }

    @GetMapping("/recipientId/{id}")
    public ResponseEntity<DialogDto> getDialog(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(dialogService.getDialog(id));
    }

    @GetMapping("/messages")
    public ResponseEntity<PageMessageShortDto> getMessages(@RequestParam(name = "recipientId") UUID recipientId,
                                                           @ModelAttribute @Valid Pageable pageable) {
        return ResponseEntity.ok(messageService.getMessages(recipientId, pageable));
    }

}
