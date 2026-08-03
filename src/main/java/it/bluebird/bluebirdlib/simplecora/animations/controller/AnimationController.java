package it.bluebird.bluebirdlib.simplecora.animations.controller;

import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimated;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimatedEntity;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimatedTile;
import it.bluebird.bluebirdlib.simplecora.animations.components.Animation;
import it.bluebird.bluebirdlib.simplecora.animations.components.AnimationInstance;
import it.bluebird.bluebirdlib.simplecora.animations.components.GeometryChanges;
import it.bluebird.bluebirdlib.simplecora.animations.components.LoopMode;
import it.bluebird.bluebirdlib.networking.Networking;
import it.bluebird.bluebirdlib.networking.packets.C2SPacket;
import it.bluebird.bluebirdlib.networking.packets.S2CPacket;
import it.bluebird.bluebirdlib.utils.INBTSerializable;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class AnimationController implements INBTSerializable<CompoundTag> {
    public IAnimated object;
    public float tickCount;
    public boolean hasSync = false;
    public Map<String, AnimationLayer> layers = new HashMap<>();

    public AnimationController(IAnimated object) {
        this.object = object;
    }

    public void tick() {
        if (!this.hasSync) {
            if (this.object instanceof IAnimatedTile tile) {
                Level level = tile.getAnimatedTile().getLevel();
                if (level != null && level.isClientSide) {
                    this.syncClient();
                }
            }
            this.hasSync = true;
        }

        this.tickCount += 0.05F;

        for (AnimationLayer layer : this.layers.values()) {
            layer.tick(this.tickCount);
        }
    }

    public GeometryChanges apply(float partialTicks) {
        GeometryChanges geometryChanges = new GeometryChanges();
        float controllerTime = this.getControllerTime(partialTicks);

        for (AnimationLayer layer : this.layers.values()) {
            layer.apply(geometryChanges, controllerTime);
        }

        return geometryChanges;
    }

    public float getControllerTime(float partialTicks) {
        return this.tickCount + partialTicks * 0.05F;
    }

    public AnimationLayer getLayer(String layerName) {
        return this.layers.computeIfAbsent(layerName, name -> new AnimationLayer(name, this));
    }

    public boolean startAnimation(String layerName, Animation animation, LoopMode loopMode) {
        AnimationLayer layer = this.getLayer(layerName);
        layer.setLoopMode(loopMode);
        return layer.startAnimation(animation);
    }

    public boolean startAnimation(String layerName, Animation animation) {
        return this.getLayer(layerName).startAnimation(animation);
    }

    public boolean stopAnimation(String layerName) {
        return this.startAnimation(layerName, Animation.EMPTY);
    }

    public boolean isCurrentAnimation(String layer, String key) {
        AnimationInstance current = this.getLayer(layer).getCurrentAnimation();
        return current != null && current.getAnimation().getKey().equals(key);
    }

    public void trySync() {
        if (this.object instanceof IAnimatedEntity entity) {
            Level level = entity.getAnimatedEntity().level();
            if (!level.isClientSide && level.isLoaded(entity.getAnimatedEntity().getOnPos())) {
                CompoundTag tag = this.serializeNBT();
                Networking.sendToPlayersTrackingEntity(new S2CPacket(tag, 112), entity.getAnimatedEntity());
            }
        } else if (this.object instanceof IAnimatedTile tile) {
            Level level = tile.getAnimatedTile().getLevel();
            if (level != null && !level.isClientSide) {
                BlockPos pos = tile.getAnimatedTile().getBlockPos();
                if (level.isLoaded(pos)) {
                    CompoundTag tag = new CompoundTag();
                    tag.putInt("x", pos.getX());
                    tag.putInt("y", pos.getY());
                    tag.putInt("z", pos.getZ());
                    tag.put("controller", this.serializeNBT());
                    Networking.sendToPlayersTrackingChunk(new S2CPacket(tag, 113), (ServerLevel) level, new ChunkPos(pos));
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void syncClient() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && this.object instanceof IAnimatedTile tile) {
            BlockPos pos = tile.getAnimatedTile().getBlockPos();
            if (level.isLoaded(pos)) {
                CompoundTag tag = new CompoundTag();
                tag.putInt("x", pos.getX());
                tag.putInt("y", pos.getY());
                tag.putInt("z", pos.getZ());
                Networking.sendToServer(new C2SPacket(tag, 312));
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        if (this.object instanceof IAnimatedEntity entity) {
            tag.putInt("entityId", entity.getAnimatedEntity().getId());
        }

        tag.putFloat("tickCount", this.tickCount);
        CompoundTag layersTag = new CompoundTag();

        for (Map.Entry<String, AnimationLayer> entry : this.layers.entrySet()) {
            layersTag.put(entry.getKey(), entry.getValue().serializeNBT());
        }

        tag.put("layers", layersTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.tickCount = tag.getFloat("tickCount");
        this.layers.clear();
        CompoundTag layersTag = tag.getCompound("layers");

        for (String key : layersTag.getAllKeys()) {
            AnimationLayer layer = new AnimationLayer(key, this);
            layer.deserializeNBT(layersTag.getCompound(key));
            this.layers.put(key, layer);
        }
    }
}