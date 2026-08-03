//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package it.bluebird.bluebirdlib.data;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class AnimationSequence {
    private List<AnimationFrame> frames;
    private int totalDuration;
    private int maxHeight = 0;

    private AnimationSequence(List<AnimationFrame> frames, int totalDuration) {
        this.frames = frames;
        this.totalDuration = totalDuration;
    }

    public AnimationFrame getFrameForTime(long elapsedTime) {
        long time = elapsedTime % (long)this.totalDuration;
        int accumulatedTime = 0;

        for(AnimationFrame frame : this.frames) {
            accumulatedTime += frame.getDuration();
            if (time < (long)accumulatedTime) {
                return frame;
            }
        }

        return (AnimationFrame)this.frames.getLast();
    }

    @Nullable
    public AnimationFrame getFrame(int index) {
        if (this.frames.isEmpty()) {
            return null;
        } else {
            int correctedIndex = index % this.frames.size();
            return (AnimationFrame)this.frames.get(correctedIndex);
        }
    }

    private static List<AnimationFrame> $default$frames() {
        return new ArrayList();
    }

    public static AnimationSequenceBuilder builder() {
        return new AnimationSequenceBuilder();
    }

    public List<AnimationFrame> getFrames() {
        return this.frames;
    }

    public int getTotalDuration() {
        return this.totalDuration;
    }

    public int getMaxHeight() {
        return this.maxHeight;
    }

    public void setFrames(List<AnimationFrame> frames) {
        this.frames = frames;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AnimationSequence)) {
            return false;
        } else {
            AnimationSequence other = (AnimationSequence)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getTotalDuration() != other.getTotalDuration()) {
                return false;
            } else if (this.getMaxHeight() != other.getMaxHeight()) {
                return false;
            } else {
                Object this$frames = this.getFrames();
                Object other$frames = other.getFrames();
                if (this$frames == null) {
                    if (other$frames != null) {
                        return false;
                    }
                } else if (!this$frames.equals(other$frames)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof AnimationSequence;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTotalDuration();
        result = result * 59 + this.getMaxHeight();
        Object $frames = this.getFrames();
        result = result * 59 + ($frames == null ? 43 : $frames.hashCode());
        return result;
    }

    public String toString() {
        String var10000 = String.valueOf(this.getFrames());
        return "AnimationSequence(frames=" + var10000 + ", totalDuration=" + this.getTotalDuration() + ", maxHeight=" + this.getMaxHeight() + ")";
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public static class AnimationSequenceBuilder {
        private boolean frames$set;
        private List<AnimationFrame> frames$value;
        private int maxHeight;
        private final List<AnimationFrame> frames = new ArrayList();
        private int totalDuration = 0;

        public AnimationSequenceBuilder addFrame(int frameIndex, int duration) {
            this.frames.add(new AnimationFrame(frameIndex, duration));
            this.totalDuration += duration;
            return this;
        }

        public AnimationSequence build() {
            return new AnimationSequence(this.frames, this.totalDuration);
        }

        AnimationSequenceBuilder() {
        }

        public AnimationSequenceBuilder frames(List<AnimationFrame> frames) {
            this.frames$value = frames;
            this.frames$set = true;
            return this;
        }

        public AnimationSequenceBuilder totalDuration(int totalDuration) {
            this.totalDuration = totalDuration;
            return this;
        }

        public AnimationSequenceBuilder maxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }

        public String toString() {
            String var10000 = String.valueOf(this.frames$value);
            return "AnimationSequence.AnimationSequenceBuilder(frames$value=" + var10000 + ", totalDuration=" + this.totalDuration + ", maxHeight=" + this.maxHeight + ")";
        }
    }
}