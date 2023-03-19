package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mkcore.core.MKAttributes;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;



public class MKGolemEntity extends MKEntity  {
    public static final String DEFAULT = "default";
    public MKGolemEntity(EntityType<? extends MKGolemEntity> type, World worldIn) {
        super(type, worldIn);
        setCurrentModelLook(DEFAULT);
    }

    public static AttributeModifierMap.MutableAttribute registerAttributes(double attackDamage, double movementSpeed) {
        return MKEntity.registerAttributes(attackDamage, movementSpeed)
                .createMutableAttribute(MKAttributes.SHADOW_RESISTANCE, 0.25)
                .createMutableAttribute(MKAttributes.BLEED_RESISTANCE, 0.25)
                .createMutableAttribute(MKAttributes.RANGED_RESISTANCE, 0.25)
                .createMutableAttribute(MKAttributes.POISON_RESISTANCE, 0.75)
                .createMutableAttribute(MKAttributes.HOLY_RESISTANCE, 0.50)
                .createMutableAttribute(MKAttributes.FIRE_RESISTANCE, 0.25)
                .createMutableAttribute(MKAttributes.ARCANE_RESISTANCE, 0.25)
                .createMutableAttribute(MKAttributes.FROST_RESISTANCE, 0.25)
                .createMutableAttribute(MKAttributes.NATURE_RESISTANCE, 0.25)
                .createMutableAttribute(Attributes.ARMOR, 10);
    }



    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }


    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_STEP;
    }

    @Override
    protected SoundEvent getShootSound() {
        return SoundEvents.ENTITY_SKELETON_SHOOT;
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEFINED;
    }

}
