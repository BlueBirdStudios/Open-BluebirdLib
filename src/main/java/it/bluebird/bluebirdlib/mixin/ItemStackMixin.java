package it.bluebird.bluebirdlib.mixin;

import it.bluebird.bluebirdlib.data.ItemStackCooldown;
import it.bluebird.bluebirdlib.registry.DataComponentRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void preventUseIfCooldown(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (hasActiveStackCooldown(level)) {
            ItemStack self = (ItemStack) (Object) this;
            cir.setReturnValue(InteractionResultHolder.fail(self));
        }
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void preventUseOnIfCooldown(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (hasActiveStackCooldown(context.getLevel())) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }

    @Unique
    private boolean hasActiveStackCooldown(Level level) {
        ItemStack self = (ItemStack) (Object) this;
        ItemStackCooldown cooldown = self.get(DataComponentRegistry.STACK_COOLDOWN.get());
        if (cooldown != null) {
            return level.getGameTime() < cooldown.finishTick();
        }
        return false;
    }
}