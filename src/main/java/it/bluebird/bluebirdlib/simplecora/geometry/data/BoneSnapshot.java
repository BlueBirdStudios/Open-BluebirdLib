package it.bluebird.bluebirdlib.simplecora.geometry.data;

import it.bluebird.bluebirdlib.simplecora.geometry.components.GeometryBone;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Vector3f;

@Data
@NoArgsConstructor
public class BoneSnapshot {
    private GeometryBone bone;

    private Vector3f previousRotation;
    private Vector3f currentRotation;
    private Vector3f previousPosition;
    private Vector3f currentPosition;
    private Vector3f previousScale;
    private Vector3f currentScale;

    private boolean rotationChanged;
    private boolean positionChanged;
    private boolean scaleChanged;

    private boolean posProgress;
    private boolean rotProgress;
    private boolean scaleProgress;

    private float resetRotTick = 0.0F;
    private float resetPosTick = 0.0F;
    private float resetScaleTick = 0.0F;

    public BoneSnapshot(Vector3f initialRotation, Vector3f initialPosition, Vector3f initialScale) {
        this.previousRotation = initialRotation;
        this.currentRotation = initialRotation;
        this.previousPosition = initialPosition;
        this.currentPosition = initialPosition;
        this.previousScale = initialScale;
        this.currentScale = initialScale;
    }

    public BoneSnapshot(GeometryBone bone, Vector3f initialRotation, Vector3f initialPosition, Vector3f initialScale) {
        this(initialRotation, initialPosition, initialScale);
        this.bone = bone;
    }

    public void updateRotation(Vector3f newRotation) {
        if (!newRotation.equals(0.0F, 0.0F, 0.0F)) {
            this.previousRotation = this.currentRotation;
            this.currentRotation = newRotation;
            this.rotationChanged = !newRotation.equals(this.previousRotation);
        }
    }

    public void updatePosition(Vector3f newPosition) {
        if (!newPosition.equals(0.0F, 0.0F, 0.0F)) {
            this.previousPosition = this.currentPosition;
            this.currentPosition = newPosition;
            this.positionChanged = !newPosition.equals(this.previousPosition);
        }
    }

    public void updateScale(Vector3f newScale) {
        if (!newScale.equals(0.0F, 0.0F, 0.0F)) {
            this.previousScale = this.currentScale;
            this.currentScale = newScale;
            this.scaleChanged = !newScale.equals(this.previousScale);
        }
    }

    public void startPosAnim() {
        this.posProgress = true;
    }

    public void stopPosAnim(float tick) {
        this.posProgress = false;
        this.resetPosTick = tick;
    }

    public void startRotAnim() {
        this.rotProgress = true;
    }

    public void stopRotAnim(float tick) {
        this.rotProgress = false;
        this.resetRotTick = tick;
    }

    public void startScaleAnim() {
        this.scaleProgress = true;
    }

    public void stopScaleAnim(float tick) {
        this.scaleProgress = false;
        this.resetScaleTick = tick;
    }

    public BoneSnapshot copy() {
        BoneSnapshot copy = new BoneSnapshot();
        copy.bone = this.bone;
        copy.previousRotation = this.previousRotation != null ? new Vector3f(this.previousRotation) : null;
        copy.currentRotation = this.currentRotation != null ? new Vector3f(this.currentRotation) : null;
        copy.previousPosition = this.previousPosition != null ? new Vector3f(this.previousPosition) : null;
        copy.currentPosition = this.currentPosition != null ? new Vector3f(this.currentPosition) : null;
        copy.previousScale = this.previousScale != null ? new Vector3f(this.previousScale) : null;
        copy.currentScale = this.currentScale != null ? new Vector3f(this.currentScale) : null;

        copy.rotationChanged = this.rotationChanged;
        copy.positionChanged = this.positionChanged;
        copy.scaleChanged = this.scaleChanged;

        copy.posProgress = this.posProgress;
        copy.rotProgress = this.rotProgress;
        copy.scaleProgress = this.scaleProgress;

        copy.resetRotTick = this.resetRotTick;
        copy.resetPosTick = this.resetPosTick;
        copy.resetScaleTick = this.resetScaleTick;
        return copy;
    }
}