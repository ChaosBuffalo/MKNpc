package com.chaosbuffalo.mknpc.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class MovementUtils {

    @Nullable
    public static Vector3d findRandomTargetBlockAwayFromNoWater(CreatureEntity entity, int xz, int y, Vector3d targetPos) {
        Vector3d vec3d = entity.getPositionVec().subtract(targetPos);
        return RandomPositionGenerator.func_226339_a_(entity, xz, y, 0, vec3d, false,
                ((float)Math.PI / 2F), entity::getBlockPathWeight, false, 0,
                0, true);
    }

    @Nullable
    public static Vector3d findRandomTargetBlockTowardsNoWater(CreatureEntity entity, int xz, int y, Vector3d targetPos){
        Vector3d vec3d = targetPos.subtract(entity.getPositionVec());
        return RandomPositionGenerator.func_226339_a_(entity, xz, y, 0, vec3d, false,
                ((float)Math.PI / 2F), entity::getBlockPathWeight, false, 0,
                0, true);
    }
}
