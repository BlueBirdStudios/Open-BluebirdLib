package it.bluebird.bluebirdlib.blocks.base.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.bluebird.bluebirdlib.data.AnimationSequence;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimated;
import it.bluebird.bluebirdlib.simplecora.geometry.GeometryStorage;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeometryData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CustomBlockRenderer<T extends BlockEntity & IAnimated> implements BlockEntityRenderer<T>, ICustomRenderBlock<T> {
    public CustomBlockRenderer() {
    }

    @Override
    public ResourceLocation getTextureLocation(BlockEntity blockEntity) {
        return ResourceLocation.fromNamespaceAndPath("bluebirdlib", "textures/item/default_texture.png");
    }

    @Override
    public ResourceLocation getModelLocation(BlockEntity blockEntity) {
        return ResourceLocation.fromNamespaceAndPath("bluebirdlib", "bb_geo/default_model.bb_geo.json");
    }

    @Override
    public AnimationSequence getAnimationSequence(BlockEntity blockEntity) {
        return AnimationSequence.builder().addFrame(0, 2).build();
    }

    @Override
    public void render(T blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        GeometryData model = GeometryStorage.getGeometry(this.getModelLocation(blockEntity));
        poseStack.translate(0.5F, 0.0F, 0.5F);

        model.renderModel(
                poseStack,
                bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(blockEntity))),
                packedLight,
                packedOverlay
        );

        poseStack.popPose();
    }
}