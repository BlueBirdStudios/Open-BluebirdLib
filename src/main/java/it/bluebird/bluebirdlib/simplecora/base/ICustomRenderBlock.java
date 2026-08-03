package it.bluebird.bluebirdlib.simplecora.base;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.simplecora.animations.AnimationStorage;
import it.bluebird.bluebirdlib.simplecora.animations.components.Animation;
import it.bluebird.bluebirdlib.simplecora.animations.components.AnimationSequence;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public interface ICustomRenderBlock<T> {
    ResourceLocation getTextureLocation(T block);
    ResourceLocation getModelLocation(T block);
    AnimationSequence getAnimationSequence(T block);

    default Map<String, Animation> getAnimations(T block) {
        return AnimationStorage.getAnimations(
                ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "animations/default")
        );
    }
}