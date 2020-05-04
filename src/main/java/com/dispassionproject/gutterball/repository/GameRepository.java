package com.dispassionproject.gutterball.repository;

import com.dispassionproject.gutterball.api.Game;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
public class GameRepository {

    private final HashMap<UUID, Game> games = new HashMap<>();

    public synchronized void save(final Game game) {
        games.put(game.getId(), game);
    }

    public synchronized Game fetch(final UUID id) {
        return games.get(id);
    }

}
