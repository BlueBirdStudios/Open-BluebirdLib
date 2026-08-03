package it.bluebird.bluebirdlib.simplecora.geometry.components;

import it.bluebird.bluebirdlib.simplecora.geometry.data.TexVertex;
import it.bluebird.bluebirdlib.utils.INBTSerializable;
import it.bluebird.bluebirdlib.utils.NbtUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.joml.Vector3f;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeometryQuad implements INBTSerializable<CompoundTag> {
    private TexVertex[] texVertices;
    private Direction direction;
    private Vector3f normal;

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag verticesList = new ListTag();

        for (TexVertex vertex : this.texVertices) {
            verticesList.add(vertex.serializeNBT());
        }

        tag.put("texVertices", verticesList);
        tag.putString("direction", this.direction.getName());
        tag.put("normal", NbtUtils.writeVector3f(this.normal));

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ListTag verticesList = tag.getList("texVertices", 10);
        this.texVertices = new TexVertex[verticesList.size()];

        for (int i = 0; i < verticesList.size(); ++i) {
            TexVertex vertex = new TexVertex();
            vertex.deserializeNBT(verticesList.getCompound(i));
            this.texVertices[i] = vertex;
        }

        this.direction = Direction.byName(tag.getString("direction"));
        this.normal = NbtUtils.readVector3f(tag.getCompound("normal"));
    }
}