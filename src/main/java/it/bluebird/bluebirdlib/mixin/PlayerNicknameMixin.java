package it.bluebird.bluebirdlib.mixin;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.client.cosmetics.CosmeticsManager;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(EntityRenderer.class)
public class PlayerNicknameMixin<T extends Entity> {
    @ModifyVariable(
            method = "renderNameTag",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Component modifyNametag(Component original, T entity) {
        if (entity instanceof Player player) {
            List<String> icons = CosmeticsManager.getIcons(player.getGameProfile().getId());
            if (!icons.isEmpty()) {
                if (original.getStyle() != null && original.getStyle().getFont() != null) {
                    if (original.getStyle().getFont().equals(BluebirdLib.ICONS_FONT)) {
                        return original;
                    }
                }
                String rawString = original.getString();
                if (!rawString.isEmpty() && (rawString.contains("\uE000") || rawString.contains("\uE001") || rawString.contains("\uE002"))) {
                    return original;
                }

                MutableComponent newName = Component.empty();
                for (String icon : icons) {
                    String iconChar = CosmeticsManager.getFontCharForIcon(icon);
                    if (!iconChar.isEmpty()) {
                        Component iconComponent = Component.literal(iconChar)
                                .withStyle(style -> style.withFont(BluebirdLib.ICONS_FONT));
                        newName.append(iconComponent);
                    }
                }
                newName.append(Component.literal(" ").withStyle(Style.EMPTY));
                newName.append(original);
                return newName;
            }
        }
        return original;
    }
}