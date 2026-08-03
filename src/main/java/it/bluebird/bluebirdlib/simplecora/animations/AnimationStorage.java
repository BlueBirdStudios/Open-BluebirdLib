package it.bluebird.bluebirdlib.simplecora.animations;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.simplecora.animations.components.Animation;
import it.bluebird.bluebirdlib.simplecora.animations.components.AnimationSet;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AnimationStorage {
    private static final Map<ResourceLocation, AnimationSet> STORAGE = new HashMap<>();
    private static final String ANIMATION_SUFFIX = ".animation.json";

    public static void storeAnimation(ResourceLocation key, Map<String, Animation> data) {
        STORAGE.put(key, new AnimationSet(data));
    }

    public static AnimationSet getAnimations(String id) {
        return getAnimationSet(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "animations/" + id));
    }

    public static Animation getAnimationByKey(String animationKey, float animationLength) {
        for (AnimationSet set : STORAGE.values()) {
            Animation animation = set.get(animationKey);
            if (animation != Animation.EMPTY && animation.getAnimationLength() == animationLength) {
                return animation;
            }
        }
        return Animation.EMPTY;
    }

    public static AnimationSet getAnimationSet(ResourceLocation path) {
        ResourceLocation fixedPath = ensureJsonSuffix(path);
        AnimationSet set = STORAGE.get(fixedPath);

        return set != null ? set : new AnimationSet(new HashMap<>());
    }

    public static Animation getAnimation(ResourceLocation path, String key) {
        ResourceLocation fixedPath = ensureJsonSuffix(path);
        AnimationSet set = STORAGE.get(fixedPath);

        return set != null ? set.get(key) : Animation.EMPTY;
    }

    private static ResourceLocation ensureJsonSuffix(ResourceLocation path) {
        if (!path.getPath().endsWith(ANIMATION_SUFFIX)) {
            return ResourceLocation.fromNamespaceAndPath(path.getNamespace(), path.getPath() + ANIMATION_SUFFIX);
        }
        return path;
    }

    @Deprecated
    public static Map<String, Animation> getAnimations(ResourceLocation path) {
        ResourceLocation fixedPath = ensureJsonSuffix(path);
        AnimationSet set = STORAGE.get(fixedPath);

        return set != null ? new HashMap<>(set.getAnimations()) : new HashMap<>();
    }
}