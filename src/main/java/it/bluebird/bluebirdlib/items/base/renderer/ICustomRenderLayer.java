package it.bluebird.bluebirdlib.items.base.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.bluebird.bluebirdlib.data.AnimationSequence;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public interface ICustomRenderLayer<T> {
    AnimationSequence getLayerAnimationSequence(ItemStack itemStack);
    ResourceLocation getLayerTextureLocation(ItemStack itemStack);
    ResourceLocation getModelLocation(ItemStack itemStack);

    default void render2dLayer(GuiGraphics guiGraphics, ItemStack itemStack, int tickCount) {
        if (!isVisible(itemStack))
            return;
    }

    default void render3dLayer(PoseStack poseStack, ItemStack itemStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        if (!isVisible(itemStack))
            return;
    }

    boolean isVisible(ItemStack itemStack);

    default int getMaxLayerHeight() {
        return 128;
    }
}