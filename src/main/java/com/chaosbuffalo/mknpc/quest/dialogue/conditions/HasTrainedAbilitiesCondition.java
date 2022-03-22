package com.chaosbuffalo.mknpc.quest.dialogue.conditions;

import com.chaosbuffalo.mkchat.dialogue.conditions.DialogueCondition;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mknpc.MKNpc;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class HasTrainedAbilitiesCondition extends DialogueCondition {
    public static final ResourceLocation conditionTypeName = new ResourceLocation(MKNpc.MODID, "has_trained_abilities");
    private final List<ResourceLocation> abilities = new ArrayList<>();
    private boolean allMatch;

    public HasTrainedAbilitiesCondition(boolean allMatch, ResourceLocation... abilityIds) {
        this(allMatch, Arrays.asList(abilityIds));
    }

    public HasTrainedAbilitiesCondition(boolean allMatch, List<ResourceLocation> abilityIds) {
        this();
        this.allMatch = allMatch;
        abilities.addAll(abilityIds);
    }

    public HasTrainedAbilitiesCondition() {
        super(conditionTypeName);
    }

    @Override
    public boolean meetsCondition(ServerPlayerEntity player, LivingEntity source) {
        return MKCore.getPlayer(player).map(playerData -> {
            if (allMatch) {
                return abilities.stream().allMatch(abilityId -> playerData.getAbilities().knowsAbility(abilityId));
            } else {
                return abilities.stream().anyMatch(abilityId -> playerData.getAbilities().knowsAbility(abilityId));
            }
        }).orElse(false);
    }

    @Override
    public HasTrainedAbilitiesCondition copy() {
        return new HasTrainedAbilitiesCondition(allMatch, abilities);
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        this.allMatch = dynamic.get("allMatch").asBoolean(false);

        dynamic.get("abilities").asStream()
                .map(entry -> entry.asString().result()
                        .map(ResourceLocation::new)
                        .orElseThrow(() -> new IllegalStateException("Failed to decode ability from: %s" + entry)))
                .forEach(abilities::add);
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("allMatch"), ops.createBoolean(allMatch));
        builder.put(ops.createString("abilities"),
                ops.createList(abilities.stream().map(x -> ops.createString(x.toString()))));
    }
}