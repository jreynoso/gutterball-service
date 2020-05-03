package com.dispassionproject.gutterball.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class GameNotFoundException extends GutterballApiException {

    public GameNotFoundException(final UUID id, final String reason) {
        super(HttpStatus.NOT_FOUND, String.format("%s: gameId=%s", reason, id));
    }

    public GameNotFoundException(final UUID id) {
        this(id, "Game not found");
    }

}
