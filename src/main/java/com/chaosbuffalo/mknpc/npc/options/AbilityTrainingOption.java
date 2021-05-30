package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.training.IAbilityTrainer;
import com.chaosbuffalo.mkcore.abilities.training.IAbilityTrainingEntity;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;

public class AbilityTrainingOption extends ResourceLocationListOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "ability_trainings");

    public AbilityTrainingOption() {
        super(NAME);
    }

    public AbilityTrainingOption withOptions(List<MKAbility> abilities){
        setValue(abilities.stream().map(MKAbility::getAbilityId).collect(Collectors.toList()));
        return this;
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity, List<ResourceLocation> value) {
        if (entity instanceof IAbilityTrainingEntity){
            for (ResourceLocation abilityId : value){
                MKAbility ability = MKCoreRegistry.getAbility(abilityId);
                if (ability != null){
                    ((IAbilityTrainingEntity) entity).getAbilityTrainer().addTrainedAbility(ability);
                }
            }
        }
    }
}
