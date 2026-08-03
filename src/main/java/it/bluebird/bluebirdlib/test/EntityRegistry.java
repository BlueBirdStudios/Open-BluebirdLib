//package it.bluebird.bluebirdlib.test;
//
//import it.bluebird.bluebirdlib.BluebirdLib;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.MobCategory;
//import net.neoforged.neoforge.registries.DeferredHolder;
//import net.neoforged.neoforge.registries.DeferredRegister;
//
//public class EntityRegistry {
//    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, BluebirdLib.MODID);
//
//    public static final DeferredHolder<EntityType<?>, EntityType<TestEntity>> TEST = ENTITIES.register("test", () ->
//            EntityType.Builder.<TestEntity>of(TestEntity::new, MobCategory.MISC)
//                    .sized(1F, 1F)
//                    .build("test")
//    );
//}
