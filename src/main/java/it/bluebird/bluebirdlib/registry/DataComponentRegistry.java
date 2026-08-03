package it.bluebird.bluebirdlib.registry;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.data.ItemStackCooldown;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponentRegistry {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, BluebirdLib.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemStackCooldown>> STACK_COOLDOWN =
            DATA_COMPONENT_TYPES.register("stack_cooldown", () ->
                    DataComponentType.<ItemStackCooldown>builder()
                            .persistent(ItemStackCooldown.CODEC)
                            .networkSynchronized(ItemStackCooldown.STREAM_CODEC)
                            .build()
            );
}