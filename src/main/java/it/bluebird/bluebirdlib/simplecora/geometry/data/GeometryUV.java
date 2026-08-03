package it.bluebird.bluebirdlib.simplecora.geometry.data;

import it.bluebird.bluebirdlib.utils.INBTSerializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeometryUV implements INBTSerializable<CompoundTag> {
    private String key;
    private float[] uv;
    private float[] uvSize;

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("key", this.key);

        ListTag uvList = new ListTag();
        for (float value : this.uv) {
            uvList.add(FloatTag.valueOf(value));
        }
        tag.put("uv", uvList);

        ListTag uvSizeList = new ListTag();
        for (float value : this.uvSize) {
            uvSizeList.add(FloatTag.valueOf(value));
        }
        tag.put("uvSize", uvSizeList);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.key = tag.getString("key");

        ListTag uvList = tag.getList("uv", Tag.TAG_FLOAT);
        this.uv = new float[uvList.size()];
        for (int i = 0; i < uvList.size(); ++i) {
            this.uv[i] = uvList.getFloat(i);
        }

        ListTag uvSizeList = tag.getList("uvSize", Tag.TAG_FLOAT);
        this.uvSize = new float[uvSizeList.size()];
        for (int i = 0; i < uvSizeList.size(); ++i) {
            this.uvSize[i] = uvSizeList.getFloat(i);
        }
    }
}