package it.bluebird.bluebirdlib.simplecora.animations.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.simplecora.animations.AnimationStorage;
import it.bluebird.bluebirdlib.simplecora.animations.components.Animation;
import it.bluebird.bluebirdlib.simplecora.animations.components.InterpolationType;
import it.bluebird.bluebirdlib.simplecora.animations.components.bone.BoneAnimation;
import it.bluebird.bluebirdlib.simplecora.animations.components.bone.Keyframe;
import it.bluebird.bluebirdlib.simplecora.animations.molang.MolangVector3f;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import it.bluebird.bluebirdlib.registry.AnimationRegistry;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class AnimationsLoader {
    private static final Gson GSON = new Gson();

    public static void init() {
        AnimationRegistry.waitForAllRegistrations()
                .thenCompose(aVoid -> loadAnimations())
                .thenRun(() -> BluebirdLib.LOGGER.info("All controllers loaded successfully."));
    }

    public static CompletableFuture<Void> loadAnimations() {
        return CompletableFuture.runAsync(() -> {
            AnimationRegistry.getRegistry().forEach((key, path) -> {
                ResourceLocation location = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), path);
                String jsonString = getJsonStringFromResource(location);

                if (jsonString != null) {
                    JsonObject jsonObject = GSON.fromJson(jsonString, JsonObject.class);
                    BluebirdLib.LOGGER.info("Loaded animation JSON for {}", key);

                    Map<String, Animation> parsed = parseAnimations(location, jsonObject);
                    if (parsed != null) {
                        AnimationStorage.storeAnimation(key, parsed);
                    }
                } else {
                    BluebirdLib.LOGGER.error("Failed to load animation JSON from resource: {}", location);
                }
            });
        });
    }

    public static Map<String, Animation> parseAnimations(ResourceLocation location, JsonObject jsonObject) {
        try {
            JsonObject animationsNode = jsonObject.getAsJsonObject("animations");
            Map<String, Animation> animationMap = new HashMap<>();

            for (Map.Entry<String, JsonElement> entry : animationsNode.entrySet()) {
                String animationName = entry.getKey();
                JsonObject animationObj = entry.getValue().getAsJsonObject();

                float animationLength = animationObj.has("animation_length")
                        ? animationObj.get("animation_length").getAsFloat()
                        : 0.0F;

                JsonObject bonesNode = animationObj.getAsJsonObject("bones");

                Map<String, BoneAnimation> boneAnimations = bonesNode.entrySet().stream().collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                boneEntry -> parseBoneAnimation(location, boneEntry.getValue().getAsJsonObject(), boneEntry.getKey())
                        )
                );

                animationMap.put(animationName, new Animation(animationName, boneAnimations, animationLength));
            }

            return animationMap;
        } catch (Exception e) {
            BluebirdLib.LOGGER.error("Failed to parse animation from resource {}: {}", location, e.getMessage());
            return null;
        }
    }

    private static BoneAnimation parseBoneAnimation(ResourceLocation location, JsonObject boneObject, String boneName) {
        try {
            BoneAnimation boneAnimation = new BoneAnimation();
            boneAnimation.setName(boneName);

            if (boneObject.has("rotation")) {
                boneAnimation.setRotation(parseKeyframes(boneObject.get("rotation")));
            }
            if (boneObject.has("position")) {
                boneAnimation.setPosition(parseKeyframes(boneObject.get("position")));
            }
            if (boneObject.has("scale")) {
                boneAnimation.setScale(parseKeyframes(boneObject.get("scale")));
            }

            return boneAnimation;
        } catch (Exception e) {
            BluebirdLib.LOGGER.error("Failed to parse BoneAnimation '{}' from resource {}: {}", boneName, location, e.getMessage());
            return new BoneAnimation();
        }
    }

    private static Map<Float, Keyframe> parseKeyframes(JsonElement keyframesElement) {
        Map<Float, Keyframe> keyframes = new HashMap<>();

        if (keyframesElement.isJsonArray()) {
            JsonArray arr = keyframesElement.getAsJsonArray();
            if (arr.size() == 3) {
                if (hasString(arr)) {
                    MolangVector3f molang = parseMolangVector(arr);
                    keyframes.put(0.0F, new Keyframe(0.0F, null, molang, "linear", InterpolationType.LINEAR));
                } else {
                    Vector3f vector = parseVector(arr);
                    keyframes.put(0.0F, new Keyframe(0.0F, vector, null, "linear", InterpolationType.LINEAR));
                }
            }
        }
        else if (keyframesElement.isJsonObject()) {
            JsonObject keyframesJson = keyframesElement.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : keyframesJson.entrySet()) {
                float time;
                try {
                    time = Float.parseFloat(entry.getKey());
                } catch (NumberFormatException e) {
                    continue;
                }

                JsonElement valueElement = entry.getValue();
                Vector3f vector = null;
                MolangVector3f molang = null;
                String easing = "linear";
                InterpolationType interpolation = InterpolationType.LINEAR;
                JsonArray vectorArray = null;

                if (valueElement.isJsonObject()) {
                    JsonObject keyframeObject = valueElement.getAsJsonObject();

                    if (keyframeObject.has("vector") && keyframeObject.get("vector").isJsonArray()) {
                        vectorArray = keyframeObject.getAsJsonArray("vector");
                    }
                    else if (keyframeObject.has("post")) {
                        JsonElement postElement = keyframeObject.get("post");
                        if (postElement.isJsonArray()) {
                            vectorArray = postElement.getAsJsonArray();
                        } else if (postElement.isJsonObject()) {
                            JsonObject postObj = postElement.getAsJsonObject();
                            if (postObj.has("vector") && postObj.get("vector").isJsonArray()) {
                                vectorArray = postObj.getAsJsonArray("vector");
                            }
                            if (postObj.has("easing")) {
                                easing = postObj.get("easing").getAsString();
                            }
                        }
                    }

                    if (keyframeObject.has("easing")) {
                        easing = keyframeObject.get("easing").getAsString();
                    }
                    if (keyframeObject.has("lerp_mode")) {
                        interpolation = InterpolationType.fromString(keyframeObject.get("lerp_mode").getAsString());
                    }
                } else if (valueElement.isJsonArray()) {
                    vectorArray = valueElement.getAsJsonArray();
                }

                if (vectorArray != null && vectorArray.size() == 3) {
                    if (hasString(vectorArray)) {
                        molang = parseMolangVector(vectorArray);
                    } else {
                        vector = parseVector(vectorArray);
                    }
                }

                keyframes.put(time, new Keyframe(time, vector, molang, easing, interpolation));
            }
        }

        return keyframes;
    }

    private static boolean hasString(JsonArray arr) {
        for (JsonElement el : arr) {
            if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
                return true;
            }
        }
        return false;
    }

    private static MolangVector3f parseMolangVector(JsonArray arr) {
        String x = parseMolangComponent(arr.get(0));
        String y = parseMolangComponent(arr.get(1));
        String z = parseMolangComponent(arr.get(2));
        return new MolangVector3f(x, y, z);
    }

    private static String parseMolangComponent(JsonElement el) {
        if (el.isJsonPrimitive()) {
            if (el.getAsJsonPrimitive().isString()) {
                return el.getAsString();
            }
            if (el.getAsJsonPrimitive().isNumber()) {
                return Float.toString(el.getAsFloat());
            }
        }
        return "";
    }

    private static Vector3f parseVector(JsonArray vectorArray) {
        if (vectorArray.size() >= 3) {
            float x = vectorArray.get(0).getAsFloat();
            float y = vectorArray.get(1).getAsFloat();
            float z = vectorArray.get(2).getAsFloat();
            return new Vector3f(x, y, z);
        }
        return new Vector3f(0.0F, 0.0F, 0.0F);
    }

    private static String getJsonStringFromResource(ResourceLocation path) {
        String resourcePath = "assets/" + path.getNamespace() + "/" + path.getPath();
        InputStream inputStream = AnimationsLoader.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            BluebirdLib.LOGGER.error("Resource not found: {}", resourcePath);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line).append(System.lineSeparator());
            }
            return jsonString.toString();
        } catch (IOException e) {
            BluebirdLib.LOGGER.error("Error reading resource: {}", resourcePath, e);
            return null;
        }
    }
}