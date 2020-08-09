package com.chaosbuffalo.mknpc.entity.ai.movement_strategy;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class StationaryMovementStrategy extends MovementStrategy {

    public static final StationaryMovementStrategy STATIONARY_MOVEMENT_STRATEGY = new StationaryMovementStrategy();

    @Override
    public void update(ServerWorld world, CreatureEntity entity) {
        Brain<?> brain = entity.getBrain();
        Optional<WalkTarget> walkTargetOptional = brain.getMemory(MemoryModuleType.WALK_TARGET);
        if (walkTargetOptional.isPresent()) {
            brain.removeMemory(MemoryModuleType.WALK_TARGET);
        }
    }
}
