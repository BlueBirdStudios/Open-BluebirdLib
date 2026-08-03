package it.bluebird.bluebirdlib.items.base.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.data.AnimationSequence;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimated;
import lombok.Data;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Data
public class CustomItemLayer<T extends Item & IAnimated> implements ICustomRenderLayer<T> {
    public final CustomItemRenderer<T> renderer;

    public CustomItemLayer(CustomItemRenderer<T> renderer) {
        this.renderer = renderer;
    }

    @Override
    public AnimationSequence getLayerAnimationSequence(ItemStack itemStack) {
        return AnimationSequence.builder().addFrame(0,2).build();
    }

    @Override
    public ResourceLocation getLayerTextureLocation(ItemStack itemStack) {
        return ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "textures/item/default_texture.png");
    }

    @Override
    public ResourceLocation getModelLocation(ItemStack itemStack) {
        return getRenderer().getModelLocation(itemStack);
    }

    @Override
    public void render2dLayer(GuiGraphics guiGraphics, ItemStack itemStack, int tickCount) {
    }

    @Override
    public boolean isVisible(ItemStack itemStack) {
        return true;
    }

    @Override
    public void render3dLayer(PoseStack poseStack, ItemStack itemStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
    }
}