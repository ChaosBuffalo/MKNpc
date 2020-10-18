package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.blocks.MKSpawnerBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MKNpcBlocks {
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MKNpc.MODID);
    public static final RegistryObject<MKSpawnerBlock> MK_SPAWNER_BLOCK = BLOCKS.register("mk_spawner",
            () -> new MKSpawnerBlock(Block.Properties.create(MKSpawnerBlock.SPAWNER_MATERIAL).notSolid()));
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MKNpc.MODID);
    public static final RegistryObject<BlockItem> MK_SPAWNER_ITEM = ITEMS.register("mk_spawner",
            () -> new BlockItem(MK_SPAWNER_BLOCK.get(),  new Item.Properties()));

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
