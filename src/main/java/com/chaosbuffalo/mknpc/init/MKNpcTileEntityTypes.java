package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.tile_entities.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.tile_entities.MKPoiTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MKNpcTileEntityTypes {
    public static final DeferredRegister<TileEntityType<?>> TILES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MKNpc.MODID);
    public static final RegistryObject<TileEntityType<?>> MK_SPAWNER_TILE_ENTITY_TYPE =
            TILES.register("mk_spawner", () -> TileEntityType.Builder.create(
                    MKSpawnerTileEntity::new, MKNpcBlocks.MK_SPAWNER_BLOCK.get()).build(null));

    public static final RegistryObject<TileEntityType<?>> MK_POI_TILE_ENTITY_TYPE =
            TILES.register("mk_poi", () -> TileEntityType.Builder.create(
                    MKPoiTileEntity::new, MKNpcBlocks.MK_POI_BLOCK.get()).build(null));

    public static void register() {
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
