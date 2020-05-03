package com.dispassionproject.gutterball.controller;

import com.dispassionproject.gutterball.api.Game;
import com.dispassionproject.gutterball.api.GameStatus;
import com.dispassionproject.gutterball.api.Player;
import com.dispassionproject.gutterball.exception.GameNotFoundException;
import com.dispassionproject.gutterball.exception.GamePlayException;
import com.dispassionproject.gutterball.exception.GameSetupException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final HashMap<UUID, Game> games = new HashMap<>();

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/game")
    public Game createGame() {
        Game game = Game.builder().build();
        games.put(game.getId(), game);
        return game;
    }

    @GetMapping("/game/{id}")
    public Game getGame(@PathVariable final UUID id) {
        Game game = games.get(id);
        if (game == null) {
            throw new GameNotFoundException(id);
        }
        return game;
    }

    @PostMapping("/game/{id}/start")
    public Game startGame(@PathVariable final UUID id) {
        final Game game = games.get(id);
        if (game == null) {
            throw new GameNotFoundException(id, "Unknown game cannot be started");
        }
        if (game.getStatus() != GameStatus.READY) {
            throw new GameSetupException(game, "Game cannot be started");
        }
        game.setStatus(GameStatus.STARTED);
        game.setNextPlayer(game.getPlayers().get(0).getId());
        return game;
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("/game/{id}/player")
    public Player createPlayer(@PathVariable final UUID id, @RequestBody final String playerName) {
        final Game game = games.get(id);
        if (game == null) {
            throw new GameNotFoundException(id, "Player cannot be created for unknown game");
        }
        if (game.getStatus() == GameStatus.STARTED) {
            throw new GamePlayException(game, "Game has already started");
        }
        if (game.getStatus() == GameStatus.COMPLETED) {
            throw new GamePlayException(game, "Game is over");
        }
        if (game.getPlayers().size() == 4) {
            throw new GameSetupException(game, "Game is full");
        }
        if (game.getPlayers().stream().anyMatch(player1 -> player1.getName().equals(playerName))) {
            throw new GameSetupException(game, String.format("Player %s already exists", playerName));
        }
        final Player player = Player.builder()
                .name(playerName)
                .build();
        game.getPlayers().add(player);
        if (game.getStatus() == GameStatus.PENDING) {
            game.setStatus(GameStatus.READY);
        }

        return player;
    }

}
