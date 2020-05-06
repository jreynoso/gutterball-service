package com.dispassionproject.gutterball.controller;

import com.dispassionproject.gutterball.api.BowlRequest;
import com.dispassionproject.gutterball.api.CreatePlayerRequest;
import com.dispassionproject.gutterball.api.Game;
import com.dispassionproject.gutterball.api.Player;
import com.dispassionproject.gutterball.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(path = "/game", produces = "application/json" )
    public Game createGame() {
        return gameService.createGame();
    }

    @GetMapping(path = "/game/{id}", produces = "application/json")
    public Game getGame(@PathVariable final UUID id) {
        return gameService.getGame(id);
    }

    @PostMapping(value = "/game/{id}/start", produces = "application/json")
    public Game startGame(@PathVariable final UUID id) {
        return gameService.startGame(id);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(path = "/game/{id}/player", consumes = "application/json", produces = "application/json")
    public Player createPlayer(
            @PathVariable final UUID id,
            @Valid @RequestBody final CreatePlayerRequest createPlayerRequest
    ) {
        return gameService.createPlayer(id, createPlayerRequest.getName());
    }

    @PostMapping(
            path = "/game/{id}/player/{playerId}/bowl",
            consumes = "application/json",
            produces = "application/json"
    )
    public Game bowl(
            @PathVariable final UUID id,
            @PathVariable final UUID playerId,
            @Valid @RequestBody final BowlRequest bowlRequest
    ) {
        return gameService.bowl(id, playerId, bowlRequest.getPins());
    }

}
