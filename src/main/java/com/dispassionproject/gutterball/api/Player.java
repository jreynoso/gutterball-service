package com.dispassionproject.gutterball.api;

import com.dispassionproject.gutterball.exception.UnexpectedServiceException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(Include.NON_NULL)
public class Player {

    @Builder.Default
    private final UUID id = UUID.randomUUID();
    private final String name;
    private int score;
    @Builder.Default
    private final List<Frame> frames = new ArrayList<>();

    public Frame getFrame(final int frameNo) {
        if (frameNo < 1 || frameNo > 10) {
            throw new UnexpectedServiceException("An invalid frame was requested.");
        }
        Frame frame;
        if (frameNo > frames.size()) {
            frame = Frame.builder().number(frameNo).build();
            frames.add(frame);
        } else {
            frame = frames.get(frameNo - 1);
        }
        return frame;
    }

    public void calculatePlayerScore() {
        score = frames.stream().map(Frame::getScore).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
    }

}
