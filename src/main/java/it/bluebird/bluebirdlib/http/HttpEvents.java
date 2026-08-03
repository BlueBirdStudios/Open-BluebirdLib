package it.bluebird.bluebirdlib.http;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.client.cosmetics.CosmeticsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = BluebirdLib.MODID, value = Dist.CLIENT)
public class HttpEvents {
    @SubscribeEvent
    public static void onClientLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            CosmeticsManager.loadPlayerCosmetics(localPlayer.getGameProfile().getId());
        }
    }

    @SubscribeEvent
    public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            CosmeticsManager.clearPlayerCosmetics(localPlayer.getGameProfile().getId());
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player targetPlayer) {
            CosmeticsManager.loadPlayerCosmetics(targetPlayer.getGameProfile().getId());
        }
    }

    @SubscribeEvent
    public static void onPlayerStopTracking(PlayerEvent.StopTracking event) {
        if (event.getTarget() instanceof Player targetPlayer) {
            CosmeticsManager.clearPlayerCosmetics(targetPlayer.getGameProfile().getId());
        }
    }
}