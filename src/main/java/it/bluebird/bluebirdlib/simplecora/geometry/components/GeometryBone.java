package it.bluebird.bluebirdlib.simplecora.geometry.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.bluebird.bluebirdlib.simplecora.animations.components.bone.BoneRenderCallback;
import it.bluebird.bluebirdlib.simplecora.geometry.data.BoneSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Data
@NoArgsConstructor
public class GeometryBone {
    private String name;
    private Vector3f pos = new Vector3f(0.0F, 0.0F, 0.0F);
    private Vector3f pivot;
    private Vector3f rotation;
    public Vector3f scale;
    private String parent = "";

    private List<GeometryCube> cubes = new ArrayList<>();
    private List<GeometryBone> children = new ArrayList<>();

    private BoneSnapshot snapshot;
    private BoneSnapshot initialSnapshot;
    private Set<String> alphaBones = new HashSet<>();

    public BoneRenderCallback callback = null;
    public Vector3f worldPos = new Vector3f();

    private boolean rotChanged;
    private boolean posChanged;
    private boolean scaleChanged;

    public GeometryBone(String name, Vector3f pivot, Vector3f rotation, Vector3f scale) {
        this.name = name;
        this.pivot = pivot;
        this.rotation = rotation;
        this.scale = scale;
        this.snapshot = new BoneSnapshot(rotation, this.pos, scale);
        this.initialSnapshot = new BoneSnapshot(rotation, this.pos, scale);
    }

    public GeometryBone cloneTree() {
        GeometryBone copy = new GeometryBone(
                this.name,
                new Vector3f(this.pivot),
                new Vector3f(this.rotation),
                new Vector3f(this.scale)
        );

        this.cubes.forEach(copy::addCube);
        copy.setParent(this.parent);

        for (GeometryBone child : this.children) {
            GeometryBone childCopy = child.cloneTree();
            childCopy.setParent(copy.name);
            copy.addChildBone(childCopy);
        }

        return copy;
    }

    public GeometryBone copyFrom(GeometryBone source) {
        this.name = source.name;
        this.pos = source.pos;
        this.pivot = source.pivot;
        this.rotation = source.rotation;
        this.scale = source.scale;
        this.parent = source.parent;
        this.cubes = source.cubes;
        this.children = source.children;
        if (source.callback != null) {
            this.callback = source.callback;
        }
        return this;
    }

    public void resetToInitial() {
        this.snapshot.updateRotation(this.initialSnapshot.getCurrentRotation());
        this.snapshot.updatePosition(this.initialSnapshot.getCurrentPosition());
        this.snapshot.updateScale(this.initialSnapshot.getCurrentScale());
    }

    public void reset() {
        this.setRotation(this.initialSnapshot.getCurrentRotation());
        this.setPos(new Vector3f(0.0F, 0.0F, 0.0F));
        this.setScale(new Vector3f(1.0F, 1.0F, 1.0F));
    }

    public void setAlphaBones(Set<String> alphaBones) {
        this.alphaBones = alphaBones;
        for (GeometryBone bone : this.children) {
            bone.setAlphaBones(alphaBones);
        }
    }

    public void addCube(GeometryCube cube) {
        this.cubes.add(cube);
    }

    public void addChildBone(GeometryBone child) {
        this.children.add(child);
    }

