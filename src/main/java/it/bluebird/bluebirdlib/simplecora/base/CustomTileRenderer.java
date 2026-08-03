package it.bluebird.bluebirdlib.simplecora.base;

import com.mojang.blaze3d.vertex.PoseStack;
import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.simplecora.animations.AnimationStorage;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimatedTile;
import it.bluebird.bluebirdlib.simplecora.animations.components.Animation;
import it.bluebird.bluebirdlib.simplecora.animations.components.AnimationSequence;
import it.bluebird.bluebirdlib.simplecora.animations.components.GeometryChanges;
import it.bluebird.bluebirdlib.simplecora.animations.controller.AnimationController;
import it.bluebird.bluebirdlib.simplecora.geometry.GeometryStorage;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeometryData;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class CustomTileRenderer<T extends BlockEntity & IAnimatedTile> implements BlockEntityRenderer<T>, ICustomRenderBlock<T> {
    private final String index;

    public CustomTileRenderer(String index) {
        this.index = index;
    }

    @Override
    public ResourceLocation getTextureLocation(T block) {
        return ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "textures/block/" + this.index + ".png");
    }

    @Override
    public ResourceLocation getModelLocation(T block) {
        return ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "geometry/" + this.index);
    }

    @Override
    public AnimationSequence getAnimationSequence(T block) {
        return AnimationSequence.builder().build();
    }

    @Override
    public Map<String, Animation> getAnimations(T block) {
        return AnimationStorage.getAnimations(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "animations/" + this.index));
    }

    @Override
    public void render(@NotNull T tile, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 0.0D, 0.5D);

        GeometryData model = GeometryStorage.getGeometry(this.getModelLocation(tile));
        AnimationController controller = tile.getController();
        GeometryChanges changes = controller.apply(partialTicks);

        model.applyAnimChanges(changes);

        RenderType renderType = RenderType.entityCutout(this.getTextureLocation(tile));
        model.renderModel(poseStack, bufferSource.getBuffer(renderType), packedLight, packedOverlay);

        poseStack.popPose();
    }

    public String getIndex() {
        return this.index;
    }
}