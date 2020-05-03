package com.dispassionproject.gutterball.api;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Frame {

    @Builder.Default
    private List<Integer> rolls = new ArrayList<>();

}
