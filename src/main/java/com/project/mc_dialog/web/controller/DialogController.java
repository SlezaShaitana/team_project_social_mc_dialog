package com.project.mc_dialog.web.controller;

import com.project.mc_dialog.service.DialogService;
import com.project.mc_dialog.web.dto.*;
import com.project.mc_dialog.web.dto.dialogDto.DialogDto;
import com.project.mc_dialog.web.dto.dialogDto.PageDialogDto;
import com.project.mc_dialog.web.dto.messageDto.MessageDto;
import com.project.mc_dialog.web.dto.messageDto.PageMessageShortDto;
import com.project.mc_dialog.web.dto.messageDto.UnreadCountDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
@Slf4j
public class DialogController {

    private final DialogService dialogService;

    @PutMapping("/{dialogId}")
    public ResponseEntity<String> updateMessageStatus(@PathVariable(name = "dialogId") UUID dialogId) {
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
    public ResponseEntity<PageDialogDto> getDialogs(@RequestHeader("Authorization") String headerAuth,
                                                    @ModelAttribute @Valid Pageable pageable) {
        return ResponseEntity.ok(dialogService.getDialogs(getToken(headerAuth), pageable));
    }

    @GetMapping("/unread")
    public ResponseEntity<UnreadCountDto> getUnreadMessages(@RequestHeader("Authorization") String headerAuth) {
        return ResponseEntity.ok(dialogService.getUnreadCount(getToken(headerAuth)));
    }

    @GetMapping("/recipientId/{id}")
    public ResponseEntity<DialogDto> getDialog(@RequestHeader("Authorization") String headerAuth,
                                               @PathVariable(name = "id") UUID id) {
        log.info("Getting dialog by id: {}", id);
        return ResponseEntity.ok(dialogService.getDialog(getToken(headerAuth), id));
    }

    @GetMapping("/messages")
    public ResponseEntity<PageMessageShortDto> getMessages(@RequestHeader("Authorization") String headerAuth,
                                                           @RequestParam(name = "recipientId") UUID recipientId,
                                                           @ModelAttribute @Valid Pageable pageable) {
        log.info("Getting messages from dialog id: {}", recipientId);
        return ResponseEntity.ok(dialogService.getMessages(getToken(headerAuth), recipientId, pageable));
    }

    private String getToken(String headerAuth){
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
