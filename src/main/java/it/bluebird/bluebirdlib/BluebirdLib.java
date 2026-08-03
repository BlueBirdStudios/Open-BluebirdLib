package it.bluebird.bluebirdlib;

import it.bluebird.bluebirdlib.simplecora.animations.util.AnimationsLoader;
import it.bluebird.bluebirdlib.simplecora.geometry.util.GeometryLoader;
import it.bluebird.bluebirdlib.registry.AnimationRegistry;
import it.bluebird.bluebirdlib.registry.CodecRegistry;
import it.bluebird.bluebirdlib.registry.GeometryRegistry;

import it.bluebird.bluebirdlib.registry.DataComponentRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BluebirdLib.MODID)
public class BluebirdLib {
    public static final String MODID = "bluebirdlib";
    public static final Logger LOGGER = LogManager.getLogger(BluebirdLib.MODID);
    public static final ResourceLocation ICONS_FONT = ResourceLocation.fromNamespaceAndPath("bluebirdlib", "icons");

    public BluebirdLib(IEventBus modBus, ModContainer container) {
        CodecRegistry.GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modBus);
        GeometryRegistry.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            GeometryLoader.init();
        }

        AnimationRegistry.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            AnimationsLoader.init();
        }

        DataComponentRegistry.DATA_COMPONENT_TYPES.register(modBus);
    }
}