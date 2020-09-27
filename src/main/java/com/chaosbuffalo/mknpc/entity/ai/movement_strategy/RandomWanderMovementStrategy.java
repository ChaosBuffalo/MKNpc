package com.chaosbuffalo.mknpc.entity.ai.movement_strategy;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class RandomWanderMovementStrategy extends MovementStrategy{
    private final int positionChance;

    public RandomWanderMovementStrategy(int positionChance){
        this.positionChance = positionChance;
    }

    @Override
    public void update(ServerWorld world, MKEntity entity) {
        Brain<?> brain = entity.getBrain();
        Optional<WalkTarget> walkTargetOptional = brain.getMemory(MemoryModuleType.WALK_TARGET);
        if (!walkTargetOptional.isPresent() || entity.getRNG().nextInt(positionChance) == 0){
            Vec3d vec3d = RandomPositionGenerator.getLandPos(entity, entity.getWanderRange() / 2, entity.getWanderRange() / 2);
            if (vec3d != null){
                brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, 1.0f, 1));
            }
        }
    }
}
