package com.project.mc_dialog.web.controller.exceptions;

import com.project.mc_dialog.exception.DialogNotFoundException;
import com.project.mc_dialog.web.dto.errors.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(DialogNotFoundException.class)
    public ResponseEntity<ErrorResponse> dialogNotFound(DialogNotFoundException ex) {
        log.error("Ошибка при попытке получить диалог", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getLocalizedMessage()));
    }
}
