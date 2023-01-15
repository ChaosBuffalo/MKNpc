package com.chaosbuffalo.mknpc.entity.ai.movement_strategy;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.ai.MovementUtils;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
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
        Optional<BlockPos> spawnPointOptional = brain.getMemory(MKMemoryModuleTypes.SPAWN_POINT);
        if (!walkTargetOptional.isPresent() || entity.getRNG().nextInt(positionChance) == 0 || entity.getNavigator().noPath()){
            Vector3d position = spawnPointOptional.map(blockPos -> {
                Vector3d vecPos = Vector3d.copy(blockPos);
                if (entity.getDistanceSq(vecPos) > entity.getWanderRange() * entity.getWanderRange()){
                    return MovementUtils.findRandomTargetBlockTowardsNoWater(
                            entity, entity.getWanderRange() /2, entity.getWanderRange() / 2, vecPos);
                } else {
                    return RandomPositionGenerator.getLandPos(entity, entity.getWanderRange() / 2,
                            entity.getWanderRange() / 2);
                }
            }).orElse(RandomPositionGenerator.getLandPos(entity, entity.getWanderRange() / 2,
                    entity.getWanderRange() / 2));
            if (position != null){
                brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(position, 0.5f, 1));
            }
        }
    }
}
