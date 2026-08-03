package it.bluebird.bluebirdlib.utils;

import it.bluebird.bluebirdlib.data.ItemStackCooldown;
import it.bluebird.bluebirdlib.registry.DataComponentRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemUtils {
    public ItemUtils() {
    }

    public static void setCooldown(ItemStack stack, Player player, int ticks) {
        long finishTime = player.level().getGameTime() + (long)ticks;
        stack.set(DataComponentRegistry.STACK_COOLDOWN.get(), new ItemStackCooldown(finishTime, ticks));
    }

    public static boolean isOnCooldown(ItemStack stack, Player player) {
        if (!stack.has(DataComponentRegistry.STACK_COOLDOWN.get())) {
            return false;
        } else {
            ItemStackCooldown data = stack.get(DataComponentRegistry.STACK_COOLDOWN.get());
            return data != null && player.level().getGameTime() < data.finishTick();
        }
    }

    public static float getCooldownProgress(ItemStack stack, float partialTick, long clientGameTime) {
        if (!stack.has(DataComponentRegistry.STACK_COOLDOWN.get())) {
            return 0.0F;
        } else {
            ItemStackCooldown data = stack.get(DataComponentRegistry.STACK_COOLDOWN.get());
            if (data == null || clientGameTime >= data.finishTick()) {
                return 0.0F;
            } else {
                double remaining = (double)(data.finishTick() - clientGameTime);
                return (float)(remaining / (double)data.totalDuration());
            }
        }
    }
}