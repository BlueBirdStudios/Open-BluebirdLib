package it.bluebird.bluebirdlib.simplecora.geometry.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeoUVs;
import it.bluebird.bluebirdlib.simplecora.geometry.data.GeometryUV;
import it.bluebird.bluebirdlib.simplecora.geometry.data.TexVertex;
import java.util.Locale;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Data
@NoArgsConstructor
public class GeometryCube {
    private Vector3f origin;
    private Vector3f size;
    private Vector3f pivot;
    private Vector3f rotation;
    private GeoUVs uvs = new GeoUVs();
    private int texWidth;
    private int texHeight;
    private double inflate;
    private GeometryQuad[] quads;
    private boolean mirror = false;

    public GeometryCube(Vector3f origin, Vector3f size, Vector3f pivot, Vector3f rotation, double inflate, int textureWidth, int textureHeight) {
        this.origin = origin;
        this.size = size;
        this.pivot = pivot;
        this.rotation = rotation;
        this.inflate = inflate;
        this.texWidth = textureWidth;
        this.texHeight = textureHeight;
    }

    public void setupQuads(TexVertex[] vertices) {
        this.quads = new GeometryQuad[] {
                constructQuad(vertices, Direction.NORTH),
                constructQuad(vertices, Direction.SOUTH),
                constructQuad(vertices, Direction.WEST),
                constructQuad(vertices, Direction.EAST),
                constructQuad(vertices, Direction.UP),
                constructQuad(vertices, Direction.DOWN)
        };
    }

    public TexVertex[] transformVertices(TexVertex[] vertices, Direction direction, boolean isBoxUv) {
        return switch (direction) {
            case WEST -> this.mirror
                    ? new TexVertex[]{vertices[4], vertices[5], vertices[7], vertices[6]}
                    : new TexVertex[]{vertices[3], vertices[2], vertices[0], vertices[1]};
            case EAST -> this.mirror
                    ? new TexVertex[]{vertices[3], vertices[2], vertices[0], vertices[1]}
                    : new TexVertex[]{vertices[4], vertices[5], vertices[7], vertices[6]};
            case NORTH -> new TexVertex[]{vertices[2], vertices[4], vertices[6], vertices[0]};
            case SOUTH -> new TexVertex[]{vertices[5], vertices[3], vertices[1], vertices[7]};
            case UP -> (this.mirror && !isBoxUv)
                    ? new TexVertex[]{vertices[0], vertices[6], vertices[7], vertices[1]}
                    : new TexVertex[]{vertices[3], vertices[5], vertices[4], vertices[2]};
            case DOWN -> (this.mirror && !isBoxUv)
                    ? new TexVertex[]{vertices[3], vertices[5], vertices[4], vertices[2]}
                    : new TexVertex[]{vertices[0], vertices[6], vertices[7], vertices[1]};
        };
    }

    public GeometryQuad constructQuad(TexVertex[] vertices, Direction direction) {
        if (!this.uvs.isBoxUv()) {
            String directionName = direction.name().toLowerCase(Locale.ROOT);
            GeometryUV uvDetails = this.uvs.getUvMap().get(directionName);
            TexVertex[] modifiedVertices = this.transformVertices(vertices, direction, false);
            return this.createQuad(modifiedVertices, uvDetails, direction);
        }

        float[] baseUv = this.uvs.getUvMap().get("default").getUv();
        Vector3f sz = new Vector3f((float)Math.floor(this.size.x), (float)Math.floor(this.size.y), (float)Math.floor(this.size.z));

        float[][] uvCoords = switch (direction) {
            case WEST  -> new float[][]{{baseUv[0] + sz.z + sz.x, baseUv[1] + sz.z}, {sz.z, sz.y}};
            case EAST  -> new float[][]{{baseUv[0], baseUv[1] + sz.z}, {sz.z, sz.y}};
            case NORTH -> new float[][]{{baseUv[0] + sz.z, baseUv[1] + sz.z}, {sz.x, sz.y}};
            case SOUTH -> new float[][]{{baseUv[0] + sz.z + sz.x + sz.z, baseUv[1] + sz.z}, {sz.x, sz.y}};
            case UP    -> new float[][]{{baseUv[0] + sz.z, baseUv[1]}, {sz.x, sz.z}};
            case DOWN  -> new float[][]{{baseUv[0] + sz.z + sz.x, baseUv[1] + sz.z}, {sz.x, -sz.z}};
        };

        TexVertex[] modifiedVertices = this.transformVertices(vertices, direction, true);
        GeometryUV generatedUV = new GeometryUV("default", uvCoords[0], uvCoords[1]);
        return this.createQuad(modifiedVertices, generatedUV, direction);
    }

    private GeometryQuad createQuad(TexVertex[] vertices, GeometryUV geometryUV, Direction direction) {
        float[] uvStart = geometryUV.getUv();
        float[] uvDimensions = geometryUV.getUvSize();

        float startU = uvStart[0];
        float startV = uvStart[1];
        float endU = (startU + uvDimensions[0]) / (float)this.texWidth;
        float endV = (startV + uvDimensions[1]) / (float)this.texHeight;

        startU /= (float)this.texWidth;
        startV /= (float)this.texHeight;

        Vector3f faceNormal = direction.step();
        if (this.mirror) {
            faceNormal.mul(-1.0F, 1.0F, 1.0F);
        } else {
            float tempU = endU;
            endU = startU;
            startU = tempU;
        }

        vertices[0] = vertices[0].setUv(startU, startV);
        vertices[1] = vertices[1].setUv(endU, startV);
        vertices[2] = vertices[2].setUv(endU, endV);
        vertices[3] = vertices[3].setUv(startU, endV);

        return new GeometryQuad(vertices, direction, faceNormal);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                       float red, float green, float blue, float alpha) {
        poseStack.translate(this.pivot.x / 16.0F, this.pivot.y / 16.0F, this.pivot.z / 16.0F);
        poseStack.mulPose(new Quaternionf().rotateZYX(this.rotation.z, this.rotation.y, this.rotation.x));
        poseStack.translate(-this.pivot.x / 16.0F, -this.pivot.y / 16.0F, -this.pivot.z / 16.0F);

        Matrix4f positionMatrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        float r = red > 1.0F ? red / 255.0F : red;
        float g = green > 1.0F ? green / 255.0F : green;
        float b = blue > 1.0F ? blue / 255.0F : blue;
        float a = alpha > 1.0F ? alpha / 255.0F : alpha;
        int colorBits = ((int)(a * 255.0F) << 24) | ((int)(r * 255.0F) << 16) | ((int)(g * 255.0F) << 8) | (int)(b * 255.0F);

        for (GeometryQuad quad : this.quads) {
            if (quad == null) continue;
            Vector3f normal = normalMatrix.transform(new Vector3f(quad.getNormal()));

            for (TexVertex vertex : quad.getTexVertices()) {
                Vector3f pos = vertex.getPos();

                Vector4f worldPos = positionMatrix.transform(new Vector4f(pos.x(), pos.y(), pos.z(), 1.0F));
                vertexConsumer.addVertex(
                        worldPos.x(), worldPos.y(), worldPos.z(),
                        colorBits,
                        vertex.getTexU(), vertex.getTexV(),
                        packedOverlay, packedLight,
                        normal.x(), normal.y(), normal.z()
                );
            }
        }
    }
}