package it.bluebird.bluebirdlib.simplecora.animations.components.bone;

import it.bluebird.bluebirdlib.simplecora.animations.components.InterpolationType;
import it.bluebird.bluebirdlib.simplecora.animations.molang.MolangParser;
import it.bluebird.bluebirdlib.simplecora.animations.molang.MolangVector3f;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoneAnimation {
    private String name;
    private Map<Float, Keyframe> rotation = new HashMap<>();
    private Map<Float, Keyframe> position = new HashMap<>();
    private Map<Float, Keyframe> scale = new HashMap<>();

    public Vector3f getRotationAtTime(float time) {
        return this.interpolateKeyframes(this.rotation, time, false);
    }

    public Vector3f getPositionAtTime(float time) {
        return this.interpolateKeyframes(this.position, time, false);
    }

    public Vector3f getScaleAtTime(float time) {
        return this.interpolateKeyframes(this.scale, time, true);
    }

    private Vector3f interpolateKeyframes(Map<Float, Keyframe> keyframes, float time, boolean isScale) {
        if (keyframes.isEmpty()) {
            return isScale ? new Vector3f(1.0F, 1.0F, 1.0F) : new Vector3f(0.0F, 0.0F, 0.0F);
        }

        TreeMap<Float, Keyframe> sortedKeyframes = new TreeMap<>(keyframes);

        Map.Entry<Float, Keyframe> previousEntry = sortedKeyframes.floorEntry(time);
        Map.Entry<Float, Keyframe> nextEntry = sortedKeyframes.ceilingEntry(time);

        Keyframe previousKeyframe = previousEntry != null ? previousEntry.getValue() : null;
        Keyframe nextKeyframe = nextEntry != null ? nextEntry.getValue() : null;

        if (previousKeyframe == null) {
            return this.computeVector(nextKeyframe, time);
        } else if (nextKeyframe == null) {
            return this.computeVector(previousKeyframe, time);
        } else {
            Keyframe previousPreviousKeyframe = sortedKeyframes.lowerEntry(previousEntry.getKey()) != null
                    ? sortedKeyframes.lowerEntry(previousEntry.getKey()).getValue() : null;

            Keyframe nextNextKeyframe = sortedKeyframes.higherEntry(nextEntry.getKey()) != null
                    ? sortedKeyframes.higherEntry(nextEntry.getKey()).getValue() : null;

            float progress = (time - previousKeyframe.getTime()) / (nextKeyframe.getTime() - previousKeyframe.getTime());
            return this.interpolateWithMolang(previousPreviousKeyframe, previousKeyframe, nextKeyframe, nextNextKeyframe, progress, time);
        }
    }

    private Vector3f computeVector(Keyframe keyframe, float time) {
        Vector3f fallback = keyframe.getVector() != null ? keyframe.getVector() : new Vector3f(0.0F, 0.0F, 0.0F);
        MolangVector3f molang = keyframe.getMolangVector3f();

        if (molang == null) {
            return fallback;
        }

        float x = molang.getX() != null ? MolangParser.calculate(molang.getX(), time) : fallback.x();
        float y = molang.getY() != null ? MolangParser.calculate(molang.getY(), time) : fallback.y();
        float z = molang.getZ() != null ? MolangParser.calculate(molang.getZ(), time) : fallback.z();

        return new Vector3f(x, y, z);
    }

    private Vector3f interpolateWithMolang(Keyframe p0, Keyframe p1, Keyframe p2, Keyframe p3, float progress, float time) {
        InterpolationType interpolation = p1.getInterpolation();

        Vector3f vectorP0 = p0 != null ? this.computeVector(p0, time) : this.computeVector(p1, time);
        Vector3f vectorP1 = this.computeVector(p1, time);
        Vector3f vectorP2 = this.computeVector(p2, time);
        Vector3f vectorP3 = p3 != null ? this.computeVector(p3, time) : this.computeVector(p2, time);

        return switch (interpolation) {
            case LINEAR -> this.linearInterpolate(vectorP1, vectorP2, progress);
            case CATMULLROM -> this.catmullRomInterpolate(vectorP0, vectorP1, vectorP2, vectorP3, progress);
            default -> this.linearInterpolate(vectorP1, vectorP2, progress);
        };
    }

    private Vector3f linearInterpolate(Vector3f start, Vector3f end, float progress) {
        return new Vector3f(
                Mth.lerp(progress, start.x(), end.x()),
                Mth.lerp(progress, start.y(), end.y()),
                Mth.lerp(progress, start.z(), end.z())
        );
    }

    private Vector3f catmullRomInterpolate(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;

        float x = 0.5F * (2.0F * p1.x() + (-p0.x() + p2.x()) * t + (2.0F * p0.x() - 5.0F * p1.x() + 4.0F * p2.x() - p3.x()) * t2 + (-p0.x() + 3.0F * p1.x() - 3.0F * p2.x() + p3.x()) * t3);
        float y = 0.5F * (2.0F * p1.y() + (-p0.y() + p2.y()) * t + (2.0F * p0.y() - 5.0F * p1.y() + 4.0F * p2.y() - p3.y()) * t2 + (-p0.y() + 3.0F * p1.y() - 3.0F * p2.y() + p3.y()) * t3);
        float z = 0.5F * (2.0F * p1.z() + (-p0.z() + p2.z()) * t + (2.0F * p0.z() - 5.0F * p1.z() + 4.0F * p2.z() - p3.z()) * t2 + (-p0.z() + 3.0F * p1.z() - 3.0F * p2.z() + p3.z()) * t3);

        return new Vector3f(x, y, z);
    }
}