package com.dispassionproject.gutterball.controller;

import com.dispassionproject.gutterball.api.Game;
import com.dispassionproject.gutterball.api.Player;
import com.dispassionproject.gutterball.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/game")
    public Game createGame() {
        return gameService.createGame();
    }

    @GetMapping("/game/{id}")
    public Game getGame(@PathVariable final UUID id) {
        return gameService.getGame(id);
    }

    @PostMapping("/game/{id}/start")
    public Game startGame(@PathVariable final UUID id) {
        return gameService.startGame(id);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/game/{id}/player")
    public Player createPlayer(@PathVariable final UUID id, @RequestBody final String playerName) {
        return gameService.createPlayer(id, playerName);
    }

    @PostMapping("/game/{id}/player/{playerId}/bowl")
    public Game bowl(
            @PathVariable final UUID id,
            @PathVariable final UUID playerId,
            @RequestBody final Integer pins
    ) {
        return gameService.bowl(id, playerId, pins);
    }

}
