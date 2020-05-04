package com.dispassionproject.gutterball.api;

import com.dispassionproject.gutterball.exception.GamePlayException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class Game {

    @Builder.Default
    private UUID id = UUID.randomUUID();
    @Builder.Default
    private GameStatus status = GameStatus.PENDING;
    @Builder.Default
    private List<Player> players = new ArrayList<>();
    @Builder.Default
    private int currentFrame = 1;
    @Builder.Default
    private int nextPlayer = 1;

    public Player getPlayer(final int playerNo) {
        if (playerNo < 1 || playerNo > players.size()) {
            throw new GamePlayException(this, String.format("Player %d not found", playerNo));
        }
        return players.get(playerNo - 1);
    }

    public String dumpGameState() {
        return String.format(
                "gameId=%s, status=%s, playerCount=%d, currentFrame=%d, nextPlayer=%s",
                id, status, players.size(), currentFrame, nextPlayer
        );
    }

}
