package com.chaosbuffalo.mknpc.entity.ai.goal;


import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.Optional;

public class MKTargetGoal extends TargetGoal {
    private final MKEntity entity;

    public MKTargetGoal(MKEntity mobIn, boolean checkSight, boolean nearbyOnlyIn) {
        super(mobIn, checkSight, nearbyOnlyIn);
        this.entity = mobIn;
    }

    @Override
    public boolean shouldExecute() {
        Optional<LivingEntity> opt = goalOwner.getBrain().getMemory(MKMemoryModuleTypes.THREAT_TARGET);
        if (opt.isPresent() && (this.target == null || !this.target.isEntityEqual(opt.get()))) {
            this.target = opt.get();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        Optional<LivingEntity> opt = goalOwner.getBrain().getMemory(MKMemoryModuleTypes.THREAT_TARGET);
        return opt.isPresent() && opt.get().isEntityEqual(target);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        entity.setAggroed(false);
    }

    public void startExecuting() {
        this.goalOwner.setAttackTarget(this.target);
        entity.setAggroed(true);
        entity.enterCombatMovementState(target);
        super.startExecuting();
    }
}
