package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.MKGolemEntity;
import com.chaosbuffalo.mknpc.entity.MKSkeletonEntity;
import com.chaosbuffalo.mknpc.entity.MKZombifiedPiglinEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKNpcEntityTypes {
    public static final String SKELETON_NAME = "skeleton";
    public static final String ZOMBIFIED_PIGLIN_NAME = "zombified_piglin";
//    public static EntityType<MKSkeletonEntity> SKELETON_TYPE;
//    public static EntityType<MKZombifiedPiglinEntity> ZOMBIFIED_PIGLIN_TYPE;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event){
//        EntityType<MKSkeletonEntity> skel1 = EntityType.Builder.of(
//                MKSkeletonEntity::new, MobCategory.MONSTER)
//                .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
//                .build(new ResourceLocation(MKNpc.MODID, SKELETON_NAME).toString());
//        skel1.setRegistryName(MKNpc.MODID, SKELETON_NAME);
//        SKELETON_TYPE = skel1;
//        event.getRegistry().register(skel1);
//        EntityType<MKZombifiedPiglinEntity> zombiePiglin = EntityType.Builder.of(
//                MKZombifiedPiglinEntity::new, MobCategory.MONSTER)
//                .sized(EntityType.ZOMBIFIED_PIGLIN.getWidth(), EntityType.ZOMBIFIED_PIGLIN.getHeight())
//                .build(new ResourceLocation(MKNpc.MODID, ZOMBIFIED_PIGLIN_NAME).toString());
//        zombiePiglin.setRegistryName(MKNpc.MODID, ZOMBIFIED_PIGLIN_NAME);
//        ZOMBIFIED_PIGLIN_TYPE = zombiePiglin;
//        event.getRegistry().register(zombiePiglin);

    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event){
        event.put(SKELETON_TYPE.get(), MKSkeletonEntity.registerAttributes(1.0, 0.3).build());
//        event.put(ZOMBIFIED_PIGLIN_TYPE, MKZombifiedPiglinEntity.registerAttributes(1.0, 0.2).build());
    }

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MKNpc.MODID);

    public static final RegistryObject<EntityType<MKSkeletonEntity>> SKELETON_TYPE = ENTITIES.register("skeleton",
            () -> EntityType.Builder.of(MKSkeletonEntity::new, MobCategory.MONSTER)
                    .sized(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
                    .build(new ResourceLocation(MKNpc.MODID, "skeleton").toString()));



    public static void register() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}