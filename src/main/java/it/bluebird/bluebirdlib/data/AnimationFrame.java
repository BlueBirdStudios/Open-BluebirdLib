package it.bluebird.bluebirdlib.data;

import lombok.Data;

@Data
public class AnimationFrame {
    private final int frameIndex;
    private final int duration;

    public AnimationFrame(int frameIndex, int duration) {
        this.frameIndex = frameIndex;
        this.duration = duration;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public int getDuration() {
        return duration;
    }
}
