package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.GreenLadyEntity;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKNpcEntityTypes {

    public static final String GREEN_LADY_NAME = "green_lady";

    public static EntityType<GreenLadyEntity> GREEN_LADY_ENTITY_TYPE;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event){
        EntityType<GreenLadyEntity> entityType = EntityType.Builder.create(
                GreenLadyEntity::new, EntityClassification.CREATURE)
                .size(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
                .build(new ResourceLocation(MKNpc.MODID, GREEN_LADY_NAME).toString());
        entityType.setRegistryName(MKNpc.MODID, GREEN_LADY_NAME);
        GREEN_LADY_ENTITY_TYPE = entityType;
        event.getRegistry().register(entityType);
        GlobalEntityTypeAttributes.put(entityType, MKEntity.registerAttributes(1.0, 0.3).create());
    }

}