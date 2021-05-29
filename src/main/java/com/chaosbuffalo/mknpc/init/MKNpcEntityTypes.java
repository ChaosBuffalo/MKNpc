package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.MKSkeletonEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKNpcEntityTypes {
    public static final String SKELETON_NAME = "skeleton";
    public static EntityType<MKSkeletonEntity> SKELETON_TYPE;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event){
        EntityType<MKSkeletonEntity> skel1 = EntityType.Builder.create(
                MKSkeletonEntity::new, EntityClassification.MONSTER)
                .size(EntityType.SKELETON.getWidth(), EntityType.SKELETON.getHeight())
                .build(new ResourceLocation(MKNpc.MODID, SKELETON_NAME).toString());
        skel1.setRegistryName(MKNpc.MODID, SKELETON_NAME);
        SKELETON_TYPE = skel1;
        event.getRegistry().register(skel1);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event){
        event.put(SKELETON_TYPE, MKSkeletonEntity.registerAttributes(1.0, 0.3).create());
    }

}