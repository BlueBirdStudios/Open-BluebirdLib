package it.bluebird.bluebirdlib.simplecora.animations.components.bone;

import it.bluebird.bluebirdlib.simplecora.animations.components.InterpolationType;
import it.bluebird.bluebirdlib.simplecora.animations.molang.MolangVector3f;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Vector3f;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Keyframe {
    private float time;
    private Vector3f vector;

    @Nullable
    private MolangVector3f molangVector3f;
    private String easing;
    private InterpolationType interpolation;

    public Keyframe(float time, Vector3f vector, String easing, InterpolationType type) {
        this.time = time;
        this.vector = vector;
        this.molangVector3f = null;
        this.easing = easing;
        this.interpolation = type;
    }
}