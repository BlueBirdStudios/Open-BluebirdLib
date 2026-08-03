package it.bluebird.bluebirdlib.level;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.bluebird.bluebirdlib.data.LootConfig;
import it.bluebird.bluebirdlib.registry.CodecRegistry;
import it.bluebird.bluebirdlib.items.base.ILootModifiedItem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BBLootModifier extends LootModifier {
    public static final Supplier<MapCodec<BBLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, BBLootModifier::new)));

    public BBLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }


    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> loot, LootContext ctx) {
        String table = ctx.getQueriedLootTableId().toString();

        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof ILootModifiedItem)
                .map(item -> ((ILootModifiedItem) item).getLootConfig())
                .forEach(cfg -> {
                    for (LootConfig.LootTarget target : cfg.getTargets()) {
                        if (target.id().equals(table) && ctx.getRandom().nextFloat() <= target.chance()) {
                            loot.add(cfg.getDropItem().getDefaultInstance());
                        }
                    }
                });

        return loot;
    }
    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CodecRegistry.LOOT_MODIFIER.get();
    }
}
