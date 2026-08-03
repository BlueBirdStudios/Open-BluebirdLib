package it.bluebird.bluebirdlib.simplecora.animations.components;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnimationInstance {
    public Animation animation;
    public float startTime;
}