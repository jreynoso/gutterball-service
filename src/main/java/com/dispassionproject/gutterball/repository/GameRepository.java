package com.dispassionproject.gutterball.repository;

import com.dispassionproject.gutterball.api.Game;
import io.jsondb.JsonDBTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GameRepository {

    private final JsonDBTemplate jsonDBTemplate;

    public GameRepository(final JsonDBTemplate jsonDBTemplate) {
        this.jsonDBTemplate = jsonDBTemplate;
        if (!jsonDBTemplate.collectionExists(Game.class)) {
            jsonDBTemplate.createCollection(Game.class);
        }
    }

    public void save(final Game game) {
        jsonDBTemplate.upsert(game);
    }

    public Game fetch(final UUID id) {
        return jsonDBTemplate.findById(id, Game.class);
    }

    public void reset() {
        if (jsonDBTemplate.collectionExists(Game.class)) {
            jsonDBTemplate.dropCollection(Game.class);
        }
        jsonDBTemplate.createCollection(Game.class);
    }

}
