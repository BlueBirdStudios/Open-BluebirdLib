package it.bluebird.bluebirdlib.networking.packets;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.simplecora.animations.base.IAnimatedTile;
import it.bluebird.bluebirdlib.networking.Networking;
import it.bluebird.bluebirdlib.utils.CodecUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record C2SPacket(CompoundTag tag, int action) implements CustomPacketPayload {
    public static final Type<C2SPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "client_to_server_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SPacket> STREAM_CODEC = StreamCodec.composite(
            CodecUtils.STREAM_CODEC, p -> p.tag,
            ByteBufCodecs.INT, p -> p.action,
            C2SPacket::new);

    public boolean handle(IPayloadContext ctx) {
        Player p = ctx.player();
        if (!(p instanceof ServerPlayer player))
            return false;

        switch (action) {
            case 312 -> {
                BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
                if (player.level().getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.IMMEDIATE) instanceof IAnimatedTile animated) {
                    tag.put("controller",animated.getController().serializeNBT());
                    Networking.sendToClient(new S2CPacket(tag, 113),player);
                }
            }
        }

        return true;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
