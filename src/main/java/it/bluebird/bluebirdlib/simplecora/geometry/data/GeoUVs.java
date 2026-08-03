package it.bluebird.bluebirdlib.simplecora.geometry.data;

import it.bluebird.bluebirdlib.utils.INBTSerializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.minecraft.nbt.CompoundTag;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoUVs implements INBTSerializable<CompoundTag> {
    private Map<String, GeometryUV> uvMap = new HashMap<>();
    private boolean boxUv;

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("boxUv", this.boxUv);

        CompoundTag uvMapTag = new CompoundTag();
        for (Map.Entry<String, GeometryUV> entry : this.uvMap.entrySet()) {
            uvMapTag.put(entry.getKey(), entry.getValue().serializeNBT());
        }
        tag.put("uvMap", uvMapTag);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.boxUv = tag.getBoolean("boxUv");
        CompoundTag uvMapTag = tag.getCompound("uvMap");
        this.uvMap = new HashMap<>();

        for (String key : uvMapTag.getAllKeys()) {
            GeometryUV geometryUV = new GeometryUV();
            geometryUV.deserializeNBT(uvMapTag.getCompound(key));
            this.uvMap.put(key, geometryUV);
        }
    }
}