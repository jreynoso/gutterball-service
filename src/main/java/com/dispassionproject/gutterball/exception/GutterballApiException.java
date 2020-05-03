package com.dispassionproject.gutterball.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public abstract class GutterballApiException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

}
