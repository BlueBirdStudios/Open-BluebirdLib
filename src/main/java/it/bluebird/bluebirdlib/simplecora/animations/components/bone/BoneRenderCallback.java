package it.bluebird.bluebirdlib.simplecora.animations.components.bone;

import com.mojang.blaze3d.vertex.PoseStack;
import it.bluebird.bluebirdlib.simplecora.geometry.components.GeometryBone;
import org.joml.Vector3f;

import java.util.Map;

@FunctionalInterface
public interface BoneRenderCallback {
    void callback(Map<String, GeometryBone> allBones, GeometryBone bone, PoseStack poseStack, Vector3f translations);
}
