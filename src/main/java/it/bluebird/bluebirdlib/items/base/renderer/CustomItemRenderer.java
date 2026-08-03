package it.bluebird.bluebirdlib.items.base.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.bluebird.bluebirdlib.data.AnimationFrame;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimated;
import it.bluebird.bluebirdlib.simplecora.geometry.GeometryStorage;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeometryData;
import it.bluebird.bluebirdlib.items.base.renderer.layer.LayerStorage;
import it.bluebird.bluebirdlib.data.AnimationSequence;
import it.bluebird.bluebirdlib.utils.GuiUtils;
import it.bluebird.bluebirdlib.BluebirdLib;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Set;

public class CustomItemRenderer<T extends Item & IAnimated> extends BlockEntityWithoutLevelRenderer implements ICustomItemRenderer<T> {

    @Getter
    private final LayerStorage<T> layer2dStorage = new LayerStorage<>();
    @Getter
    private final LayerStorage<T> layer3dStorage = new LayerStorage<>();

    public CustomItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public ResourceLocation getTextureLocation(ItemStack itemStack) {
        return ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "textures/item/default_texture.png");
    }

    @Override
    public ResourceLocation getGuiTextureLocation(ItemStack itemStack) {
        return ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "textures/item/default_texture.png");
    }

    @Override
    public ResourceLocation getModelLocation(ItemStack itemStack) {
        return ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "geometry/empty.geo.json");
    }

    @Override
    public ResourceLocation get2dModelLocation(ItemStack itemStack) {
        return ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "models/item/default");
    }

    @Override
    public float getOffX() {
        return 0f;
    }

    @Override
    public float getOffY() {
        return 0f;
    }

    @Override
    public AnimationSequence getAnimationSequence(ItemStack itemStack) {
        return AnimationSequence.builder().addFrame(0, 2).build();
    }

    @Override
    public void renderLayers(GuiGraphics guiGraphics, ItemStack itemStack, int tickCount) {
        for (ICustomRenderLayer<T> layer : layer2dStorage.getLayers().values()) {
            layer.render2dLayer(guiGraphics, itemStack, tickCount);
        }
    }

    @Override
    public void renderLayers(PoseStack poseStack, ItemStack itemStack, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        for (ICustomRenderLayer<T> layer : layer3dStorage.getLayers().values()) {
            layer.render3dLayer(poseStack, itemStack, bufferSource, partialTick, packedLight, packedOverlay);
        }
    }

    public void add2dLayer(String id, ICustomRenderLayer<T> layer) {
        layer2dStorage.addLayer(id, layer);
    }

    public void add3dLayer(String id, ICustomRenderLayer<T> layer) {
        layer3dStorage.addLayer(id, layer);
    }

    @Override
    public void preTranclateMatrix(PoseStack poseStack, ItemStack itemStack) {
        ICustomItemRenderer.super.preTranclateMatrix(poseStack, itemStack);
    }

    @Override
    public void setCustomAnimation(GeometryData data, ItemStack itemStack) {
    }

    @Override
    public int getMaxHeight() {
        return ICustomItemRenderer.super.getMaxHeight();
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // System.out.println("renderByItem CALLED, context=" + transformType);

        if (transformType == ItemDisplayContext.GUI) {
            this.renderGuiTexture(itemStack, poseStack, bufferSource);
        } else if (transformType == ItemDisplayContext.FIXED) {
            // System.out.println("FIXED branch entered");
            this.renderFlatWorldTexture(itemStack, poseStack, bufferSource, packedLight, packedOverlay);
            // System.out.println("FIXED branch finished without exception");
        } else {
            Minecraft mc = Minecraft.getInstance();
            int tickCount = mc.player != null ? mc.player.tickCount : 0;

            poseStack.pushPose();
            poseStack.translate(0.5F, 0.51F, 0.5F);

            GeometryData model = GeometryStorage.getGeometry(getModelLocation(itemStack));

            preTranclateMatrix(poseStack, itemStack);
            setCustomAnimation(model, itemStack);

            if (model != null && model.getBones() != null && !model.getBones().isEmpty()) {
                model.renderModel(poseStack, bufferSource.getBuffer(RenderType.entityCutout(getTextureLocation(itemStack))),
                        packedLight, packedOverlay);
            }

            renderLayers(poseStack, itemStack, bufferSource, tickCount, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    private void renderFlatWorldTexture(ItemStack itemStack, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        int tickCount = mc.player != null ? mc.player.tickCount : 0;

        ResourceLocation texture = getGuiTextureLocation(itemStack);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));

        AnimationFrame currentFrame = getAnimationSequence(itemStack).getFrameForTime(tickCount);
        int frameIndex = currentFrame.getFrameIndex();
        int maxHeight = getMaxHeight();

        float frameHeightPx = 16F;
        float vOffsetPx = frameHeightPx * frameIndex;

        float minU = 0F;
        float maxU = 1F;
        float minV = vOffsetPx / maxHeight;
        float maxV = (vOffsetPx + frameHeightPx) / maxHeight;

        poseStack.pushPose();
        poseStack.translate(1F, 0F, 1F);
        // poseStack.scale(0.625F, 0.625F, 0.625F);

        Matrix4f pose = poseStack.last().pose();
        float z = 0F;

        consumer.addVertex(pose, -0.5F, -0.5F, z).setColor(255, 255, 255, 255).setUv(minU, maxV)
                .setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        consumer.addVertex(pose, 0.5F, -0.5F, z).setColor(255, 255, 255, 255).setUv(maxU, maxV)
                .setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        consumer.addVertex(pose, 0.5F, 0.5F, z).setColor(255, 255, 255, 255).setUv(maxU, minV)
                .setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
        consumer.addVertex(pose, -0.5F, 0.5F, z).setColor(255, 255, 255, 255).setUv(minU, minV)
                .setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);

        poseStack.popPose();
    }

    public void renderGuiTexture(ItemStack itemStack, PoseStack poseStack, MultiBufferSource bufferSource) {
        Minecraft mc = Minecraft.getInstance();
        int tickCount = mc.player != null ? mc.player.tickCount : 0;
        ResourceLocation texture = getGuiTextureLocation(itemStack);

        Matrix4f oldMatrix = poseStack.last().pose();
        Vector3f translation = new Vector3f();
        oldMatrix.getTranslation(translation);

        PoseStack newPoseStack = new PoseStack();

        GuiGraphics guiGraphics = new GuiGraphics(mc, newPoseStack, (MultiBufferSource.BufferSource) bufferSource);

        newPoseStack.pushPose();
        newPoseStack.translate(translation.x()-getOffX(), translation.y()-getOffY(), translation.z());

        postRender(guiGraphics,itemStack,tickCount);

        renderLayers(guiGraphics,itemStack,tickCount);

        newPoseStack.popPose();
    }

    @Override
    public void postRender(GuiGraphics guiGraphics, ItemStack itemStack, int tickCount) {
        GuiUtils.drawAnimatedTexture(guiGraphics, getGuiTextureLocation(itemStack), 0, 0, 16, 16, tickCount, getAnimationSequence(itemStack), getMaxHeight());
    }

    @Override
    public void setAlphaBones(Set<String> alphaBones) {
        ICustomItemRenderer.super.setAlphaBones(alphaBones);
    }
}