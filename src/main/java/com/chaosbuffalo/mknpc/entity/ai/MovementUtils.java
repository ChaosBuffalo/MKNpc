package com.chaosbuffalo.mknpc.entity.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class MovementUtils {

    @Nullable
    public static Vec3d findRandomTargetBlockAwayFromNoWater(CreatureEntity entitycreatureIn, int xz, int y, Vec3d targetVec3) {
        Vec3d vec3d = entitycreatureIn.getPositionVec().subtract(targetVec3);
        return RandomPositionGenerator.func_226339_a_(entitycreatureIn, xz, y, 0, vec3d, false,
                ((float)Math.PI / 2F), entitycreatureIn::getBlockPathWeight, false, 0,
                0, true);
    }
}
