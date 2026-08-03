//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package it.bluebird.bluebirdlib.registry;

import it.bluebird.bluebirdlib.BluebirdLib;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resources.ResourceLocation;

public class AnimationRegistry {
    public static final Map<ResourceLocation, String> REGISTRY = new HashMap();
    private static final List<CompletableFuture<Void>> registrationFutures = new ArrayList();

    public AnimationRegistry() {
    }

    public static void registerAnimation(ResourceLocation key) {
        String path = key.getPath();
        if (!path.endsWith(".animation.json")) {
            if (path.endsWith(".json")) {
                String var10000 = path.substring(0, path.length() - 5);
                path = var10000 + ".animation.json";
            } else {
                path = path + ".animation.json";
            }
        }

        BluebirdLib.LOGGER.info("Register Animation: " + path);
        REGISTRY.put(key, path);
    }

    public static CompletableFuture<Void> waitForAllRegistrations() {
        return CompletableFuture.allOf((CompletableFuture[])registrationFutures.toArray(new CompletableFuture[0]));
    }

    public static CompletableFuture<Void> registerAnimationsFolder(ResourceLocation folderKey) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                ClassLoader var10000 = AnimationRegistry.class.getClassLoader();
                String var10001 = folderKey.getNamespace();
                Path modelsPath = Paths.get(var10000.getResource("assets/" + var10001 + "/" + folderKey.getPath()).toURI());
                Files.walk(modelsPath).filter((x$0) -> Files.isRegularFile(x$0, new LinkOption[0])).forEach((file) -> {
                    String relativePath = modelsPath.relativize(file).toString().replace("\\", "/");
                    if (relativePath.endsWith(".animation.json")) {
                        String ss = folderKey.getNamespace();
                        String sss = folderKey.getPath();
                        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(ss, sss + "/" + relativePath);
                        registerAnimation(key);
                    }

                });
            } catch (Exception e) {
                BluebirdLib.LOGGER.error("Error scanning folder: {}", folderKey.getPath(), e);
            }

        });
        registrationFutures.add(future);
        return future;
    }

    public static Map<ResourceLocation, String> getRegistry() {
        return REGISTRY;
    }

    public static void init() {
        registerAnimationsFolder(ResourceLocation.fromNamespaceAndPath("bluebirdlib", "animations"));
    }
}