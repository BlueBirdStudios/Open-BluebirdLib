package it.bluebird.bluebirdlib.mixin;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.client.cosmetics.CosmeticsManager;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabMixin {
    @Inject(method = "getNameForDisplay", at = @At("RETURN"), cancellable = true)
    private void modifyTabName(PlayerInfo info, CallbackInfoReturnable<Component> cir) {
        Component original = cir.getReturnValue();

        if (original == null) {
            original = Component.literal(info.getProfile().getName());
        }

        List<String> icons = CosmeticsManager.getIcons(info.getProfile().getId());
        if (!icons.isEmpty()) {
            MutableComponent newName = Component.empty();

            for (String icon : icons) {
                String iconChar = CosmeticsManager.getFontCharForIcon(icon);
                Component iconComponent = Component.literal(iconChar)
                        .withStyle(style -> style.withFont(BluebirdLib.ICONS_FONT));
                newName.append(iconComponent);
            }
            newName.append(Component.literal(" ").withStyle(Style.EMPTY));
            newName.append(original);
            cir.setReturnValue(newName);
        }
    }
}