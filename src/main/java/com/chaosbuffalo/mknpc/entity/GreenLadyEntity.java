package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mkcore.abilities.training.*;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;


@SuppressWarnings("EntityConstructor")
public class GreenLadyEntity extends MKEntity implements IAbilityTrainingEntity {
    private final EntityAbilityTrainer abilityTrainer;

    public GreenLadyEntity(EntityType<? extends GreenLadyEntity> type, World worldIn) {
        super(type, worldIn);
        abilityTrainer = new EntityAbilityTrainer(this);
//        abilityTrainer.addTrainedAbility(EmberAbility.INSTANCE);
//        abilityTrainer.addTrainedAbility(ClericHeal.INSTANCE);
//        abilityTrainer.addTrainedAbility(WhirlwindBlades.INSTANCE)
//                .addRequirement(new HeldItemRequirement(Items.DIAMOND_SWORD, Hand.MAIN_HAND))
//                .addRequirement(new ExperienceLevelRequirement(5));
        if (!worldIn.isRemote()){
            setComboDefaults(6, 20);
            setAttackComboCount(6);
            setAttackComboCooldown(20);
        }
        setLungeSpeed(2.0);
    }



    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
                                            @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {

        ILivingEntityData entityData = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
//        this.getCapability(CoreCapabilities.ENTITY_CAPABILITY).ifPresent(
//                mkEntityData -> {
//                    mkEntityData.getKnowledge().learnAbility(EmberAbility.INSTANCE, 2);
//                    mkEntityData.getKnowledge().learnAbility(FireArmor.INSTANCE);
//                    mkEntityData.getKnowledge().learnAbility(ClericHeal.INSTANCE);
//                });
        return entityData;

    }



    @Override
    public IAbilityTrainer getAbilityTrainer() {
        return abilityTrainer;
    }
}
