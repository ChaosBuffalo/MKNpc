package com.chaosbuffalo.mknpc.entity.ai.sensor;

import com.chaosbuffalo.mkcore.Capabilities;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.MKAbilityInfo;
import com.chaosbuffalo.mkcore.abilities.MKAbilityMemories;
import com.chaosbuffalo.mkcore.abilities.ai.AbilityDecisionContext;
import com.chaosbuffalo.mkcore.abilities.ai.AbilityTargetingDecision;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class AbilityUseSensor extends Sensor<MKEntity> {

    public AbilityUseSensor() {
        super(40);
    }

    @Override
    protected void update(@Nonnull ServerWorld worldIn, MKEntity entityIn) {
        Optional<MKAbility> abilityOptional = entityIn.getBrain().getMemory(MKMemoryModuleTypes.CURRENT_ABILITY);
        if (abilityOptional.isPresent())
            return;

        entityIn.getCapability(Capabilities.ENTITY_CAPABILITY).ifPresent(mkEntityData -> {
            AbilityDecisionContext context = createAbilityDecisionContext(entityIn);
            for (MKAbilityInfo ability : mkEntityData.getKnowledge().getAbilitiesPriorityOrder()) {
                MKAbility mkAbility = ability.getAbility();
                if (!mkEntityData.getAbilityExecutor().canActivateAbility(mkAbility))
                    continue;

                AbilityTargetingDecision targetSelection = mkAbility.getUseCondition().getDecision(context);
                if (targetSelection == AbilityTargetingDecision.UNDECIDED)
                    continue;

                if (mkAbility.isValidTarget(entityIn, targetSelection.getTargetEntity())) {
                    entityIn.getBrain().setMemory(MKAbilityMemories.ABILITY_TARGET, targetSelection.getTargetEntity());
                    entityIn.getBrain().setMemory(MKMemoryModuleTypes.CURRENT_ABILITY, mkAbility);
                    entityIn.getBrain().setMemory(MKMemoryModuleTypes.MOVEMENT_STRATEGY,
                            entityIn.getMovementStrategy(targetSelection));
                    entityIn.getBrain().setMemory(MKMemoryModuleTypes.MOVEMENT_TARGET, targetSelection.getTargetEntity());
                    return;
                }
            }
            entityIn.enterDefaultMovementState(context.getThreatTarget());
        });
    }

    @Nonnull
    private AbilityDecisionContext createAbilityDecisionContext(MKEntity entityIn) {
        Optional<LivingEntity> targetOptional = entityIn.getBrain().getMemory(MKMemoryModuleTypes.THREAT_TARGET);
        return new AbilityDecisionContext(entityIn, targetOptional.orElse(null),
                entityIn.getBrain().getMemory(MKMemoryModuleTypes.ALLIES).orElse(Collections.emptyList()),
                entityIn.getBrain().getMemory(MKMemoryModuleTypes.ENEMIES).orElse(Collections.emptyList()));
    }

    @Nonnull
    @Override
    public Set<MemoryModuleType<?>> getUsedMemories() {
        return ImmutableSet.of(MKMemoryModuleTypes.CURRENT_ABILITY, MKMemoryModuleTypes.THREAT_TARGET,
                MKAbilityMemories.ABILITY_TARGET, MKMemoryModuleTypes.ALLIES, MKMemoryModuleTypes.ENEMIES,
                MKMemoryModuleTypes.MOVEMENT_STRATEGY);
    }
}
