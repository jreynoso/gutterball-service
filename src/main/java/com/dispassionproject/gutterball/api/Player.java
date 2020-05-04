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
    @Builder.Default
    private int score = 0;
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

    public void updateScore(final int frameNo) {
        updateFrameScores(frameNo);
        updatePlayerScore();
    }

    private void updateFrameScores(final int frameNo) {
        final boolean isFinalFrame = frameNo == 10;

        if (isFinalFrame) {
            scoreFinalFrame();
        }
        else {
            final Frame frame = getFrame(frameNo);
            Frame prevFrame = null;
            if (frameNo > 1) {
                prevFrame = getFrame(frameNo - 1);
            }
            Frame twoFramesBack = null;
            if (frameNo > 2) {
                twoFramesBack = getFrame(frameNo - 2);
            }

            scoreFrame(twoFramesBack, prevFrame, frame);
            scoreFrame(prevFrame, frame, null);
            scoreFrame(frame, null, null);
        }
    }

    private void updatePlayerScore() {
        score = frames.stream().map(Frame::getScore).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
    }

    private void scoreFrame(final Frame frame, final Frame nextFrame1, final Frame nextFrame2) {
        if (frame != null && frame.isComplete() && frame.isNotScored()) {
            if (frame.getType() == FrameType.STRIKE) {
                if (nextFrame1 != null) {
                    if (nextFrame1.getType() != FrameType.STRIKE && nextFrame1.isComplete()) {
                        frame.finalizeStrike(nextFrame1.getPins(1), nextFrame1.getPins(2));
                    } else if (nextFrame1.getType() == FrameType.STRIKE && nextFrame2 != null) {
                        frame.finalizeStrike(10, nextFrame2.getPins(1));
                    }
                }
            } else if (frame.getType() == FrameType.SPARE && nextFrame1 != null) {
                frame.finalizeSpare(nextFrame1.getPins(1));
            } else if (frame.getType() == FrameType.NORMAL) {
                frame.finalizeFrame();
            }
        }
    }

    private void scoreFinalFrame() {
        final Frame eightFrame = getFrame(8);
        final Frame ninthFrame = getFrame(9);
        final Frame finalFrame = getFrame(10);
        final int rollCount = finalFrame.getRolls().size();

        if (eightFrame.isNotScored()) {
            if (ninthFrame.isNotScored() && ninthFrame.getType() == FrameType.STRIKE) {
                eightFrame.finalizeStrike(10, finalFrame.getPins(1));
            }
        }
        if (ninthFrame.isNotScored()) {
            if (ninthFrame.getType() == FrameType.SPARE) {
                ninthFrame.finalizeSpare(finalFrame.getPins(1));
            } else if (rollCount == 2) {
                ninthFrame.finalizeStrike(finalFrame.getPins(1), finalFrame.getPins(2));
            }
        }
        if (finalFrame.isComplete()) {
            finalFrame.finalizeFrame();
        }

    }

}
