package com.dispassionproject.gutterball.exception;

import com.dispassionproject.gutterball.api.Game;
import org.springframework.http.HttpStatus;

public class GameSetupException extends GutterballApiException {

    public GameSetupException(final Game game, final String reason) {
        super(HttpStatus.BAD_REQUEST, String.format("%s: %s", reason, game.dumpGameState()));
    }

}
