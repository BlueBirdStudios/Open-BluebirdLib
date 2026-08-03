package it.bluebird.bluebirdlib.simplecora.animations.components;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AnimationFrame {
    private final int frameIndex;
    private final int duration;
}