package it.bluebird.bluebirdlib.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ItemStackCooldown(long finishTick, int totalDuration) {
    public static final Codec<ItemStackCooldown> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.LONG.fieldOf("finish_tick").forGetter(ItemStackCooldown::finishTick), Codec.INT.fieldOf("total_duration").forGetter(ItemStackCooldown::totalDuration)).apply(instance, ItemStackCooldown::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackCooldown> STREAM_CODEC = StreamCodec.composite(StreamCodec.of(FriendlyByteBuf::writeLong, FriendlyByteBuf::readLong), ItemStackCooldown::finishTick, StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt), ItemStackCooldown::totalDuration, ItemStackCooldown::new);
}