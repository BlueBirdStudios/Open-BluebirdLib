package it.bluebird.bluebirdlib.networking.packets;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimated;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimatedTile;
import it.bluebird.bluebirdlib.simplecora.animations.controller.AnimationController;
import it.bluebird.bluebirdlib.utils.CodecUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record S2CPacket(CompoundTag tag, int action) implements CustomPacketPayload {
    public static final Type<S2CPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "server_to_client_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, S2CPacket> STREAM_CODEC = StreamCodec.composite(
            CodecUtils.STREAM_CODEC, p -> p.tag,
            ByteBufCodecs.INT, p -> p.action,
            S2CPacket::new);

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(this::handleClient);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        switch (action) {
            case 112 -> {
                ClientLevel world = Minecraft.getInstance().level;
                if (world != null) {
                    Entity entity = world.getEntity(tag.getInt("entityId"));
                    if (entity instanceof IAnimated animated) {
                        AnimationController controller = animated.getController();
                        controller.deserializeNBT(tag);
                    }

                }
            }
            case 113 -> {
                ClientLevel world = Minecraft.getInstance().level;
                if (world != null) {
                    BlockPos pos = new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
                    if (world.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE) instanceof IAnimatedTile animated) {
                        AnimationController controller = animated.getController();
                        controller.deserializeNBT(tag.getCompound("controller"));
                    }
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
