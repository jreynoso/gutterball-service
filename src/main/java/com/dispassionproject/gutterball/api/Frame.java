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
    @Builder.Default
    private Integer score = null;
    @JsonIgnore
    @Builder.Default
    private FrameType type = FrameType.NORMAL;

    public void addRoll(int pins) {
        int maxRolls = number == 10 ? 3 : 2;
        if (rolls.size() < maxRolls) {
            rolls.add(pins);
            if (pins == 10) {
                type = FrameType.STRIKE;
            } else if (rolls.size() == 2 && getPins(1) + getPins(2) == 10) {
                type = FrameType.SPARE;
            }
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
        switch (type) {
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

}
