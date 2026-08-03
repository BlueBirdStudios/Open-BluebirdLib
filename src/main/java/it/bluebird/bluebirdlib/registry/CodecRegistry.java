package it.bluebird.bluebirdlib.registry;

import com.mojang.serialization.MapCodec;
import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.level.BBLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CodecRegistry {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BluebirdLib.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<BBLootModifier>> LOOT_MODIFIER = GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("loot_modifier", BBLootModifier.CODEC);
}
