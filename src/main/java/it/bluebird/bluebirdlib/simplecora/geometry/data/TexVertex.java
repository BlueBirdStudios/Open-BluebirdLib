package it.bluebird.bluebirdlib.simplecora.geometry.data;

import it.bluebird.bluebirdlib.utils.INBTSerializable;
import it.bluebird.bluebirdlib.utils.NbtUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import org.joml.Vector3f;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TexVertex implements INBTSerializable<CompoundTag> {
    private Vector3f pos;
    private float texU;
    private float texV;

    public TexVertex(float x, float y, float z) {
        this.pos = new Vector3f(x, y, z);
        this.texU = 0.0F;
        this.texV = 0.0F;
    }

    public TexVertex setUv(float texU, float texV) {
        return new TexVertex(this.pos, texU, texV);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("pos", NbtUtils.writeVector3f(this.pos));
        tag.putFloat("texU", this.texU);
        tag.putFloat("texV", this.texV);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.pos = NbtUtils.readVector3f(tag.getCompound("pos"));
        this.texU = tag.getFloat("texU");
        this.texV = tag.getFloat("texV");
    }
}