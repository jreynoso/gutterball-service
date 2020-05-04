package com.dispassionproject.gutterball.exception;

import com.dispassionproject.gutterball.api.Game;
import org.springframework.http.HttpStatus;

public class UnexpectedServiceException extends GutterballApiException {

    public UnexpectedServiceException(final String reason) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }

    public UnexpectedServiceException(final Game game, final String reason) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, String.format("%s: %s", reason, game.dumpGameState()));
    }

}
