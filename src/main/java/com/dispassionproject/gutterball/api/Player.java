package com.dispassionproject.gutterball.api;

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
public class Player {

    @Builder.Default
    private UUID id = UUID.randomUUID();
    private String name;
    @Builder.Default
    private int score = 0;
    @Builder.Default
    private List<Frame> frames = new ArrayList<>();

}
