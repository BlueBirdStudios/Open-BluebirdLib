package it.bluebird.bluebirdlib.simplecora.animations.components;

import it.bluebird.bluebirdlib.simplecora.animations.components.bone.BoneAnimation;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Vector3f;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animation {
    private String key;
    private Map<String, BoneAnimation> bones = new HashMap<>();
    private float animationLength;

    public static final Animation EMPTY = new Animation("empty", new HashMap<>(), 0.0F);

    public Vector3f getRotationAtTime(String boneName, float time) {
        BoneAnimation boneData = this.bones.get(boneName);
        return boneData != null ? boneData.getRotationAtTime(time) : new Vector3f(0.0F, 0.0F, 0.0F);
    }

    public Vector3f getPositionAtTime(String boneName, float time) {
        BoneAnimation boneData = this.bones.get(boneName);
        return boneData != null ? boneData.getPositionAtTime(time) : new Vector3f(0.0F, 0.0F, 0.0F);
    }

    public Vector3f getScaleAtTime(String boneName, float time) {
        BoneAnimation boneData = this.bones.get(boneName);
        return boneData != null ? boneData.getScaleAtTime(time) : new Vector3f(1.0F, 1.0F, 1.0F);
    }

    public boolean same(Animation animation) {
        if (animation == null) return false;
        return this.key.equals(animation.getKey())
                && this.bones.equals(animation.getBones())
                && this.animationLength == animation.getAnimationLength();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Animation animation = (Animation) obj;
        return this.key.equals(animation.key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
}