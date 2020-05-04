package com.dispassionproject.gutterball.service;

import com.dispassionproject.gutterball.api.Frame;
import com.dispassionproject.gutterball.api.Game;
import com.dispassionproject.gutterball.api.GameStatus;
import com.dispassionproject.gutterball.api.Player;
import com.dispassionproject.gutterball.exception.GameNotFoundException;
import com.dispassionproject.gutterball.exception.GamePlayException;
import com.dispassionproject.gutterball.exception.GameSetupException;
import com.dispassionproject.gutterball.exception.UnexpectedServiceException;
import com.dispassionproject.gutterball.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Game createGame() {
        Game game = Game.builder().build();
        gameRepository.save(game);
        return game;
    }

    public Game getGame(final UUID id) {
        Game game = gameRepository.fetch(id);
        if (game == null) {
            throw new GameNotFoundException(id);
        }
        return game;
    }

    public Game startGame(final UUID id) {
        final Game game = gameRepository.fetch(id);
        if (game == null) {
            throw new GameNotFoundException(id, "Unknown game cannot be started");
        }
        if (game.getStatus() != GameStatus.READY) {
            throw new GameSetupException(game, "Game cannot be started");
        }
        game.setStatus(GameStatus.STARTED);
        game.setNextPlayer(1);
        gameRepository.save(game);
        return game;
    }

    public Player createPlayer(final UUID id, final String playerName) {
        final Game game = gameRepository.fetch(id);
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
        gameRepository.save(game);
        return player;
    }

    public Game bowl(final UUID id, final UUID playerId, final int pins) {
        final Game game = gameRepository.fetch(id);
        if (game == null) {
            throw new GameNotFoundException(id);
        }
        if (game.getStatus() == GameStatus.PENDING || game.getStatus() == GameStatus.READY) {
            throw new GameSetupException(game, "Cannot bowl a game that has not started");
        }
        if (game.getStatus() == GameStatus.COMPLETED) {
            throw new GameSetupException(game, "Cannot bowl a game that has been completed");
        }
        if (pins < 0 || pins > 10) {
            throw new GamePlayException(game, String.format("%d is an invalid number of pins", pins));
        }
        int playerNo = game.getNextPlayer();
        Player player = game.getPlayer(playerNo);
        if (player == null) {
            throw new UnexpectedServiceException(game, "Next player not found");
        }
        if (!player.getId().equals(playerId)) {
            throw new GamePlayException(game, String.format("It is not playerId=%s's turn", playerId));
        }

        // update frame
        final int currentFrameNo = game.getCurrentFrame();
        final Frame currentFrame = player.getFrame(currentFrameNo);
        currentFrame.addRoll(pins);

        // score
        player.updateScore(currentFrameNo);

        // Post roll stuff
        if (currentFrame.isComplete()) {
            int numPlayers = game.getPlayers().size();
            boolean isLastPlayer = playerNo == numPlayers;
            if (isLastPlayer) {
                game.setNextPlayer(1);
            } else {
                game.setNextPlayer(playerNo + 1);
            }
            boolean isLastFrame = currentFrameNo == 10;
            if (!isLastFrame) {
                game.setCurrentFrame(currentFrameNo + 1);
            } else if (isLastPlayer) {
                game.setStatus(GameStatus.COMPLETED);
            }
        }
        gameRepository.save(game);
        return game;
    }

}
