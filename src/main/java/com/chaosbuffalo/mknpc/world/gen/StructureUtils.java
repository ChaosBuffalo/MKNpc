package com.chaosbuffalo.mknpc.world.gen;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

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
}
