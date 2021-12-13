package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mkcore.core.MKAttributes;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.monster.piglin.PiglinAction;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class MKZombifiedPiglinEntity extends MKAbstractPiglinEntity{
    private static final DataParameter<Boolean> CHARGING_CROSSBOW = EntityDataManager.createKey(MKZombifiedPiglinEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DANCING = EntityDataManager.createKey(MKZombifiedPiglinEntity.class, DataSerializers.BOOLEAN);

    public MKZombifiedPiglinEntity(EntityType<? extends MKZombifiedPiglinEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public PiglinAction getPiglinAction() {
        if (isDancing()) {
            return PiglinAction.DANCING;
        } else if (isAggressive() && isHoldingMeleeWeapon()) {
            return PiglinAction.ATTACKING_WITH_MELEE_WEAPON;
        } else if (isChargingCrossbow()) {
            return PiglinAction.CROSSBOW_CHARGE;
        } else {
            return isAggressive() && canEquip(Items.CROSSBOW) ? PiglinAction.CROSSBOW_HOLD : PiglinAction.DEFAULT;
        }
    }

    public void setDancing(boolean isDancing) {
        this.dataManager.set(DANCING, isDancing);
    }

    public boolean isDancing(){
        return this.dataManager.get(DANCING);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CHARGING_CROSSBOW, false);
        this.dataManager.register(DANCING, false);
    }

    public void setChargingCrossbow(boolean isCharging){
        this.dataManager.set(CHARGING_CROSSBOW, isCharging);
    }

    public boolean isChargingCrossbow() {
        return this.dataManager.get(CHARGING_CROSSBOW);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.hasThreatTarget() ? SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_DEATH;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes(double attackDamage, double movementSpeed) {
        return MKEntity.registerAttributes(attackDamage, movementSpeed)
                .createMutableAttribute(MKAttributes.SHADOW_RESISTANCE, 0.25)
                .createMutableAttribute(MKAttributes.FIRE_RESISTANCE, 0.25)
                .createMutableAttribute(MKAttributes.HOLY_RESISTANCE, -0.25);
    }
}
