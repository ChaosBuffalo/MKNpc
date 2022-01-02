package com.chaosbuffalo.mknpc.world.gen;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class StructureUtils {

    public static BlockPos getCorrectionForEvenRotation(Rotation rotation){
        switch (rotation){
            case CLOCKWISE_90:
                return new BlockPos(-1, 0, 0);
            case COUNTERCLOCKWISE_90:
                return new BlockPos(0, 0, -1);
            case CLOCKWISE_180:
                return new BlockPos(-1, 0, -1);
            case NONE:
            default:
                return new BlockPos(0, 0, 0);
        }
    }

    public static void handleMKDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb,
                                          ResourceLocation structureName, UUID instanceId)
    {
        if (function.equals("mkspawner")) {

            TileEntity tileentity = worldIn.getTileEntity(pos.down());
            if (tileentity instanceof MKSpawnerTileEntity) {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                MKSpawnerTileEntity spawner = (MKSpawnerTileEntity) tileentity;
                spawner.regenerateSpawnID();
                spawner.setStructureName(structureName);
                spawner.setStructureId(instanceId);
            } else {
                IChunk chunk = worldIn.getChunk(pos.down());
                MKNpc.LOGGER.warn("Failed to find TE in Chunk for mkspawner datablock: {}", chunk);
            }
        } else if (function.startsWith("mkcontainer")){
            String[] names = function.split("#", 2);
            String labels = names[1];
            TileEntity tileEntity = worldIn.getTileEntity(pos.down());
            if (tileEntity instanceof ChestTileEntity){
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                tileEntity.getCapability(NpcCapabilities.CHEST_NPC_DATA_CAPABILITY).ifPresent(x ->{
                    x.setStructureId(instanceId);
                    x.setStructureName(structureName);
                    x.generateChestId(labels);
                });

            }else {
                IChunk chunk = worldIn.getChunk(pos.down());
                MKNpc.LOGGER.warn("Failed to find TE in Chunk for mkcontainer datablock: {}", chunk);
            }

        }
    }
}
