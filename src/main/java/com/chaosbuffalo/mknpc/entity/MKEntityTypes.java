package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MKEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES,
            MKNpc.MODID);

    public static final String GREEN_LADY_NAME = "green_lady";

    public static final RegistryObject<EntityType<GreenLadyEntity>> GREEN_LADY = ENTITY_TYPES.register(
            GREEN_LADY_NAME, () ->
                    EntityType.Builder.create(GreenLadyEntity::new, EntityClassification.CREATURE)
                            .size(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
                            .build(new ResourceLocation(MKNpc.MODID, GREEN_LADY_NAME).toString())
    );
}