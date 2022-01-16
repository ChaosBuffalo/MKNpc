package com.chaosbuffalo.mknpc.entity.ai.goal;

import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.abilities.AbilityContext;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.MKAbilityMemories;
import com.chaosbuffalo.mkcore.abilities.ai.BrainAbilityContext;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Optional;

public class UseAbilityGoal extends Goal {
    public static final int CAN_SEE_TIMEOUT = 10;
    private final MKEntity entity;
    private MKAbility currentAbility;
    private LivingEntity target;
    private int ticksSinceSeenTarget;

    public UseAbilityGoal(MKEntity entity) {
        this.entity = entity;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        ticksSinceSeenTarget = 0;
    }

    @Override
    public boolean isPreemptible() {
        return false;
    }

    @Override
    public boolean shouldExecute() {
        Optional<MKAbility> abilityOptional = entity.getBrain().getMemory(MKMemoryModuleTypes.CURRENT_ABILITY);
        Optional<LivingEntity> target = entity.getBrain().getMemory(MKAbilityMemories.ABILITY_TARGET);
        if (abilityOptional.isPresent() && target.isPresent()) {
            currentAbility = abilityOptional.get();
            LivingEntity targetEntity = target.get();

            if (!canActivate())
                return false;

            if (entity != targetEntity) {
                if (!isInRange(currentAbility, targetEntity))
                    return false;
                if (!entity.getEntitySenses().canSee(targetEntity))
                    return false;
            }

            // Now we know we can actually start the cast
            this.target = targetEntity;
            return true;
        } else {
            return false;
        }
    }

    protected boolean isInRange(MKAbility ability, LivingEntity target) {
        float range = ability.getDistance(entity);
        return target.getDistanceSq(entity) <= range * range;
    }

    public boolean canActivate() {
        return entity.getCapability(CoreCapabilities.ENTITY_CAPABILITY).map((entityData) ->
                entityData.getAbilityExecutor().canActivateAbility(currentAbility))
                .orElse(false);
    }

    public boolean shouldContinueExecuting() {
        return ticksSinceSeenTarget < CAN_SEE_TIMEOUT && entity.getCapability(CoreCapabilities.ENTITY_CAPABILITY).map(
                (entityData) -> entityData.getAbilityExecutor().isCasting()).orElse(false) && entity.getBrain()
                .getMemory(MKAbilityMemories.ABILITY_TARGET).map(tar -> tar.isAlive()
                        && tar.isEntityEqual(target)).orElse(false) && entity.getBrain().getMemory(MKMemoryModuleTypes.CURRENT_ABILITY)
                .map(mkAbility -> mkAbility.equals(currentAbility)).orElse(false);
    }

    @Override
    public void startExecuting() {
        if (!target.isEntityEqual(entity)) {
            entity.faceEntity(target, 360.0f, 360.0f);
            entity.getLookController().setLookPositionWithEntity(target, 50.0f, 50.0f);
        }
        AbilityContext context = new BrainAbilityContext(entity);
        MKCore.LOGGER.info("ai {} casting {} on {}", entity, currentAbility.getAbilityId(), target);
        entity.getCapability(CoreCapabilities.ENTITY_CAPABILITY).ifPresent(
                (entityData) -> entityData.getAbilityExecutor().executeAbilityWithContext(currentAbility.getAbilityId(), context));
    }

    @Override
    public void tick() {
        if (!target.isEntityEqual(entity)){
            entity.faceEntity(target, 50.0f, 50.0f);
            entity.getLookController().setLookPositionWithEntity(target, 50.0f, 50.0f);
            if (entity.getEntitySenses().canSee(target)){
                ticksSinceSeenTarget = 0;
            } else {
                ticksSinceSeenTarget++;
            }
        }
    }

    @Override
    public void resetTask() {
        super.resetTask();
        currentAbility = null;
        target = null;
        entity.getBrain().removeMemory(MKMemoryModuleTypes.CURRENT_ABILITY);
        entity.getBrain().removeMemory(MKAbilityMemories.ABILITY_TARGET);
        entity.returnToDefaultMovementState();
        ticksSinceSeenTarget = 0;

    }
}
