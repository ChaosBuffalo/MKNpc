package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.world.gen.StructureUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;

import java.util.Random;
import java.util.UUID;

public interface IMKStructurePiece {
    UUID getInstanceId();

    ResourceLocation getStructureName();

    default void handleMKDataMarker(String function, BlockPos pos, IWorld worldIn,
                                    Random rand, MutableBoundingBox sbb) {
        StructureUtils.handleMKDataMarker(function, pos, worldIn, rand, sbb, getStructureName(), getInstanceId());
    }
}
