package com.chaosbuffalo.mknpc.entity.ai.sensor;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.world.server.ServerWorld;

import java.util.Set;

public class MovementStrategySensor extends Sensor<MKEntity> {


    @Override
    protected void update(ServerWorld worldIn, MKEntity entityIn) {
        if ((entityIn.avoidsWater() && entityIn.isInWater())) {
            return;
        }
        entityIn.getBrain().getMemory(MKMemoryModuleTypes.MOVEMENT_STRATEGY).ifPresent(
                movementStrategy -> movementStrategy.update(worldIn, entityIn));

    }

    @Override
    public Set<MemoryModuleType<?>> getUsedMemories() {
        return ImmutableSet.of(MKMemoryModuleTypes.THREAT_TARGET, MKMemoryModuleTypes.VISIBLE_ENEMIES,
                MemoryModuleType.WALK_TARGET, MKMemoryModuleTypes.MOVEMENT_STRATEGY, MKMemoryModuleTypes.IS_RETURNING);
    }
}
