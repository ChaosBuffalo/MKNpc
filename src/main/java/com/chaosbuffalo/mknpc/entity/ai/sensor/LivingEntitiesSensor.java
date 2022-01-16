package com.chaosbuffalo.mknpc.entity.ai.sensor;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.ai.memory.MKMemoryModuleTypes;
import com.chaosbuffalo.targeting_api.Targeting;
import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.stream.Collectors;

public class LivingEntitiesSensor extends Sensor<MKEntity> {

    public LivingEntitiesSensor() {
        super(10);
    }

    protected void update(ServerWorld worldIn, MKEntity entityIn) {
        List<LivingEntity> entities = worldIn.getLoadedEntitiesWithinAABB(LivingEntity.class,
                entityIn.getBoundingBox().grow(16.0D, 16.0D, 16.0D),
                (entity) -> entity != entityIn && entity.isAlive());
        entities.sort(Comparator.comparingDouble(entityIn::getDistanceSq));
        Brain<?> brain = entityIn.getBrain();

        Map<Targeting.TargetRelation, List<LivingEntity>> groups = entities.stream()
                .collect(Collectors.groupingBy(other -> Targeting.getTargetRelation(entityIn, other)));

        List<LivingEntity> enemies = groups.getOrDefault(Targeting.TargetRelation.ENEMY, Collections.emptyList());
        List<LivingEntity> friends = groups.getOrDefault(Targeting.TargetRelation.FRIEND, Collections.emptyList())
                .stream()
                .sorted(this::sortByHealth)
                .collect(Collectors.toList());
        brain.setMemory(MKMemoryModuleTypes.ENEMIES, enemies);
        brain.setMemory(MKMemoryModuleTypes.ALLIES, friends);
        brain.setMemory(MKMemoryModuleTypes.VISIBLE_ENEMIES, enemies.stream().filter(x -> entityIn.getEntitySenses().canSee(x))
                .collect(Collectors.toList()));
    }

    private int sortByHealth(LivingEntity friend, LivingEntity other) {
        return Float.compare(friend.getHealth() / friend.getMaxHealth(), other.getHealth() / other.getMaxHealth());
    }

    public Set<MemoryModuleType<?>> getUsedMemories() {
        return ImmutableSet.of(MKMemoryModuleTypes.ENEMIES, MKMemoryModuleTypes.ALLIES,
                MKMemoryModuleTypes.VISIBLE_ENEMIES);
    }
}
