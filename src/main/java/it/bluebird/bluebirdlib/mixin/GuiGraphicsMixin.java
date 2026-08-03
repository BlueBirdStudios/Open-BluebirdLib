package it.bluebird.bluebirdlib.mixin;

import it.bluebird.bluebirdlib.data.ItemStackCooldown;
import it.bluebird.bluebirdlib.registry.DataComponentRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"))
    private void cooldown(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        ItemStackCooldown cooldown = stack.get(DataComponentRegistry.STACK_COOLDOWN.get());
        if (cooldown == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        long gameTime = minecraft.level.getGameTime();
        if (cooldown.finishTick() <= gameTime) {
            return;
        }

        long longTime = cooldown.finishTick() - gameTime;
        float percent = (float) longTime / cooldown.totalDuration();
        percent = Mth.clamp(percent, 0F, 1F);

        GuiGraphics self = (GuiGraphics) (Object) this;
        int offset = Mth.floor(16F * (1F - percent));

        self.pose().pushPose();
        self.pose().translate(0, 0, 200.0F);

        self.fill(RenderType.guiOverlay(), x, y + offset, x + 16, y + 16, 2147483647);
        self.flush();

        self.pose().popPose();
    }
}