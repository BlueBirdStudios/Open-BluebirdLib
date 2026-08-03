package it.bluebird.bluebirdlib.items.base.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.bluebird.bluebirdlib.data.AnimationSequence;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeometryData;
import it.bluebird.bluebirdlib.utils.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public interface ICustomItemRenderer<T> {
    void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay);
    void renderLayers(GuiGraphics guiGraphics, ItemStack itemStack, int tickCount);
    void renderLayers(PoseStack poseStack, ItemStack itemStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay);

    ResourceLocation getTextureLocation(ItemStack itemStack);
    ResourceLocation get2dModelLocation(ItemStack itemStack);
    ResourceLocation getGuiTextureLocation(ItemStack itemStack);
    ResourceLocation getModelLocation(ItemStack itemStack);
    AnimationSequence getAnimationSequence(ItemStack itemStack);

    default void postRender(GuiGraphics guiGraphics, ItemStack itemStack, int tickCount) {
        GuiUtils.drawAnimatedTexture(guiGraphics, getGuiTextureLocation(itemStack), 0, 0, 16, 16, tickCount, getAnimationSequence(itemStack), getMaxHeight());
    }

    default void setAlphaBones(Set<String> alphaBones) {}
    default void preTranclateMatrix(PoseStack poseStack, ItemStack itemStack) {}
    void setCustomAnimation(GeometryData data, ItemStack itemStack);

    default int getMaxHeight() {
        return 128;
    }

    float getOffX();
    float getOffY();
}