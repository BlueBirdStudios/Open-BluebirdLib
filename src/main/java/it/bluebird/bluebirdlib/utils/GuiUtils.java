package it.bluebird.bluebirdlib.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.bluebird.bluebirdlib.data.AnimationFrame;
import it.bluebird.bluebirdlib.data.AnimationSequence;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class GuiUtils {
    public static void drawAnimatedTexture(GuiGraphics guiGraphics, ResourceLocation texture, float x, float y, float width, float height, long ticks, AnimationSequence animationSequence, int maxHeight) {
        AnimationFrame currentFrame = animationSequence.getFrameForTime(ticks);
        int frameIndex = currentFrame.getFrameIndex();

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        guiGraphics.blit(texture, (int) x, (int) y,0,height*frameIndex, (int) width, (int) height, (int) 16, maxHeight);

        RenderSystem.disableBlend();
    }

    public static void drawAnimatedTextureByStage(GuiGraphics guiGraphics, ResourceLocation texture, float x, float y, float width, float height,int frameIndex, int maxHeight) {
        guiGraphics.blit(texture, (int) x, (int) y,0,height*frameIndex, (int) width, (int) height, (int) 16, maxHeight);
    }
}
