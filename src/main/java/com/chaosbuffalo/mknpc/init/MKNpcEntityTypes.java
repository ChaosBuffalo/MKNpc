package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.GreenLadyEntity;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MKNpcEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES,
            MKNpc.MODID);

    public static final String GREEN_LADY_NAME = "green_lady";

    public static final RegistryObject<EntityType<GreenLadyEntity>> GREEN_LADY = ENTITY_TYPES.register(
            GREEN_LADY_NAME, () ->
                    EntityType.Builder.create(GreenLadyEntity::new, EntityClassification.CREATURE)
                            .size(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
                            .build(new ResourceLocation(MKNpc.MODID, GREEN_LADY_NAME).toString())
    );

    public static void register(){
        ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void registerAttributes(){
        GlobalEntityTypeAttributes.put(GREEN_LADY.get(),
                MKEntity.registerAttributes(1.0, 0.3).create());
    }
}