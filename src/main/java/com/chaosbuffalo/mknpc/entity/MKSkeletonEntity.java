package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mknpc.entity.attributes.NpcAttributes;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class MKSkeletonEntity extends MKEntity  {
    public static final String DEFAULT = "default";

    public MKSkeletonEntity(EntityType<? extends MKEntity> type, World worldIn) {
        super(type, worldIn);
        setCurrentModelLook(DEFAULT);
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes(double attackDamage, double movementSpeed) {
        return MKEntity.registerAttributes(attackDamage, movementSpeed)
                .createMutableAttribute(MKAttributes.SHADOW_RESISTANCE, 0.25)
                .createMutableAttribute(MKAttributes.HOLY_RESISTANCE, -0.25);
    }


    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }


    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_SKELETON_STEP;
    }

    @Override
    protected SoundEvent getShootSound() {
        return SoundEvents.ENTITY_SKELETON_SHOOT;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEAD;
    }

}
