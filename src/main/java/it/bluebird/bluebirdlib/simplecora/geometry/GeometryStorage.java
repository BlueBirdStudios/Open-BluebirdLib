package it.bluebird.bluebirdlib.simplecora.geometry;

import it.bluebird.bluebirdlib.simplecora.geometry.data.GeometryData;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class GeometryStorage {
    private static final Map<ResourceLocation, GeometryData> STORAGE = new HashMap<>();
    private GeometryStorage() {}

    public static void storeGeometry(ResourceLocation key, GeometryData geometryData) {
        STORAGE.put(key, geometryData);
    }

    public static GeometryData getGeometry(String id) {
        return getGeometry(ResourceLocation.fromNamespaceAndPath("bluebirdlib", "geometry/" + id));
    }

    public static GeometryData getGeometry(ResourceLocation key) {
        ResourceLocation finalKey = key.getPath().endsWith(".bb_geo.json")
                ? key
                : ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + ".bb_geo.json");
        return GeometryData.clone(STORAGE.get(finalKey));
    }
}