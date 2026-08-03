package it.bluebird.bluebirdlib.client.cosmetics;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.http.Http;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CosmeticsManager {
    private static final Map<UUID, List<String>> ICONS_CACHE = new HashMap<>();

    public static void loadPlayerCosmetics(UUID playerUuid) {
        String uuidStr = playerUuid.toString();

        Http.getPlayerIcons(uuidStr).thenAccept(icons -> {
            ICONS_CACHE.put(playerUuid, icons);

            if (!icons.isEmpty()) {
                BluebirdLib.LOGGER.info("[CosmeticManager]: Loaded icons for player: " + playerUuid + " | icons: " + icons);
                Minecraft.getInstance().execute(() -> {
                    if (Minecraft.getInstance().getConnection() != null) {
                        PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(playerUuid);
                        if (info != null) {
                            info.setTabListDisplayName(info.getTabListDisplayName());
                        }
                    }
                });
            }
        });
    }

    public static void clearPlayerCosmetics(UUID playerUuid) {
        ICONS_CACHE.remove(playerUuid);
    }

    public static List<String> getIcons(UUID playerUuid) {
        return ICONS_CACHE.getOrDefault(playerUuid, List.of());
    }

    public static String getFontCharForIcon(String icon) {
        return switch (icon) {
            case "ls" -> "\uE008";
            case "bb" -> "\uE003";
            case "bb_boss" -> "\uE006";
            case "bb_developer" -> "\uE002";
            case "bug_hunter" -> "\uE000";
            case "bb_artist" -> "\uE001";
            case "bb_sound" -> "\uE004";
            case "bb_writer" -> "\uE005";
            case "bb_media" -> "\uE007";
            case null, default -> "";
        };
    }
}