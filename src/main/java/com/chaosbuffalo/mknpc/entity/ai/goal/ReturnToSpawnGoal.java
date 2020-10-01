package com.chaosbuffalo.mknpc.entity.ai.goal;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.Optional;

public class ReturnToSpawnGoal extends Goal {
    private final MKEntity entity;
    private static final int LEASH_RANGE = 30;
    private static final int MIN_RANGE = 1;
    private int ticksReturning;
    private static final int TICKS_TO_TELEPORT = 15 * GameConstants.TICKS_PER_SECOND;

    public ReturnToSpawnGoal(MKEntity entity) {
        this.entity = entity;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.ticksReturning = 0;
    }

    @Override
    public void tick() {
        super.tick();
        ticksReturning++;
        if (ticksReturning > TICKS_TO_TELEPORT){
            Optional<BlockPos> blockPosOpt = entity.getBrain().getMemory(MKMemoryModuleTypes.SPAWN_POINT);
            blockPosOpt.ifPresent(blockPos -> entity.setPositionAndUpdate(blockPos.getX(),
                    blockPos.getY(), blockPos.getZ()));
        }
        entity.returnToSpawnTick();
    }

    @Override
    public boolean isPreemptible() {
        return false;
    }

    @Override
    public void resetTask() {
        super.resetTask();
        entity.getNavigator().clearPath();
        entity.getBrain().removeMemory(MemoryModuleType.PATH);
        entity.getBrain().removeMemory(MKMemoryModuleTypes.IS_RETURNING);
        entity.enterNonCombatMovementState();
    }

    private boolean needsToReturnHome(BlockPos spawn){
        Optional<LivingEntity> targetOpt = entity.getBrain().getMemory(MKMemoryModuleTypes.THREAT_TARGET);
        int distFromSpawn = spawn.manhattanDistance(entity.getPosition());
        if (distFromSpawn <= MIN_RANGE * 2){
            return false;
        }
        if (targetOpt.isPresent()){
            return distFromSpawn > LEASH_RANGE;
        } else {
            if (entity.getNonCombatMoveType() == MKEntity.NonCombatMoveType.RANDOM_WANDER){
                return distFromSpawn > LEASH_RANGE;
            }
            return true;
        }
    }

    public boolean shouldExecute() {
        Optional<BlockPos> blockPosOpt = entity.getBrain().getMemory(MKMemoryModuleTypes.SPAWN_POINT);
        if (blockPosOpt.isPresent()){
            BlockPos spawn = blockPosOpt.get();
            if (needsToReturnHome(spawn)){
                Path path = entity.getNavigator().getPathToPos(spawn, 1);
                entity.getNavigator().setPath(path, 1.0);
                entity.getBrain().setMemory(MemoryModuleType.PATH, path);
                return true;
            }
        }
        return false;
    }

    public boolean shouldContinueExecuting() {
        Optional<BlockPos> blockPosOpt = entity.getBrain().getMemory(MKMemoryModuleTypes.SPAWN_POINT);
        return blockPosOpt.map((pos) -> pos.manhattanDistance(entity.getPosition()) > MIN_RANGE).orElse(false) && !this.entity.getNavigator().noPath();
    }

    public void startExecuting() {
        ticksReturning = 0;
        entity.setAttackTarget(null);
        entity.setRevengeTarget(null);
        entity.getBrain().removeMemory(MKMemoryModuleTypes.THREAT_TARGET);
        entity.getBrain().setMemory(MKMemoryModuleTypes.IS_RETURNING, true);
    }

}
