package it.bluebird.bluebirdlib.data;

import lombok.Data;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class LootConfig {
    private final Item dropItem;
    private final List<LootTarget> targets = new ArrayList<>();

    public LootConfig target(String lootTableId, float chance) {
        targets.add(new LootTarget(lootTableId, chance));
        return this;
    }

    public record LootTarget(String id, float chance) {}
}