package it.bluebird.bluebirdlib.blocks.base.renderer;

import it.bluebird.bluebirdlib.data.AnimationSequence;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ICustomRenderBlock<T> {
    ResourceLocation getTextureLocation(BlockEntity block);
    ResourceLocation getModelLocation(BlockEntity block);
    AnimationSequence getAnimationSequence(BlockEntity block);
}