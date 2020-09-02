package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MKNpcTileEntityTypes {
    public static final DeferredRegister<TileEntityType<?>> TILES =
            new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MKNpc.MODID);
    public static final RegistryObject<TileEntityType<?>> MK_SPAWNER_TILE_ENTITY_TYPE =
            TILES.register("mk_spawner", () -> TileEntityType.Builder.create(
                    MKSpawnerTileEntity::new, MKNpcBlocks.MK_SPAWNER_BLOCK.get()).build(null));

    public static void register() {
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
