package com.project.mc_dialog.web.controller;

import com.project.mc_dialog.security.JwtUtils;
import com.project.mc_dialog.service.DialogService;
import com.project.mc_dialog.web.dto.*;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.dialogDto.PageDialogDto;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
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

    private final JwtUtils jwtUtils;

    @PutMapping("/{dialogId}")
    public ResponseEntity<String> updateMessageStatus(@PathVariable UUID dialogId) {
        dialogService.updateMessageStatus(dialogId);
        return ResponseEntity.ok("Successful operation");
    }

    @PostMapping("/createMessage")
    public ResponseEntity<MessageDto> createMessage(@RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(dialogService.createMessage(messageDto));
    }

    @PostMapping("/createDialog")
    public ResponseEntity<DialogDto> createDialog(@RequestBody DialogDto dialogDto) {
        return ResponseEntity.ok(dialogService.createDialog(dialogDto));
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
    public ResponseEntity<DialogDto> getDialog(@PathVariable UUID id) {
        return ResponseEntity.ok(dialogService.getDialog(id));
    }

    @GetMapping("/messages")
    public ResponseEntity<PageMessageShortDto> getMessages(@RequestParam UUID recipientId,
                                                           @ModelAttribute @Valid Pageable pageable) {
        return ResponseEntity.ok(dialogService.getMessages(recipientId, pageable));
    }
}
