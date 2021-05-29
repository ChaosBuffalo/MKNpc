package com.chaosbuffalo.mknpc.world.gen;

import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;

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
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            TileEntity tileentity = worldIn.getTileEntity(pos.down());
            if (tileentity instanceof MKSpawnerTileEntity) {
                MKSpawnerTileEntity spawner = (MKSpawnerTileEntity) tileentity;
                spawner.regenerateSpawnID();
                spawner.setStructureName(structureName);
                spawner.setStructureId(instanceId);
            }
        }
    }
}
