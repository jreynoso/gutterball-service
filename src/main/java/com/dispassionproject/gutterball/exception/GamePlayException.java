package com.dispassionproject.gutterball.exception;

import com.dispassionproject.gutterball.api.Game;
import org.springframework.http.HttpStatus;

public class GamePlayException extends GutterballApiException {

    public GamePlayException(final Game game, final String reason) {
        super(HttpStatus.FORBIDDEN, String.format("%s: %s", reason, game.dumpGameState()));
    }

}