    public void renderBone(Map<String, GeometryBone> allBones, PoseStack poseStack, VertexConsumer buffer,
                           int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (allBones.containsKey(this.getName())) {
            this.copyFrom(allBones.get(this.getName()));
        }

        if (this.alphaBones.contains(this.name)) {
            alpha = Math.max(0.0F, Math.min(1.0F, alpha)) * 255.0F;
        }

        poseStack.pushPose();

        if (!this.pos.equals(0.0F, 0.0F, 0.0F)) {
            poseStack.translate(-this.pos.x / 16.0F, -this.pos.y / 16.0F, this.pos.z / 16.0F);
        }

        poseStack.translate(this.pivot.x() / 16.0F, this.pivot.y() / 16.0F, this.pivot.z() / 16.0F);

        Vector3f rot = this.getRotation();
        Quaternionf rotationQuat = new Quaternionf().rotateZYX(rot.z(), rot.y(), rot.x());
        poseStack.mulPose(rotationQuat);

        if (this.scale.x() != 0.0F && this.scale.y() != 0.0F && this.scale.z() != 0.0F) {
            if (this.scale.x() != 1.0F || this.scale.y() != 1.0F || this.scale.z() != 1.0F) {
                poseStack.scale(this.scale.x(), this.scale.y(), this.scale.z());
            }
        }

        if (this.callback != null) {
            this.callback.callback(allBones, this, poseStack, new Vector3f());
        }
        poseStack.translate(-this.pivot.x() / 16.0F, -this.pivot.y() / 16.0F, -this.pivot.z() / 16.0F);

        this.renderCubes(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        for (GeometryBone childBone : this.children) {
            childBone.renderBone(allBones, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        poseStack.popPose();
    }

    public void renderBone(Map<String, GeometryBone> allBones, PoseStack poseStack) {
        if (allBones.containsKey(this.getName())) {
            this.copyFrom(allBones.get(this.getName()));
        }

        poseStack.pushPose();

        if (!this.pos.equals(0.0F, 0.0F, 0.0F)) {
            poseStack.translate(-this.pos.x / 16.0F, -this.pos.y / 16.0F, this.pos.z / 16.0F);
        }

        poseStack.translate(this.pivot.x() / 16.0F, this.pivot.y() / 16.0F, this.pivot.z() / 16.0F);

        Vector3f rot = this.getRotation();
        Quaternionf rotationQuat = new Quaternionf().rotateZYX(rot.z(), -rot.y(), rot.x());
        poseStack.mulPose(rotationQuat);

        if (this.scale.x() != 0.0F && this.scale.y() != 0.0F && this.scale.z() != 0.0F) {
            if (this.scale.x() != 1.0F || this.scale.y() != 1.0F || this.scale.z() != 1.0F) {
                poseStack.scale(this.scale.x(), this.scale.y(), this.scale.z());
            }
        }

        if (this.callback != null) {
            Matrix4f finalMatrix = poseStack.last().pose();
            Vector4f posVec = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
            posVec.mul(finalMatrix);
            this.worldPos.set(posVec.x, posVec.y, posVec.z);
            this.callback.callback(allBones, this, poseStack, this.worldPos);
        }

        poseStack.translate(-this.pivot.x() / 16.0F, -this.pivot.y() / 16.0F, -this.pivot.z() / 16.0F);

        for (GeometryBone childBone : this.children) {
            childBone.renderBone(allBones, poseStack);
        }

        poseStack.popPose();
    }

    private void renderCubes(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                             float red, float green, float blue, float alpha) {
        for (GeometryCube cube : this.cubes) {
            poseStack.pushPose();
            cube.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }

    public Vector3f getWorldPositionByMatrix(PoseStack poseStack) {
        Matrix4f worldMatrix = poseStack.last().pose();
        Vector4f localPosition = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        localPosition.mul(worldMatrix);
        return new Vector3f(localPosition.x(), localPosition.y(), localPosition.z());
    }

    public Vector3f getWorldPosition(Map<String, GeometryBone> allBones) {
        PoseStack poseStack = new PoseStack();
        this.applyBoneTransformations(this, allBones, poseStack);
        Matrix4f worldMatrix = poseStack.last().pose();
        Vector4f localPosition = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        localPosition.mul(worldMatrix);
        return new Vector3f(localPosition.x(), localPosition.y(), localPosition.z());
    }

    public Vector3f getWorldPositionNew(Map<String, GeometryBone> allBones) {
        PoseStack poseStack = new PoseStack();
        this.applyBoneTransformationsNew(this, allBones, poseStack);
        Matrix4f worldMatrix = poseStack.last().pose();
        Vector4f localPosition = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
        localPosition.mul(worldMatrix);
        return new Vector3f(localPosition.x(), localPosition.y(), localPosition.z());
    }

    public void applyBoneTransformationsNew(GeometryBone bone, Map<String, GeometryBone> allBones, PoseStack poseStack) {
        if (bone != null) {
            if (!bone.parent.isEmpty()) {
                this.applyBoneTransformationsNew(allBones.get(bone.parent), allBones, poseStack);
            }

            if (!bone.pos.equals(0.0F, 0.0F, 0.0F)) {
                poseStack.translate(-bone.pos.x() / 16.0F, bone.pos.y() / 16.0F, bone.pos.z() / 16.0F);
            }

            poseStack.translate(bone.pivot.x() / 16.0F, bone.pivot.y() / 16.0F, bone.pivot.z() / 16.0F);

            Vector3f rot = bone.getRotation();
            Quaternionf rotationQuat = new Quaternionf().rotateZYX(rot.z(), rot.y(), rot.x());
            poseStack.mulPose(rotationQuat);

            if (bone.scale.x() != 0.0F && bone.scale.y() != 0.0F && bone.scale.z() != 0.0F) {
                if (bone.scale.x() != 1.0F || bone.scale.y() != 1.0F || bone.scale.z() != 1.0F) {
                    poseStack.scale(bone.scale.x(), bone.scale.y(), bone.scale.z());
                }
            }

            poseStack.translate(-bone.pivot.x() / 16.0F, -bone.pivot.y() / 16.0F, -bone.pivot.z() / 16.0F);
        }
    }

    private void applyBoneTransformations(GeometryBone bone, Map<String, GeometryBone> allBones, PoseStack poseStack) {
        if (bone != null) {
            if (!bone.parent.isEmpty()) {
                this.applyBoneTransformations(allBones.get(bone.parent), allBones, poseStack);
            }

            poseStack.pushPose();

            if (!bone.pos.equals(0.0F, 0.0F, 0.0F)) {
                poseStack.translate(-bone.pos.x() / 16.0F, bone.pos.y() / 16.0F, bone.pos.z() / 16.0F);
            }

            poseStack.translate(bone.pivot.x() / 16.0F, bone.pivot.y() / 16.0F, bone.pivot.z() / 16.0F);

            Vector3f rot = bone.getRotation();
            Quaternionf rotationQuat = new Quaternionf().rotateZYX(
                    (float) Math.toRadians(rot.z()),
                    (float) Math.toRadians(-rot.y()),
                    (float) Math.toRadians(-rot.x())
            );
            poseStack.mulPose(rotationQuat);

            if (bone.scale.x() != 0.0F && bone.scale.y() != 0.0F && bone.scale.z() != 0.0F) {
                if (bone.scale.x() != 1.0F || bone.scale.y() != 1.0F || bone.scale.z() != 1.0F) {
                    poseStack.scale(bone.scale.x(), bone.scale.y(), bone.scale.z());
                }
            }

            poseStack.translate(-bone.pivot.x() / 16.0F, -bone.pivot.y() / 16.0F, -bone.pivot.z() / 16.0F);
        }
    }
}