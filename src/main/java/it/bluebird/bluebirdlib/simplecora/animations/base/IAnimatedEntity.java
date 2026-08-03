package it.bluebird.bluebirdlib.simplecora.animations.base;

import it.bluebird.bluebirdlib.simplecora.animations.controller.AnimationController;
import net.minecraft.world.entity.Entity;

public interface IAnimatedEntity extends IAnimated {
    AnimationController getController();
    Entity getAnimatedEntity();
}
