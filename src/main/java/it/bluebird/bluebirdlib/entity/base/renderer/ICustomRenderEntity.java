package it.bluebird.bluebirdlib.entity.base.renderer;

import net.minecraft.resources.ResourceLocation;

public interface ICustomRenderEntity<T> {
    ResourceLocation getTextureLocation(T var1);

    ResourceLocation getModelLocation(T var1);
}