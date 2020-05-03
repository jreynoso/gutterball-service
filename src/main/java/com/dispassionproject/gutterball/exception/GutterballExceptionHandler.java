package com.dispassionproject.gutterball.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GutterballExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { GutterballApiException.class })
    protected ResponseEntity<Object> handleGutterballApiException(RuntimeException ex, WebRequest request) {
        GutterballApiException apiEx = (GutterballApiException) ex;
        return handleExceptionInternal(ex, apiEx.getMessage(), new HttpHeaders(), apiEx.getStatus(), request);
    }

}