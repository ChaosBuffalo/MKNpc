package com.chaosbuffalo.mknpc.entity.ai.goal;

import com.chaosbuffalo.mknpc.entity.ai.MovementUtils;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.util.math.Vec3d;

public class MKSwimGoal extends SwimGoal {
    private CreatureEntity entity;
    private boolean walkOutPath;

    public MKSwimGoal(CreatureEntity entityIn) {
        super(entityIn);
        this.entity = entityIn;
        this.walkOutPath = false;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        setWalkTargetOut();
    }



    private void setWalkTargetOut(){
        Vec3d targetPos = MovementUtils.findRandomTargetBlockAwayFromNoWater(
                entity, 5, 3, entity.getPositionVec());
        if (targetPos != null){
            entity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, 1.0f, 1));
            this.walkOutPath = true;
        } else {
            walkOutPath = false;
        }
    }

    @Override
    public void tick() {
        if (this.entity.getRNG().nextFloat() < 0.05F) {
            this.entity.getJumpController().setJumping();
        }
        if (!walkOutPath || entity.getNavigator().noPath()){
            setWalkTargetOut();
        }
    }
}
