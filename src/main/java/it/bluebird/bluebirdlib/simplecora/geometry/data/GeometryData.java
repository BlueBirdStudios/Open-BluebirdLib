package it.bluebird.bluebirdlib.simplecora.geometry.data;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.bluebird.bluebirdlib.simplecora.animations.components.GeometryChanges;
import it.bluebird.bluebirdlib.simplecora.animations.components.bone.BoneTransform;
import it.bluebird.bluebirdlib.simplecora.geometry.components.GeometryBone;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Vector3f;

@Data
@NoArgsConstructor
public class GeometryData {
    private String identifier;
    private int textureWidth;
    private int textureHeight;

    private Map<String, GeometryBone> bones = new HashMap<>();
    private Map<String, GeometryBone> topBones = new HashMap<>();

    public GeometryData(String identifier, int textureWidth, int textureHeight) {
        this.identifier = identifier;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public GeometryData(GeometryData data) {
        this.identifier = data.identifier;
        this.textureWidth = data.textureWidth;
        this.textureHeight = data.textureHeight;
        this.bones = new HashMap<>(data.bones);
        this.topBones = getTopBones(new HashMap<>(data.getBones()));
    }

    public static GeometryData clone(GeometryData data) {
        GeometryData newData = new GeometryData("geometry.empty", 64, 64);
        if (data == null) {
            return newData;
        }

        newData.identifier = data.identifier;
        newData.textureWidth = data.textureWidth;
        newData.textureHeight = data.textureHeight;
        newData.bones = new HashMap<>();

        for (Map.Entry<String, GeometryBone> entry : data.bones.entrySet()) {
            newData.bones.put(entry.getKey(), entry.getValue().cloneTree());
        }

        newData.topBones = getTopBones(new HashMap<>(newData.bones));
        return newData;
    }

    public void applyAnimChanges(GeometryChanges changes) {
        Map<String, BoneTransform> boneTransforms = changes.getBoneTransforms();

        for (Map.Entry<String, BoneTransform> entry : boneTransforms.entrySet()) {
            GeometryBone bone = this.bones.get(entry.getKey());
            if (bone == null) continue;

            BoneTransform boneTransform = entry.getValue();
            if (boneTransform != null) {
                bone.reset();

                Vector3f position = boneTransform.getPosition();
                Vector3f rotation = new Vector3f(boneTransform.getRotation()).mul((float) (Math.PI / 180.0));
                Vector3f scale = boneTransform.getScale();

                bone.setPos(bone.getPos().add(position.x, -position.y, position.z));
                bone.setRotation(bone.getRotation().sub(rotation.x, rotation.y, -rotation.z));
                bone.setScale(bone.getScale().mul(scale.x, scale.y, scale.z));
            }
        }
    }

    public static Map<String, GeometryBone> getTopBones(Map<String, GeometryBone> bones) {
        Map<String, GeometryBone> topBones = new HashMap<>();
        for (GeometryBone bone : bones.values()) {
            if (bone.getParent().isEmpty()) {
                topBones.put(bone.getName(), bone);
            }
        }
        return topBones;
    }

    public void addBone(GeometryBone bone) {
        this.bones.put(bone.getName(), bone);
    }

    public boolean hasBone(String name) {
        return this.bones.containsKey(name);
    }

    public void setAlphaBones(Set<String> alphaBones) {
        for (GeometryBone bone : this.bones.values()) {
            bone.setAlphaBones(alphaBones);
        }
    }

    public void renderModel(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                            float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        for (GeometryBone topBone : this.topBones.values()) {
            topBone.renderBone(this.bones, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
        poseStack.popPose();
    }

    public void renderModel(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
        this.renderModel(poseStack, buffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderModel(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float alpha) {
        this.renderModel(poseStack, buffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, alpha);
    }

    public void renderModel(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Color color, float alpha) {
        this.renderModel(poseStack, buffer, packedLight, packedOverlay, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, alpha);
    }

    public void renderModel(PoseStack poseStack, VertexConsumer buffer, int packedLight) {
        this.renderModel(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void renderModel(PoseStack poseStack) {
        poseStack.pushPose();
        for (GeometryBone topBone : this.topBones.values()) {
            topBone.renderBone(this.bones, poseStack);
        }
        poseStack.popPose();
    }
}