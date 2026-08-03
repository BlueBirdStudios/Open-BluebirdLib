package it.bluebird.bluebirdlib.simplecora.animations.components;

import it.bluebird.bluebirdlib.simplecora.animations.components.bone.BoneAnimation;
import it.bluebird.bluebirdlib.simplecora.animations.components.bone.BoneTransform;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeometryChanges {
    private Map<String, BoneTransform> boneTransforms = new HashMap<>();
    private Map<String, BoneTransform> boneTransformsCopy = new HashMap<>();

    public void apply(Animation animation, BlendMode mode, float animTime, float progress) {
        for (Map.Entry<String, BoneAnimation> boneEntry : animation.getBones().entrySet()) {
            String boneName = boneEntry.getKey();
            BoneTransform currentTransform = this.boneTransforms.get(boneName);

            BoneTransform updatedTransform = this.calculateBoneTransform(
                    boneEntry.getValue(),
                    currentTransform,
                    mode,
                    animTime,
                    progress
            );

            this.boneTransforms.put(boneName, updatedTransform);
        }
    }

    public BoneTransform calculateBoneTransform(BoneAnimation animation, @Nullable BoneTransform boneTransform, BlendMode mode, float animTime, float progress) {
        if (boneTransform == null) {
            boneTransform = new BoneTransform(animation.getName());
        }

        if (mode == BlendMode.OVERRIDE) {
            boneTransform.setPosition(this.lerp(boneTransform.getPosition(), animation.getPositionAtTime(animTime), progress));
            boneTransform.setRotation(this.lerp(boneTransform.getRotation(), animation.getRotationAtTime(animTime), progress));
            boneTransform.setScale(this.lerp(boneTransform.getScale(), animation.getScaleAtTime(animTime), progress));

        } else if (mode == BlendMode.ADD) {
            Vector3f posDelta = animation.getPositionAtTime(animTime);
            Vector3f rotDelta = animation.getRotationAtTime(animTime);

            if (!this.hasNaN(posDelta) && !this.hasNaN(rotDelta) && !Float.isNaN(progress)) {
                boneTransform.setPosition(boneTransform.getPosition().add(posDelta.mul(progress)));
                boneTransform.setRotation(boneTransform.getRotation().add(rotDelta.mul(progress)));
            }

            Vector3f targetedScale = this.lerp(new Vector3f(1.0F, 1.0F, 1.0F), animation.getScaleAtTime(animTime), progress);
            boneTransform.setScale(multiply(boneTransform.getScale(), targetedScale));
        }

        return boneTransform;
    }

    private boolean hasNaN(Vector3f vec) {
        return Float.isNaN(vec.x()) || Float.isNaN(vec.y()) || Float.isNaN(vec.z());
    }

    private boolean isFinite(Vector3f vec) {
        return Float.isFinite(vec.x()) && Float.isFinite(vec.y()) && Float.isFinite(vec.z());
    }

    public static Vector3f multiply(Vector3f start, Vector3f end) {
        return new Vector3f(start.x * end.x, start.y * end.y, start.z * end.z);
    }

    private Vector3f lerp(Vector3f start, Vector3f end, float progress) {
        if (start != null && end != null && this.isFinite(start) && this.isFinite(end) && !Float.isNaN(progress)) {
            return new Vector3f(
                    this.lerp(start.x(), end.x(), progress),
                    this.lerp(start.y(), end.y(), progress),
                    this.lerp(start.z(), end.z(), progress)
            );
        }
        return new Vector3f(0.0F, 0.0F, 0.0F);
    }

    private float lerp(float a, float b, float progress) {
        return !Float.isNaN(a) && !Float.isNaN(b) && !Float.isNaN(progress) ? Mth.lerp(progress, a, b) : 0.0F;
    }
}