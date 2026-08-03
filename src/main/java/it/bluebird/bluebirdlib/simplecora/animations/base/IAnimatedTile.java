package it.bluebird.bluebirdlib.simplecora.animations.base;

import it.bluebird.bluebirdlib.simplecora.animations.controller.AnimationController;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IAnimatedTile extends IAnimated {
    AnimationController getController();
    BlockEntity getAnimatedTile();
}
