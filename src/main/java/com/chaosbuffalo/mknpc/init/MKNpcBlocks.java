package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.blocks.MKPoiBlock;
import com.chaosbuffalo.mknpc.blocks.MKSpawnerBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MKNpcBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MKNpc.MODID);
    public static final RegistryObject<MKSpawnerBlock> MK_SPAWNER_BLOCK = BLOCKS.register("mk_spawner",
            () -> new MKSpawnerBlock(Block.Properties.create(MKSpawnerBlock.SPAWNER_MATERIAL).notSolid()));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MKNpc.MODID);
    public static final RegistryObject<BlockItem> MK_SPAWNER_ITEM = ITEMS.register("mk_spawner",
            () -> new BlockItem(MK_SPAWNER_BLOCK.get(),  new Item.Properties()));
    public static final RegistryObject<MKPoiBlock> MK_POI_BLOCK = BLOCKS.register("mk_poi",
            () -> new MKPoiBlock(AbstractBlock.Properties.create(MKPoiBlock.MATERIAL).notSolid()
                    .setOpaque((BlockState state, IBlockReader reader, BlockPos pos) -> false)
                    .setBlocksVision((BlockState state, IBlockReader reader, BlockPos pos) -> false)));
    public static final RegistryObject<BlockItem> MK_POI_ITEM = ITEMS.register("mk_poi",
            () -> new BlockItem(MK_POI_BLOCK.get(),  new Item.Properties()));

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
