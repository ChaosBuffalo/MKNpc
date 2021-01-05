package com.chaosbuffalo.mknpc.entity.ai.goal;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.core.CombatExtensionModule;
import com.chaosbuffalo.mkcore.core.IMKEntityData;
import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkcore.events.PostAttackEvent;
import com.chaosbuffalo.mkcore.utils.EntityUtils;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;

import java.util.EnumSet;
import java.util.Optional;

public class MKMeleeAttackGoal extends Goal {
    private final MKEntity entity;
    private LivingEntity target;

    @Override
    public boolean shouldExecute() {
        Brain<?> brain = entity.getBrain();
        Optional<LivingEntity> targetOpt = brain.getMemory(MKMemoryModuleTypes.THREAT_TARGET);
        if (targetOpt.isPresent()) {
            LivingEntity target = targetOpt.get();
            if (isInReach(target)) {
                this.target = target;
                return true;
            }
        }
        return false;
    }

    public MKMeleeAttackGoal(MKEntity entity) {
        this.entity = entity;
        this.target = null;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }


    public void startExecuting() {
        this.entity.setAggroed(true);
    }

    public int getComboCount() {
        return entity.getAttackComboCount();
    }

    public int getComboDelay() {
        return entity.getAttackComboCooldown();
    }

    @Override
    public void tick() {
        entity.getNavigator().tryMoveToEntityLiving(target, entity.getLungeSpeed());
        entity.getLookController().setLookPositionWithEntity(target, 30.0f, 30.0f);
        double cooldownPeriod = EntityUtils.getCooldownPeriod(entity);
        int ticksSinceSwing = entity.getTicksSinceLastSwing();
        if (ticksSinceSwing >= cooldownPeriod && isInReach(target)) {
            performAttack(target);
        }

    }

    protected void performAttack(LivingEntity enemy) {
        entity.swingArm(Hand.MAIN_HAND);
        entity.attackEntityAsMob(enemy);
        entity.resetSwing();
        ItemStack mainHand = entity.getHeldItemMainhand();
        if (!mainHand.isEmpty()){
            mainHand.getItem().hitEntity(mainHand, enemy, entity);
        }
        LazyOptional<? extends IMKEntityData> entityData = MKCore.getEntityData(entity);
        entityData.ifPresent(cap -> {
            cap.getCombatExtension().recordSwing();
            MinecraftForge.EVENT_BUS.post(new PostAttackEvent(entity));
            if (cap.getCombatExtension().getCurrentSwingCount() > 0 &&
                    cap.getCombatExtension().getCurrentSwingCount() % getComboCount() == 0){
                entity.subtractFromTicksSinceLastSwing(getComboDelay());
            }
        });
    }

    public boolean isInMeleeRange(LivingEntity target){
        return entity.getDistanceSq(target) <= this.getAttackReachSqr(target) * 2.0;
    }

    public boolean isInReach(LivingEntity target) {
        return entity.getDistanceSq(target) <= this.getAttackReachSqr(target);
    }

    public void resetTask() {
        this.entity.setAggroed(false);
        this.target = null;
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        double range = entity.getAttribute(MKAttributes.ATTACK_REACH).getValue();
        range *= entity.getRenderScale();
        return range * range;
    }

    @Override
    public boolean shouldContinueExecuting() {
        Brain<?> brain = entity.getBrain();
        Optional<LivingEntity> targetOpt = brain.getMemory(MKMemoryModuleTypes.THREAT_TARGET);
        return target != null && targetOpt.map((ent) -> ent.isEntityEqual(target) && isInMeleeRange(ent)).orElse(false);
    }


}
