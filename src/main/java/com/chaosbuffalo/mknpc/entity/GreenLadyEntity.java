package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mkcore.abilities.training.*;
import com.chaosbuffalo.mkcore.mku.abilities.*;
import com.chaosbuffalo.mknpc.entity.ai.controller.MovementStrategyController;
import com.chaosbuffalo.mknpc.entity.ai.goal.*;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;


@SuppressWarnings("EntityConstructor")
public class GreenLadyEntity extends MKEntity implements IAbilityTrainingEntity {
    private int timesDone;
    private final EntityAbilityTrainer abilityTrainer;

    public GreenLadyEntity(EntityType<? extends GreenLadyEntity> type, World worldIn) {
        super(type, worldIn);
        timesDone = 0;
        abilityTrainer = new EntityAbilityTrainer(this);
        abilityTrainer.addTrainedAbility(EmberAbility.INSTANCE);
        abilityTrainer.addTrainedAbility(ClericHeal.INSTANCE);
        abilityTrainer.addTrainedAbility(WhirlwindBlades.INSTANCE)
                .addRequirement(new HeldItemRequirement(Items.DIAMOND_SWORD, Hand.MAIN_HAND))
                .addRequirement(new ExperienceLevelRequirement(5));
        if (!worldIn.isRemote()){
            setComboDefaults(6, 20);
            setMeleeComboCount(6);
            setMeleeComboCooldown(20);
        }
        setLungeSpeed(2.0);
    }



    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
                                            @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {

        ILivingEntityData entityData = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.getCapability(CoreCapabilities.ENTITY_CAPABILITY).ifPresent(
                mkEntityData -> {
                    mkEntityData.getKnowledge().learnAbility(EmberAbility.INSTANCE, 2);
                    mkEntityData.getKnowledge().learnAbility(FireArmor.INSTANCE);
                    mkEntityData.getKnowledge().learnAbility(ClericHeal.INSTANCE);
                    mkEntityData.getKnowledge().learnAbility(SkinLikeWoodAbility.INSTANCE);
                });
//        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.3);
        return entityData;

    }



    @Override
    public IAbilityTrainer getAbilityTrainer() {
        return abilityTrainer;
    }
}
