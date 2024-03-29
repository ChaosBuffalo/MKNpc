package com.chaosbuffalo.mknpc.entity.ai.goal;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.core.CombatExtensionModule;
import com.chaosbuffalo.mkcore.utils.ItemUtils;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import com.chaosbuffalo.mkweapons.items.MKBow;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.EnumSet;
import java.util.Optional;

public class MKBowAttackGoal extends Goal {
    private final MKEntity entity;
    private LivingEntity target;
    private int defaultCooldown;
    private final float maxAttackDistance;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    protected static int SEE_TIME_TIMEOUT = 60;

    public MKBowAttackGoal(MKEntity mob, int attackCooldownIn, float maxAttackDistanceIn) {
        this.entity = mob;
        this.defaultCooldown = attackCooldownIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public void setDefaultCooldown(int attackCooldownIn) {
        this.defaultCooldown = attackCooldownIn;
    }

    @Override
    public boolean isPreemptible() {
        return false;
    }

    @Override
    public boolean shouldExecute() {
        if (!ItemUtils.isRangedWeapon(entity.getHeldItemMainhand())){
            return false;
        }
        Brain<?> brain = entity.getBrain();
        Optional<LivingEntity> targetOpt = brain.getMemory(MKMemoryModuleTypes.THREAT_TARGET);
        if (targetOpt.isPresent()) {
            LivingEntity target = targetOpt.get();
            if (isInReach(target) && entity.getEntitySenses().canSee(target)) {
                this.target = target;
                return true;
            }
        }
        return false;
    }

    public boolean isInReach(LivingEntity target) {
        return entity.getDistanceSq(target) <= maxAttackDistance;
    }

    @Override
    public boolean shouldContinueExecuting() {
        Brain<?> brain = entity.getBrain();
        Optional<LivingEntity> targetOpt = brain.getMemory(MKMemoryModuleTypes.THREAT_TARGET);
        return target != null && seeTime >= -SEE_TIME_TIMEOUT && ItemUtils.isRangedWeapon(entity.getHeldItemMainhand()) && targetOpt.map(
                (ent) -> ent.isEntityEqual(target) && isInReach(ent)).orElse(false);
    }

    public void startExecuting() {
        super.startExecuting();
        this.entity.setAggroed(true);
    }

    public int getDrawTime(){
        ItemStack item = entity.getHeldItemMainhand();
        float drawTime;
        if (item.getItem() instanceof MKBow){
            drawTime = ((MKBow) item.getItem()).getDrawTime(item, entity);
        } else {
            drawTime = 20.0f;
        }
        return Math.round(drawTime);
    }

    public void resetTask() {
        super.resetTask();
        this.entity.setAggroed(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.target = null;
        this.entity.resetActiveHand();
    }

    public float getLaunchVelocity(float powerFactor){
        ItemStack item = entity.getHeldItemMainhand();
        if (item.getItem() instanceof MKBow){
            MKBow mkBow = (MKBow) item.getItem();
            float launchVel = mkBow.getLaunchVelocity(item, entity);
            return powerFactor * launchVel;
        } else {
            return powerFactor * 1.6f;
        }
    }

    public float getLaunchPower(int useTicks){
        ItemStack item = entity.getHeldItemMainhand();
        if (item.getItem() instanceof MKBow){
            MKBow mkBow = (MKBow) item.getItem();
            return mkBow.getPowerFactor(useTicks, item, entity);
        } else {
            return BowItem.getArrowVelocity(useTicks);
        }
    }

    public void tick() {

        if (target != null) {
            double d0 = this.entity.getDistanceSq(target.getPosX(), target.getPosY(), target.getPosZ());
            boolean canSee = this.entity.getEntitySenses().canSee(target);
            boolean seenIt = this.seeTime > 0;
            if (canSee != seenIt) {
                this.seeTime = 0;
            }

            if (canSee) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (!(d0 > (double)this.maxAttackDistance) && this.seeTime >= GameConstants.TICKS_PER_SECOND) {
                this.entity.getNavigator().clearPath();
                ++this.strafingTime;
            } else {
                this.entity.getNavigator().tryMoveToEntityLiving(target, entity.getLungeSpeed());
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double)this.entity.getRNG().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double)this.entity.getRNG().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (d0 > (double)(this.maxAttackDistance * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (d0 < (double)(this.maxAttackDistance * 0.25F)) {
                    this.strafingBackwards = true;
                }

                this.entity.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.entity.faceEntity(target, 30.0F, 30.0F);
            } else {
                this.entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
            }

            if (this.entity.isHandActive()) {
                if (!canSee && this.seeTime < -SEE_TIME_TIMEOUT) {
                    this.entity.resetActiveHand();
                } else if (canSee) {
                    int useTicks = this.entity.getItemInUseMaxCount();
                    if (useTicks >= getDrawTime()) {
                        float powerFactor = getLaunchPower(useTicks);
                        this.entity.attackEntityWithRangedAttack(target, powerFactor, getLaunchVelocity(powerFactor));
                        this.entity.resetActiveHand();
                        boolean fullCooldown = MKCore.getEntityData(entity).map(cap -> {
                            CombatExtensionModule combatExtensionModule = cap.getCombatExtension();
                            return combatExtensionModule.getCurrentProjectileHitCount() > 0 &&
                                    combatExtensionModule.getCurrentProjectileHitCount() % entity.getAttackComboCount() == 0;

                        }).orElse(true);
                        this.attackTime = fullCooldown ? entity.getAttackComboCooldown() : defaultCooldown;
                    }
                }
            } else if (--this.attackTime <= 0 && this.seeTime >= -SEE_TIME_TIMEOUT) {
                this.entity.setActiveHand(Hand.MAIN_HAND);
            }

        }
    }
}
