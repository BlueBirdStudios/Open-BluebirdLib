package it.bluebird.bluebirdlib.entity.base.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimated;
import it.bluebird.bluebirdlib.simplecora.geometry.GeometryStorage;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeometryData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public abstract class CustomEntityRenderer<T extends Entity & IAnimated> extends EntityRenderer<T> implements ICustomRenderEntity<T> {
    public CustomEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public ResourceLocation getTextureLocation(T block) {
        return ResourceLocation.fromNamespaceAndPath("bluebirdlib", "textures/item/default_texture.png");
    }

    public ResourceLocation getModelLocation(T block) {
        return ResourceLocation.fromNamespaceAndPath("bluebirdlib", "bb_geo/default_model.bb_geo.json");
    }

    public void render(T entity, float p_114486_, float p_114487_, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn) {
        poseStack.pushPose();
        GeometryData model = GeometryStorage.getGeometry(this.getModelLocation(entity));
        model.renderModel(poseStack, bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity))), packedLightIn, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}