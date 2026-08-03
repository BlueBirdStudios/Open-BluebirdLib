package it.bluebird.bluebirdlib.simplecora.geometry.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.simplecora.geometry.GeometryStorage;
import it.bluebird.bluebirdlib.simplecora.geometry.components.GeometryBone;
import it.bluebird.bluebirdlib.simplecora.geometry.components.GeometryCube;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeoUVs;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeometryData;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeometryUV;
import it.bluebird.bluebirdlib.simplecora.geometry.data.TexVertex;
import it.bluebird.bluebirdlib.registry.GeometryRegistry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class GeometryLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final float PIXEL_TO_BLOCK = 0.0625F;

    private GeometryLoader() {}

    public static void init() {
        GeometryRegistry.waitForAllRegistrations()
                .thenCompose(aVoid -> loadModels())
                .thenRun(() -> BluebirdLib.LOGGER.info("All models loaded."));
    }

    public static CompletableFuture<Void> loadModels() {
        return CompletableFuture.runAsync(() ->
                GeometryRegistry.getRegistry().forEach((key, path) -> {
                    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), path);
                    String jsonString = getJsonStringFromResource(location);

                    if (jsonString != null) {
                        JsonObject jsonObject = GSON.fromJson(jsonString, JsonObject.class);
                        BluebirdLib.LOGGER.info("Loaded geometry for {}", key);
                        GeometryStorage.storeGeometry(key, parseGeometry(location, jsonObject));
                    } else {
                        BluebirdLib.LOGGER.error("Failed to load JSON from resource: {}", location);
                    }
                })
        );
    }

    private static String getJsonStringFromResource(ResourceLocation path) {
        String resourcePath = "assets/" + path.getNamespace() + "/" + path.getPath();
        InputStream inputStream = GeometryLoader.class.getClassLoader().getResourceAsStream(resourcePath);

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

    private static GeometryData parseGeometry(ResourceLocation location, JsonObject geometryObject) {
        JsonArray geometryArray = geometryObject.getAsJsonArray("minecraft:geometry");
        if (geometryArray == null || geometryArray.isEmpty()) {
            BluebirdLib.LOGGER.error("Missing or invalid 'minecraft:geometry' in geometry object: {}, {}", location, geometryObject);
            return null;
        }

        JsonObject firstGeometry = geometryArray.get(0).getAsJsonObject();
        JsonObject description = firstGeometry.getAsJsonObject("description");
        if (description == null) {
            BluebirdLib.LOGGER.error("Missing or invalid 'description' in geometry object: {}, {}", location, firstGeometry);
            return null;
        }

        String identifier = description.get("identifier").getAsString();
        int textureWidth = description.get("texture_width").getAsInt();
        int textureHeight = description.get("texture_height").getAsInt();

        GeometryData geometryData = new GeometryData(identifier, textureWidth, textureHeight);
        JsonArray bonesArray = firstGeometry.getAsJsonArray("bones");

        if (bonesArray != null) {
            Map<String, GeometryBone> boneMap = new HashMap<>();

            for (JsonElement boneElement : bonesArray) {
                JsonObject boneObject = boneElement.getAsJsonObject();
                GeometryBone bone = parseBone(location, boneObject, textureWidth, textureHeight);
                boneMap.put(bone.getName(), bone);
                geometryData.addBone(bone);
            }

            for (GeometryBone bone : boneMap.values()) {
                if (bone.getParent() != null && !bone.getParent().isEmpty()) {
                    GeometryBone parentBone = boneMap.get(bone.getParent());
                    if (parentBone != null) {
                        parentBone.addChildBone(bone);
                    }
                } else {
                    geometryData.getBones().put(bone.getName(), bone);
                }
            }
        } else {
            BluebirdLib.LOGGER.error("Missing or invalid 'bones' in geometry object: {}, {}", location, firstGeometry);
        }

        return geometryData;
    }

    private static GeometryBone parseBone(ResourceLocation location, JsonObject boneObject, int textureWidth, int textureHeight) {
        String name = boneObject.get("name").getAsString();

        Vector3f pivot = parseVector3f(boneObject.getAsJsonArray("pivot"));
        Vector3f rotation = new Vector3f(0.0F, 0.0F, 0.0F);

        if (boneObject.has("rotation")) {
            JsonArray rotationArray = boneObject.getAsJsonArray("rotation");
            rotation = new Vector3f(
                    (float) Math.toRadians(rotationArray.get(0).getAsFloat()),
                    (float) Math.toRadians(rotationArray.get(1).getAsFloat()),
                    (float) Math.toRadians(rotationArray.get(2).getAsFloat())
            );
        }
        rotation.mul(-1.0F, -1.0F, 1.0F);

        Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
        if (boneObject.has("scale")) {
            scale = parseVector3f(boneObject.getAsJsonArray("scale"));
        }

        GeometryBone bone = new GeometryBone(name, pivot, rotation, scale);
        bone.setPivot(new Vector3f(-pivot.x, pivot.y, pivot.z));

        if (boneObject.has("parent")) {
            bone.setParent(boneObject.get("parent").getAsString());
        }

        if (boneObject.has("cubes")) {
            for (JsonElement cubeElement : boneObject.getAsJsonArray("cubes")) {
                GeometryCube cube = parseCube(cubeElement.getAsJsonObject(), textureWidth, textureHeight);
                bone.addCube(cube);
            }
        }

        if (boneObject.has("children")) {
            for (JsonElement childElement : boneObject.getAsJsonArray("children")) {
                GeometryBone childBone = parseBone(location, childElement.getAsJsonObject(), textureWidth, textureHeight);
                bone.addChildBone(childBone);
            }
        }

        return bone;
    }

    private static GeometryCube parseCube(JsonObject cubeObject, int textureWidth, int textureHeight) {
        Vector3f origin = parseVector3f(cubeObject.getAsJsonArray("origin"));
        Vector3f size = parseVector3f(cubeObject.getAsJsonArray("size"));

        Vector3f pivot = cubeObject.has("pivot") ? parseVector3f(cubeObject.getAsJsonArray("pivot")) : new Vector3f(0.0F, 0.0F, 0.0F);
        Vector3f rotation = cubeObject.has("rotation") ? parseVector3f(cubeObject.getAsJsonArray("rotation")) : new Vector3f(0.0F, 0.0F, 0.0F);

        boolean mirror = cubeObject.has("mirror") && cubeObject.get("mirror").getAsBoolean();
        float inflate = cubeObject.has("inflate") ? cubeObject.get("inflate").getAsFloat() : 0.0F;

        origin = new Vector3f(-(origin.x + size.x) * PIXEL_TO_BLOCK, origin.y * PIXEL_TO_BLOCK, origin.z * PIXEL_TO_BLOCK);
        pivot = new Vector3f(-pivot.x, pivot.y, pivot.z);

        rotation.set(
                (float) Math.toRadians(-rotation.x),
                (float) Math.toRadians(-rotation.y),
                (float) Math.toRadians(rotation.z)
        );

        Vector3f vertexSize = new Vector3f(size).mul(PIXEL_TO_BLOCK);
        GeometryCube cube = new GeometryCube(origin, size, pivot, rotation, 0.0, textureWidth, textureHeight);
        cube.setMirror(mirror);

        if (cubeObject.has("uv")) {
            JsonElement uvElement = cubeObject.get("uv");
            Map<String, GeometryUV> uvMap = new HashMap<>();

            if (uvElement.isJsonObject()) {
                uvMap = parseUvMap(uvElement.getAsJsonObject());
                cube.setUvs(new GeoUVs(uvMap, false));
            } else if (uvElement.isJsonArray()) {
                float[] uv = parseFloatArray(uvElement.getAsJsonArray());
                uvMap.put("default", new GeometryUV("default", uv, new float[]{0.0F, 0.0F}));
                cube.setUvs(new GeoUVs(uvMap, true));
            }
        }

        TexVertex[] texVertices = createTexVertices(origin, vertexSize, inflate * PIXEL_TO_BLOCK);
        cube.setupQuads(texVertices);

        return cube;
    }

    private static Vector3f parseVector3f(JsonArray array) {
        return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }

    private static float[] parseFloatArray(JsonArray array) {
        return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat()};
    }

    private static Map<String, GeometryUV> parseUvMap(JsonObject uvObject) {
        Map<String, GeometryUV> uvMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : uvObject.entrySet()) {
            JsonObject uvData = entry.getValue().getAsJsonObject();
            float[] uv = parseFloatArray(uvData.getAsJsonArray("uv"));
            float[] uvSize = parseFloatArray(uvData.getAsJsonArray("uv_size"));

            uvMap.put(entry.getKey().toLowerCase(Locale.ROOT), new GeometryUV(entry.getKey(), uv, uvSize));
        }
        return uvMap;
    }

    private static TexVertex[] createTexVertices(Vector3f origin, Vector3f vertexSize, float inflation) {
        float minX = origin.x - inflation;
        float minY = origin.y - inflation;
        float minZ = origin.z - inflation;

        float maxX = origin.x + vertexSize.x + inflation;
        float maxY = origin.y + vertexSize.y + inflation;
        float maxZ = origin.z + vertexSize.z + inflation;

        return new TexVertex[] {
                new TexVertex(minX, minY, minZ),
                new TexVertex(minX, minY, maxZ),
                new TexVertex(minX, maxY, minZ),
                new TexVertex(minX, maxY, maxZ),
                new TexVertex(maxX, maxY, minZ),
                new TexVertex(maxX, maxY, maxZ),
                new TexVertex(maxX, minY, minZ),
                new TexVertex(maxX, minY, maxZ)
        };
    }

    public static JsonObject geometryToJson(GeometryData geometryData) {
        JsonObject geometryObject = new JsonObject();
        JsonArray geometryArray = new JsonArray();
        JsonObject firstGeometry = new JsonObject();

        JsonObject description = new JsonObject();
        description.addProperty("identifier", geometryData.getIdentifier());
        description.addProperty("texture_width", geometryData.getTextureWidth());
        description.addProperty("texture_height", geometryData.getTextureHeight());
        firstGeometry.add("description", description);

        JsonArray bonesArray = new JsonArray();
        for (GeometryBone bone : geometryData.getBones().values()) {
            bonesArray.add(boneToJson(bone));
        }

        firstGeometry.add("bones", bonesArray);
        geometryArray.add(firstGeometry);
        geometryObject.add("minecraft:geometry", geometryArray);

        return geometryObject;
    }

    private static JsonObject boneToJson(GeometryBone bone) {
        JsonObject boneObject = new JsonObject();
        boneObject.addProperty("name", bone.getName());
        boneObject.add("pivot", vector3fToJsonArray(bone.getPivot()));

        if (bone.getRotation() != null) {
            boneObject.add("rotation", vector3fToJsonArray(bone.getRotation()));
        }
        if (bone.getScale() != null) {
            boneObject.add("scale", vector3fToJsonArray(bone.getScale()));
        }
        if (bone.getParent() != null) {
            boneObject.addProperty("parent", bone.getParent());
        }

        if (!bone.getCubes().isEmpty()) {
            JsonArray cubesArray = new JsonArray();
            for (GeometryCube cube : bone.getCubes()) {
                cubesArray.add(cubeToJson(cube));
            }
            boneObject.add("cubes", cubesArray);
        }

        if (!bone.getChildren().isEmpty()) {
            JsonArray childrenArray = new JsonArray();
            for (GeometryBone child : bone.getChildren()) {
                childrenArray.add(boneToJson(child));
            }
            boneObject.add("children", childrenArray);
        }

        return boneObject;
    }

    private static JsonObject cubeToJson(GeometryCube cube) {
        JsonObject cubeObject = new JsonObject();
        cubeObject.add("origin", vector3fToJsonArray(cube.getOrigin()));
        cubeObject.add("size", vector3fToJsonArray(cube.getSize()));
        cubeObject.add("pivot", vector3fToJsonArray(cube.getPivot()));

        if (cube.getRotation() != null) {
            cubeObject.add("rotation", vector3fToJsonArray(cube.getRotation()));
        }
        if (cube.getInflate() != 0.0) {
            cubeObject.addProperty("inflate", cube.getInflate());
        }

        GeoUVs uvs = cube.getUvs();
        if (uvs != null) {
            if (uvs.isBoxUv()) {
                GeometryUV defaultUv = uvs.getUvMap().get("default");
                cubeObject.add("uv", floatArrayToJsonArray(defaultUv.getUv()));
            } else {
                JsonObject uvObject = new JsonObject();
                for (Map.Entry<String, GeometryUV> entry : uvs.getUvMap().entrySet()) {
                    JsonObject uvData = new JsonObject();
                    uvData.add("uv", floatArrayToJsonArray(entry.getValue().getUv()));
                    uvData.add("uv_size", floatArrayToJsonArray(entry.getValue().getUvSize()));
                    uvObject.add(entry.getKey(), uvData);
                }
                cubeObject.add("uv", uvObject);
            }
        }

        return cubeObject;
    }

    private static JsonArray vector3fToJsonArray(Vector3f vector) {
        JsonArray array = new JsonArray();
        array.add(vector.x());
        array.add(vector.y());
        array.add(vector.z());
        return array;
    }

    private static JsonArray floatArrayToJsonArray(float[] array) {
        JsonArray jsonArray = new JsonArray();
        for (float value : array) {
            jsonArray.add(value);
        }
        return jsonArray;
    }
}