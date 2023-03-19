package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.MKGolemEntity;
import com.chaosbuffalo.mknpc.entity.MKSkeletonEntity;
import com.chaosbuffalo.mknpc.entity.MKZombifiedPiglinEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKNpcEntityTypes {
    public static final String SKELETON_NAME = "skeleton";
    public static final String ZOMBIFIED_PIGLIN_NAME = "zombified_piglin";
    public static EntityType<MKSkeletonEntity> SKELETON_TYPE;
    public static EntityType<MKZombifiedPiglinEntity> ZOMBIFIED_PIGLIN_TYPE;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event){
        EntityType<MKSkeletonEntity> skel1 = EntityType.Builder.create(
                MKSkeletonEntity::new, EntityClassification.MONSTER)
                .size(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
                .build(new ResourceLocation(MKNpc.MODID, SKELETON_NAME).toString());
        skel1.setRegistryName(MKNpc.MODID, SKELETON_NAME);
        SKELETON_TYPE = skel1;
        event.getRegistry().register(skel1);
        EntityType<MKZombifiedPiglinEntity> zombiePiglin = EntityType.Builder.create(
                MKZombifiedPiglinEntity::new, EntityClassification.MONSTER)
                .size(EntityType.ZOMBIFIED_PIGLIN.getWidth(), EntityType.ZOMBIFIED_PIGLIN.getHeight())
                .build(new ResourceLocation(MKNpc.MODID, ZOMBIFIED_PIGLIN_NAME).toString());
        zombiePiglin.setRegistryName(MKNpc.MODID, ZOMBIFIED_PIGLIN_NAME);
        ZOMBIFIED_PIGLIN_TYPE = zombiePiglin;
        event.getRegistry().register(zombiePiglin);

    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event){
        event.put(SKELETON_TYPE, MKSkeletonEntity.registerAttributes(1.0, 0.3).create());
        event.put(ZOMBIFIED_PIGLIN_TYPE, MKZombifiedPiglinEntity.registerAttributes(1.0, 0.2).create());
    }

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MKNpc.MODID);



    public static void register() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}