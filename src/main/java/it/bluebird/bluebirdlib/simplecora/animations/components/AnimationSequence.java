package it.bluebird.bluebirdlib.simplecora.animations.components;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class AnimationSequence {
    private List<AnimationFrame> frames;
    private int totalDuration;

    @Builder.Default
    private int maxHeight = 0;

    private AnimationSequence(List<AnimationFrame> frames, int totalDuration) {
        this.frames = frames;
        this.totalDuration = totalDuration;
        this.maxHeight = 0;
    }

    public AnimationFrame getFrameForTime(long elapsedTime) {
        if (this.frames.isEmpty()) {
            return null;
        }

        long timeInLoop = elapsedTime % (long) this.totalDuration;
        int accumulatedTime = 0;

        for (AnimationFrame frame : this.frames) {
            accumulatedTime += frame.getDuration();
            if (timeInLoop < (long) accumulatedTime) {
                return frame;
            }
        }

        return this.frames.get(this.frames.size() - 1);
    }

    @Nullable
    public AnimationFrame getFrame(int index) {
        if (this.frames.isEmpty()) {
            return null;
        }
        int correctedIndex = index % this.frames.size();
        return this.frames.get(correctedIndex);
    }

    public static class AnimationSequenceBuilder {
        private List<AnimationFrame> frames = new ArrayList<>();
        private int totalDuration = 0;

        public AnimationSequenceBuilder addFrame(int frameIndex, int duration) {
            this.frames.add(new AnimationFrame(frameIndex, duration));
            this.totalDuration += duration;
            return this;
        }

        public AnimationSequence build() {
            return new AnimationSequence(this.frames, this.totalDuration);
        }
    }
}