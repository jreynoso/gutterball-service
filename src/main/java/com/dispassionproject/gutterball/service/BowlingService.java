package com.dispassionproject.gutterball.service;

import com.dispassionproject.gutterball.api.Frame;
import com.dispassionproject.gutterball.api.FrameType;
import com.dispassionproject.gutterball.api.Game;
import com.dispassionproject.gutterball.api.GameStatus;
import com.dispassionproject.gutterball.api.Player;
import org.springframework.stereotype.Component;

@Component
public class BowlingService {

    Game bowl(final Game game, final Player player, final int pins) {
        final int currentFrameNo = game.getCurrentFrame();

        final Frame currentFrame = updateFrameForPlayer(currentFrameNo, player, pins);
        updateScoresForPlayer(currentFrame, player);
        return updateGameState(game, currentFrame);
    }

    private Frame updateFrameForPlayer(final int frameNo, final Player player, final int pins) {
        final Frame currentFrame = player.getFrame(frameNo);
        currentFrame.addRoll(pins);
        return currentFrame;
    }

    private void updateScoresForPlayer(final Frame frame, final Player player) {
        final int frameNo = frame.getNumber();
        if (frameNo == 10) {
            scoreFinalFrameForPlayer(player);
        }
        else {
            Frame prevFrame = null;
            if (frameNo > 1) {
                prevFrame = player.getFrame(frameNo - 1);
            }
            Frame twoFramesBack = null;
            if (frameNo > 2) {
                twoFramesBack = player.getFrame(frameNo - 2);
            }

            scoreFrame(twoFramesBack, prevFrame, frame);
            scoreFrame(prevFrame, frame, null);
            scoreFrame(frame, null, null);
        }
        player.calculatePlayerScore();
    }

    private void scoreFrame(final Frame frame, final Frame nextFrame1, final Frame nextFrame2) {
        if (frame != null && frame.isComplete() && frame.isNotScored()) {
            if (frame.getType() == FrameType.STRIKE) {
                if (nextFrame1 != null) {
                    if (nextFrame1.getType() != FrameType.STRIKE && nextFrame1.isComplete()) {
                        finalizeStrike(frame, nextFrame1.getPins(1), nextFrame1.getPins(2));
                    } else if (nextFrame1.getType() == FrameType.STRIKE && nextFrame2 != null) {
                        finalizeStrike(frame, 10, nextFrame2.getPins(1));
                    }
                }
            } else if (frame.getType() == FrameType.SPARE && nextFrame1 != null) {
                finalizeSpare(frame, nextFrame1.getPins(1));
            } else if (frame.getType() == FrameType.NORMAL) {
                finalizeFrame(frame);
            }
        }
    }

    private void scoreFinalFrameForPlayer(final Player player) {
        final Frame eighthFrame = player.getFrame(8);
        final Frame ninthFrame = player.getFrame(9);
        final Frame finalFrame = player.getFrame(10);
        final int rollCount = finalFrame.getRolls().size();

        if (eighthFrame.isNotScored()) {
            if (ninthFrame.isNotScored() && ninthFrame.getType() == FrameType.STRIKE) {
                finalizeStrike(eighthFrame, 10, finalFrame.getPins(1));
            }
        }
        if (ninthFrame.isNotScored()) {
            if (ninthFrame.getType() == FrameType.SPARE) {
                finalizeSpare(ninthFrame, finalFrame.getPins(1));
            } else if (rollCount == 2) {
                finalizeStrike(ninthFrame, finalFrame.getPins(1), finalFrame.getPins(2));
            }
        }
        if (finalFrame.isComplete()) {
            finalizeFrame(finalFrame);
        }

    }

    private Game updateGameState(final Game game, final Frame frame) {
        final int frameNo = frame.getNumber();
        if (frame.isComplete()) {
            final int playerNo = game.getNextPlayer();
            int numPlayers = game.getPlayers().size();
            boolean isLastPlayer = playerNo == numPlayers;
            if (isLastPlayer) {
                game.setNextPlayer(1);
            } else {
                game.setNextPlayer(playerNo + 1);
            }
            boolean isLastFrame = frameNo == 10;
            if (!isLastFrame) {
                game.setCurrentFrame(frameNo + 1);
            } else if (isLastPlayer) {
                game.setStatus(GameStatus.COMPLETED);
            }
        }
        return game;
    }

    private void finalizeFrame(final Frame frame) {
        int extraRoll = frame.getRollCount() == 3 ? frame.getPins(3) : 0;
        frame.setScore(frame.getPins(1) + frame.getPins(2) + extraRoll);
    }

    private void finalizeSpare(final Frame frame, final int nextPin) {
        frame.setScore(10 + nextPin);
    }

    private void finalizeStrike(final Frame frame, final int firstPin, final int secondPin) {
        frame.setScore(10 + firstPin + secondPin);
    }


}