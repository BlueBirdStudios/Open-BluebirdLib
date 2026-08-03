package it.bluebird.bluebirdlib.events;

import it.bluebird.bluebirdlib.BluebirdLib;
import it.bluebird.bluebirdlib.simplecora.animations.util.AnimationsLoader;
import it.bluebird.bluebirdlib.simplecora.geometry.util.GeometryLoader;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = BluebirdLib.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonEvents {
    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        GeometryLoader.loadModels();
        AnimationsLoader.loadAnimations();
    }
}