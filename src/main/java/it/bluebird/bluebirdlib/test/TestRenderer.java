/*
package it.bluebird.bluebirdlib.test;

import com.mojang.blaze3d.vertex.PoseStack;
import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.bbanimations.animations.AnimationController;
import it.bluebird.bluebirdlib.bbanimations.animations.AnimationStorage;
import it.bluebird.bluebirdlib.bbanimations.animations.components.AnimationPoint;
import it.bluebird.bluebirdlib.bbanimations.animations.components.LoopMode;
import it.bluebird.bluebirdlib.bbanimations.animations.data.Animation;
import it.bluebird.bluebirdlib.bbanimations.geometry.data.GeometryData;
import it.bluebird.bluebirdlib.bbanimations.geometry.GeometryStorage;
import it.bluebird.bluebirdlib.entity.base.renderer.CustomEntityRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;

public class TestRenderer extends CustomEntityRenderer<TestEntity> {
    public TestRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getModelLocation(TestEntity block) {
        return ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "geo/ancient_spark.geo.json");
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TestEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "3d_ancient_spark.png");
    }

    @Override
    public void render(TestEntity entity, float p_114486_, float p_114487_, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn) {
        poseStack.pushPose();

        GeometryData model = GeometryStorage.getGeometry(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "geo/ancient_spark.geo.json"));
        GeometryData model2 = GeometryStorage.getGeometry(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "geo/ancient_form.geo.json"));
        GeometryData model3 = GeometryStorage.getGeometry(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "geo/ancient_cascade.geo.json"));

//        AnimationController controller = entity.controller;
//        controller.setModel(model);
//
//        controller.tickController(p_114487_);
//        controller.tickController(p_114487_);
//        controller.startAnimation("idle",
//                AnimationStorage.getAnimations(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID,
//                        "animations/spark_meteor.animation.json")).get("animation.model.new"), LoopMode.LOOP, true);

        model.renderEntityModel(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity))), packedLightIn, OverlayTexture.NO_OVERLAY, Color.WHITE);
        poseStack.translate(1,0,0);
        model2.renderEntityModel(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "3d_ancient_form.png"))), packedLightIn, OverlayTexture.NO_OVERLAY, Color.WHITE);
        poseStack.translate(-2,0,0);
        model2.renderEntityModel(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "3d_ancient_cascade.png"))), packedLightIn, OverlayTexture.NO_OVERLAY, Color.WHITE);

        poseStack.popPose();
    }
}*/
