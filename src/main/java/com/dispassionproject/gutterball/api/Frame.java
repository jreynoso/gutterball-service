package com.dispassionproject.gutterball.api;

import com.dispassionproject.gutterball.exception.UnexpectedServiceException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Frame {

    private final int number;
    @Builder.Default
    private final List<Integer> rolls = new ArrayList<>();
    @Setter
    private Integer score;

    public void addRoll(int pins) {
        int maxRolls = number == 10 ? 3 : 2;
        if (rolls.size() < maxRolls) {
            rolls.add(pins);
        } else {
            throw new UnexpectedServiceException("Cannot add another roll to current frame.");
        }
    }

    public int getPins(final int rollNo) {
        if (rollNo < 1 || rollNo > rolls.size()) {
            throw new UnexpectedServiceException("An invalid roll was requested.");
        }
        return rolls.get(rollNo - 1);
    }

    @JsonIgnore
    public boolean isNotScored() {
        return score == null;
    }

    @JsonIgnore
    public boolean isComplete() {
        int rollCount = rolls.size();
        switch (getType()) {
            case NORMAL:
                return rollCount == 2;
            case SPARE:
                return number == 10 ? rollCount == 3 : rollCount == 2;
            case STRIKE:
                return number == 10 ? rollCount == 3 : rollCount == 1;
        }
        return false;
    }

    @JsonIgnore
    public int getRollCount() {
        return rolls.size();
    }

    @JsonIgnore
    public FrameType getType() {
        int rollCount = getRollCount();
        if (rollCount > 0 && getPins(1) == 10) {
            return FrameType.STRIKE;
        } else if (rollCount > 1 && getPins(1) + getPins(2) == 10) {
            return FrameType.SPARE;
        } else {
            return FrameType.NORMAL;
        }
    }

}
