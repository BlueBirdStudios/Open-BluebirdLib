package it.bluebird.bluebirdlib.utils;

import com.mojang.datafixers.util.Function7;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class CodecUtils {
    public static final StreamCodec<FriendlyByteBuf, CompoundTag> STREAM_CODEC = StreamCodec.of(
            (buf, tag) -> buf.writeNbt(tag),
            buf -> {
                CompoundTag tag = buf.readNbt();
                return tag != null ? tag : new CompoundTag();
            }
    );

    public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> streamCodec,
                                                                                 final Function<C, T1> getter, final StreamCodec<? super B, T2> streamCodec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> streamCodec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> streamCodec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> streamCodec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> streamCodec6, final Function<C, T6> getter6, final StreamCodec<? super B, T7> streamCodec7, final Function<C, T7> getter7, final Function7<T1, T2, T3, T4, T5, T6, T7, C> creator) {
        return new StreamCodec<B, C>(){

            @Override
            public @NotNull C decode(@NotNull B buffer) {
                T1 object1 = streamCodec.decode(buffer);
                T2 object2 = streamCodec2.decode(buffer);
                T3 object3 = streamCodec3.decode(buffer);
                T4 object4 = streamCodec4.decode(buffer);
                T5 object5 = streamCodec5.decode(buffer);
                T6 object6 = streamCodec6.decode(buffer);
                T7 object7 = streamCodec7.decode(buffer);
                return creator.apply(object1, object2, object3, object4, object5, object6, object7);
            }

            @Override
            public void encode(@NotNull B buffer, @NotNull C output) {
                streamCodec.encode(buffer, getter.apply(output));
                streamCodec2.encode(buffer, getter2.apply(output));
                streamCodec3.encode(buffer, getter3.apply(output));
                streamCodec4.encode(buffer, getter4.apply(output));
                streamCodec5.encode(buffer, getter5.apply(output));
                streamCodec6.encode(buffer, getter6.apply(output));
                streamCodec7.encode(buffer, getter7.apply(output));
            }
        };
    }

}
