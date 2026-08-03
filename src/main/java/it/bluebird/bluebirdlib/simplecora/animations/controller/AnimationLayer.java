package it.bluebird.bluebirdlib.simplecora.animations.controller;

import it.bluebird.bluebirdlib.simplecora.animations.AnimationStorage;
import it.bluebird.bluebirdlib.simplecora.animations.components.Animation;
import it.bluebird.bluebirdlib.simplecora.animations.components.AnimationInstance;
import it.bluebird.bluebirdlib.simplecora.animations.components.BlendMode;
import it.bluebird.bluebirdlib.simplecora.animations.components.GeometryChanges;
import it.bluebird.bluebirdlib.simplecora.animations.components.LoopMode;
import it.bluebird.bluebirdlib.utils.INBTSerializable;
import javax.annotation.Nullable;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import net.minecraft.nbt.CompoundTag;

@Data
public class AnimationLayer implements INBTSerializable<CompoundTag> {
    private String id;
    private float startTime;
    private LoopMode loopMode = LoopMode.ONCE;
    private AnimationController controller;
    private BlendMode mode = BlendMode.ADD;

    @Nullable private AnimationInstance lastAnimation;
    @Nullable private AnimationInstance currentAnimation;

    private static final float TRANSITION_DURATION = 0.3F;

    public AnimationLayer(String id, AnimationController controller) {
        this.id = id;
        this.controller = controller;
    }

    public void tick(float tickCount) {
        float fadeOutProgress = 1.0F - Math.min(tickCount - this.startTime, TRANSITION_DURATION) / TRANSITION_DURATION;

        if (this.lastAnimation != null && fadeOutProgress <= 0.0F) {
            this.lastAnimation = null;
        }

        if (this.currentAnimation != null && this.loopMode == LoopMode.ONCE) {
            float timePassed = this.controller.getControllerTime(0.0F) - this.startTime;
            float animationLength = this.currentAnimation.getAnimation().getAnimationLength();

            if (timePassed + 0.1F >= animationLength) {
                this.stopAnimation();
            }
        }
    }

    public GeometryChanges apply(GeometryChanges geometryChanges, float controllerTime) {
        if (this.lastAnimation != null) {
            float animTime = this.getAnimTime(this.lastAnimation, controllerTime);
            float weight = 1.0F - Math.min(controllerTime - this.startTime, TRANSITION_DURATION) / TRANSITION_DURATION;
            geometryChanges.apply(this.lastAnimation.getAnimation(), this.mode, animTime, weight);
        }

        if (this.currentAnimation != null) {
            float animTime = this.getAnimTime(this.currentAnimation, controllerTime);
            float weight = Math.min(controllerTime - this.startTime, TRANSITION_DURATION) / TRANSITION_DURATION;
            geometryChanges.apply(this.currentAnimation.getAnimation(), this.mode, animTime, weight);
        }

        return geometryChanges;
    }

    public float getAnimTime(AnimationInstance animationInstance, float controllerTime) {
        float animationLength = animationInstance.getAnimation().getAnimationLength();
        float localTime = controllerTime - this.startTime;

        return switch (this.loopMode) {
            case ONCE -> localTime;
            case LOOP -> (animationLength > 0.0F) ? (localTime % animationLength) : 0.0F;
            case LAST_FRAME -> Math.min(localTime, animationLength);
        };
    }

    public boolean startAnimation(Animation animation) {
        if (animation == null) {
            return false;
        }
        if (this.currentAnimation != null && this.currentAnimation.getAnimation().equals(animation)) {
            return false;
        }

        this.lastAnimation = this.currentAnimation;
        this.startTime = this.controller.getControllerTime(0.0F);
        this.currentAnimation = new AnimationInstance(animation, this.startTime);

        this.controller.trySync();
        return true;
    }

    @NotNull
    public AnimationInstance getAnimation() {
        if (this.currentAnimation == null) {
            return new AnimationInstance(Animation.EMPTY, this.controller.getControllerTime(0.0F));
        }
        return this.currentAnimation;
    }

    public boolean stopAnimation() {
        return this.startAnimation(Animation.EMPTY);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.id != null ? this.id : "");
        tag.putFloat("startTime", this.startTime);
        tag.putString("loopMode", this.loopMode != null ? this.loopMode.name() : LoopMode.ONCE.name());

        if (this.lastAnimation != null) {
            tag.put("lastAnimation", this.serializeInstance(this.lastAnimation));
        }
        if (this.currentAnimation != null) {
            tag.put("currentAnimation", this.serializeInstance(this.currentAnimation));
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.id = tag.getString("id");
        this.startTime = tag.getFloat("startTime");
        this.loopMode = LoopMode.valueOf(tag.getString("loopMode"));

        this.lastAnimation = tag.contains("lastAnimation") ? this.deserializeInstance(tag.getCompound("lastAnimation")) : null;
        this.currentAnimation = tag.contains("currentAnimation") ? this.deserializeInstance(tag.getCompound("currentAnimation")) : null;
    }

    private CompoundTag serializeInstance(AnimationInstance instance) {
        CompoundTag tag = new CompoundTag();
        tag.putString("animationKey", instance.getAnimation().getKey());
        tag.putFloat("animationLength", instance.getAnimation().getAnimationLength());
        tag.putFloat("startTime", instance.getStartTime());
        return tag;
    }

    private AnimationInstance deserializeInstance(CompoundTag tag) {
        String key = tag.getString("animationKey");
        float animationLength = tag.getFloat("animationLength");
        Animation animation = AnimationStorage.getAnimationByKey(key, animationLength);
        return new AnimationInstance(animation, tag.getFloat("startTime"));
    }
}