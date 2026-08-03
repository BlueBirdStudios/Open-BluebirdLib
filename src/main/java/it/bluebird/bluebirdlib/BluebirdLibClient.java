package it.bluebird.bluebirdlib;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = BluebirdLib.MODID, value = Dist.CLIENT)
public class BluebirdLibClient {
    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event) {
    }
}
