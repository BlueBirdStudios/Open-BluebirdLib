package it.bluebird.bluebirdlib.simplecora.animations.components.bone;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.joml.Vector3f;

@Data
@AllArgsConstructor
public class BoneTransform {
    private String boneName;
    private Vector3f rotation;
    private Vector3f position;
    private Vector3f scale;

    public BoneTransform(String boneName) {
        this.boneName = boneName;
        this.position = new Vector3f(0.0F, 0.0F, 0.0F);
        this.rotation = new Vector3f(0.0F, 0.0F, 0.0F);
        this.scale = new Vector3f(1.0F, 1.0F, 1.0F);
    }
}