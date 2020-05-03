package com.dispassionproject.gutterball.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameStatus {

    @JsonProperty("pending")
    PENDING,
    @JsonProperty("ready")
    READY,
    @JsonProperty("started")
    STARTED,
    @JsonProperty("completed")
    COMPLETED

}
