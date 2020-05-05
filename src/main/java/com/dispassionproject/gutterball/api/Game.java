package com.dispassionproject.gutterball.api;

import com.dispassionproject.gutterball.exception.GamePlayException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@Document(collection = "games", schemaVersion= "1.0")
public class Game {

    @Id
    private UUID id = UUID.randomUUID();
    private GameStatus status = GameStatus.PENDING;
    private List<Player> players = new ArrayList<>();
    private int currentFrame = 1;
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
