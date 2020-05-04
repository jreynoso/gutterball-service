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
        final Frame frame = getFrame(frameNo);
        final int rollCount = frame.getRolls().size();

        Frame prevFrame = null;
        if (frameNo > 1) {
            prevFrame = getFrame(frameNo - 1);
        }

        if (frameNo < 10) {
            if (frame.getType() == FrameType.NORMAL) {
                if (rollCount == 1) {
                    // if prev frame is spare, finalize it
                    if (prevFrame != null && prevFrame.getType() == FrameType.SPARE) {
                        prevFrame.finalizeSpare(frame.getPins(1));
                    } else if (prevFrame != null && prevFrame.getType() == FrameType.STRIKE && frameNo > 2) {
                        Frame twoFramesBack = getFrame(frameNo - 2);
                        if (twoFramesBack.getType() == FrameType.STRIKE) {
                            twoFramesBack.finalizeStrike(10, 10);
                        }
                    }
                } else if (rollCount == 2) {
                    // if previous is strike, finalize it
                    if (prevFrame != null && prevFrame.getType() == FrameType.STRIKE) {
                        prevFrame.finalizeStrike(frame.getPins(1), frame.getPins(2));
                    }
                    frame.finalizeFrame();
                }
            } else if (frame.getType() == FrameType.STRIKE) {
                // if prev frame is spare, finalize it
                if (prevFrame != null && prevFrame.getType() == FrameType.SPARE) {
                    prevFrame.finalizeSpare(10);
                } else if (prevFrame != null && prevFrame.getType() == FrameType.STRIKE) {
                    if (frameNo > 2) {
                        Frame twoFramesBack = getFrame(frameNo - 2);
                        if (twoFramesBack.getType() == FrameType.STRIKE) {
                            twoFramesBack.finalizeStrike(10, 10);
                        }
                    }
                }
            }
        } else {
            if (frame.getType() == FrameType.NORMAL) {
                if (rollCount == 1) {
                    // if prev frame is spare, finalize it
                    if (prevFrame != null && prevFrame.getType() == FrameType.SPARE) {
                        prevFrame.finalizeSpare(frame.getPins(1));
                    }
                } else if (rollCount == 2) {
                    // if previous is strike, finalize it
                    if (prevFrame != null && prevFrame.getType() == FrameType.STRIKE) {
                        prevFrame.finalizeStrike(frame.getPins(1), frame.getPins(2));
                    }
                    frame.finalizeFrame();
                }
            } else if (frame.getType() == FrameType.SPARE) {
                if (rollCount == 3) {
                    frame.finalizeSpare(frame.getPins(3));
                }
            } else if (frame.getType() == FrameType.STRIKE) {
                // if prev frame is spare, finalize it
                if (rollCount == 1) {
                    if (prevFrame.getType() == FrameType.SPARE) {
                        prevFrame.finalizeSpare(10);
                    } else if (prevFrame.getType() == FrameType.STRIKE) {
                        Frame twoFramesBack = getFrame(frameNo - 2);
                        if (twoFramesBack.getType() == FrameType.STRIKE) {
                            twoFramesBack.finalizeStrike(10, 10);
                        }
                    }
                } else if (rollCount == 2) {
                    int pins = frame.getPins(2);
                    if (pins == 10) {
                        if (prevFrame.getType() == FrameType.STRIKE) {
                            prevFrame.finalizeStrike(10, 10);
                        }
                    } else {
                        if (prevFrame.getType() == FrameType.SPARE) {
                            prevFrame.finalizeSpare(pins);
                        }
                    }
                } else if (rollCount == 3) {
                    frame.finalizeFrame();
                }
            }
        }
        score = frames.stream().map(Frame::getScore).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
    }

}
