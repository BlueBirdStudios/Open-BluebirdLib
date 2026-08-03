//package it.bluebird.bluebirdlib.test;
//
//import it.bluebird.bluebirdlib.BluebirdLib;
//import it.bluebird.bluebirdlib.bbanimations.IAnimatable;
//import it.bluebird.bluebirdlib.bbanimations.animations.AnimationController;
//import it.bluebird.bluebirdlib.bbanimations.animations.AnimationStorage;
//import it.bluebird.bluebirdlib.bbanimations.animations.components.AnimationLayer;
//import it.bluebird.bluebirdlib.bbanimations.animations.components.LoopMode;
//import it.bluebird.bluebirdlib.bbanimations.animations.data.Animation;
//import it.bluebird.bluebirdlib.bbanimations.geometry.GeometryStorage;
//import it.bluebird.bluebirdlib.bbanimations.geometry.data.GeometryData;
//import lombok.Getter;
//import lombok.Setter;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.MoverType;
//import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.ai.goal.Goal;
//import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
//import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
//import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
//import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
//import net.minecraft.world.entity.animal.Animal;
//import net.minecraft.world.entity.monster.Monster;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.phys.Vec3;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class TestEntity extends Monster implements IAnimatable {
//    public TestEntity(EntityType<? extends Monster> p_37248_, Level p_37249_) {
//        super(p_37248_, p_37249_);
//    }
//
////    public static final EntityDataAccessor<Str/*ing> ANIMATION = SynchedEntityData.defineId(TestEntity.class, EntityDataSerializers.STRING);
////    public static final EntityDataAccessor<String> ACTION */= SynchedEntityData.defineId(TestEntity.class, EntityDataSerializers.STRING);
//    public AnimationController controller = new AnimationController(this);
//
//    @Getter @Setter private String animation = "";
//    @Getter @Setter private String action = "";
//
//    @Setter
//    public Map<String,Animation> animations = new HashMap<>();
//    private float actionTime = 0;
//
//    @Override
//    public void tick() {
//        super.tick();
//
//        this.animations = AnimationStorage.getAnimations(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "animations/gaiaar.animation.json"));
//        GeometryData model = GeometryStorage.getGeometry(ResourceLocation.fromNamespaceAndPath(BluebirdLib.MODID, "geo/gaiaar.geo.json"));
//        controller.setModel(model);
//        if (animations != null) {
//            controller.addAnimationLayer("walk");
//            controller.addAnimationLayer("idle");
//            controller.addAnimationLayer("action");
//
//            controller.startAnimation("idle", this.animations.get("animation.model.idle"), LoopMode.LOOP, level().isClientSide);
//
//            if (actionTime > 0) {
//                actionTime -= 0.05f;
//            }
//
//            var pos = position();
//            double moved = Math.sqrt(pos.distanceToSqr(xo, yo, zo));
//            boolean posChanged = Math.abs(pos.x - xo) >= 0.00380625f || Math.abs(pos.z - zo) >= 0.00390625f;
//            LivingEntity target = this.getTarget();
//
//            if (!posChanged) moved = 0;
//
//            Animation animation = this.getCurrentAnimation();
//            boolean hasAnimation = animation != Animation.EMPTY;
//            boolean isHitAnimation = animation != null && animation.getKey().equals("animation.model.roll");
//
//            if (hasAnimation && isHitAnimation) {
//                this.goalSelector.disableControlFlag(Goal.Flag.MOVE);
//                if (controller.stopAnimation("walk", 0f,level().isClientSide)) {
//                    controller.stopAnimation("walk", 0f,level().isClientSide);
//                }
//            } else {
//                this.goalSelector.enableControlFlag(Goal.Flag.MOVE);
//                if (moved > 0) {
//                    if (controller.startAnimation("walk", this.animations.get("animation.model.walk"), LoopMode.LOOP, level().isClientSide)) {
//                        controller.startAnimation("walk", this.animations.get("animation.model.walk"), LoopMode.LOOP, level().isClientSide);
//                    }
//                } else {
//                    controller.stopAnimation("walk", 0f,level().isClientSide);
//                }
//            }
//
//            if (target != null && target.isAlive()) {
//                AnimationLayer actionLayer = controller.getLayer("action");
//                if (this.distanceTo(target) < 2 && !isNoAi() && actionLayer != null && getAction().isEmpty()) {
//                    setAction("animation.model.roll");
//                }
//            } else
//                setAction("");
//
//            if (controller.getLayer("action") != null) {
//                Animation actionAnimation = this.animations.get(getAction());
//                if (actionAnimation != null) {
//                    if (controller.startAnimation("action", actionAnimation, LoopMode.LOOP, level().isClientSide)) {
//                        this.setAnimation(controller.getLayer("action").getAnimation().getKey());
//                        if (actionTime <= 0) {
//                            actionTime = actionAnimation.getAnimationLength();
//                        }
//                    }
//                }/* else if (!level().isClientSide)
//                    controller.stopAnimation("action",level().isClientSide);*/
//            }
//        }
//    }
//
//    @Override
//    protected void registerGoals() {
//        super.registerGoals();
//        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, false));
//        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
//        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
//        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
//    }
//
//    public Animation getCurrentAnimation() {
//        if (animations.get(getAnimation()) == null)
//            return Animation.EMPTY;
//        return animations.get(getAnimation());
//    }
//
//   /* public void setAnimation(String animation) {
//        this.entityData.set(ANIMATION, animation);
//    }
//*/
///*    @Override
//    public void defineSynchedData(SynchedEntityData.Builder builder) {
//        builder.define(ANIMATION, "");
//        builder.define(ACTION, "");
//    }*/
//
//  /*  public String getAnimation() {
//        return this.entityData.get(ANIMATION);
//    }
//
//    public String getAction() {
//        return this.entityData.get(ACTION);
//    }
//
//    public void setAction(String animation) {
//        this.entityData.set(ACTION, animation);
//    }*/
//
//    public void travel(@NotNull Vec3 travelVector) {
//        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (isInLava() ? 0.2F : 1F));
//        if (this.isEffectiveAi() && this.isInLava()) {
//            this.moveRelative(this.getSpeed(), travelVector);
//            this.move(MoverType.SELF, this.getDeltaMovement());
//            this.setDeltaMovement(this.getDeltaMovement().scale(0.4D));
//        } else {
//            super.travel(travelVector);
//        }
//    }
//
//    public static AttributeSupplier createAttributes() {
//        return Animal.createMobAttributes()
//                .add(Attributes.MAX_HEALTH, 125)
//                .add(Attributes.ATTACK_DAMAGE, 4.0f)
//                .add(Attributes.ATTACK_SPEED, 0.4f)
//                .add(Attributes.MOVEMENT_SPEED, 0.2f).build();
//    }
//
//    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = BluebirdLib.MODID)
//    public static class ModEvents {
//        @SubscribeEvent
//        public static void onAttributeCreation(EntityAttributeCreationEvent event) {
//            event.put(EntityRegistry.TEST.get(), TestEntity.createAttributes());
//        }
//    }
//
//    @Override
//    public AnimationController getAnimationController() {
//        return controller;
//    }
//}
