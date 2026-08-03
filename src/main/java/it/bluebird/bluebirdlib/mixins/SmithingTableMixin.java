package it.bluebird.bluebirdlib.mixins;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

//@Mixin(SmithingMenu.class)
public class SmithingTableMixin {
//
//    @Shadow
//    @Final
//    private List<RecipeHolder<SmithingRecipe>> recipes;
//
//    /**
//     * @author Mojang
//     * @reason Overwriting for modifying smithing table block on use items in slots
//     */
//    @Overwrite
//    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
//        List<String> acceptedItemsTemplate = new ArrayList<String>(); // Template slot(0)
//        List<String> acceptedItemsBase = new ArrayList<String>(); //Base slot(1)
//        List<String> acceptedItemsAddition = new ArrayList<String>(); //Addition slot(2)
//
//        acceptedItemsTemplate.add("minecraft:air");
//        acceptedItemsBase.add("minecraft:air");
//        acceptedItemsAddition.add("minecraft:air");
//        return ItemCombinerMenuSlotDefinition.create()
//                .withSlot(0, 8, 48, maybeTemplateItem -> (recipes.stream().anyMatch(smithingReciepeTemplate -> smithingReciepeTemplate.value().isTemplateIngredient(maybeTemplateItem))||acceptedItemsTemplate.contains(BuiltInRegistries.ITEM.getKey(maybeTemplateItem.getItem()).toString())))
//                .withSlot(1, 26, 48, maybeBaseItem -> recipes.stream().anyMatch(smithingReciepeBase -> smithingReciepeBase.value().isBaseIngredient(maybeBaseItem))||acceptedItemsBase.contains(BuiltInRegistries.ITEM.getKey(maybeBaseItem.getItem()).toString()))
//                .withSlot(2, 44, 48, maybeAddtionItem -> recipes.stream().anyMatch(smithingReciepeAddition -> smithingReciepeAddition.value().isAdditionIngredient(maybeAddtionItem))||acceptedItemsAddition.contains(BuiltInRegistries.ITEM.getKey(maybeAddtionItem.getItem()).toString()))
//                .withResultSlot(3, 98, 48)
//                .build();
//    }
}
