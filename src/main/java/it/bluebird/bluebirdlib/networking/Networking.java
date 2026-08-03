package it.bluebird.bluebirdlib.networking;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.networking.packets.C2SPacket;
import it.bluebird.bluebirdlib.networking.packets.S2CPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Networking {
    @SubscribeEvent
    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(BluebirdLib.MODID)
                .versioned("1.0")
                .optional();

        registrar.playToServer(C2SPacket.TYPE, C2SPacket.STREAM_CODEC, C2SPacket::handle);
        registrar.playToClient(S2CPacket.TYPE, S2CPacket.STREAM_CODEC, S2CPacket::handle);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T message) {
        PacketDistributor.sendToServer(message);
    }

    public static <T extends CustomPacketPayload> void sendToClient(T message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }

    public static <T extends CustomPacketPayload> void sendToAll(T message) {
        PacketDistributor.sendToAllPlayers(message);
    }

    public static <T extends CustomPacketPayload> void sendToPlayersTrackingEntity(T message, Entity entity) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, message);
    }

    public static <T extends CustomPacketPayload> void sendToPlayersTrackingChunk(T message, ServerLevel level, ChunkPos pos) {
        PacketDistributor.sendToPlayersTrackingChunk(level, pos, message);
    }
}
