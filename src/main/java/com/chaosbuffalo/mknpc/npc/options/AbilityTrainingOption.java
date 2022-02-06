package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.abilities.AbilityManager;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.training.AbilityTrainingEntry;
import com.chaosbuffalo.mkcore.abilities.training.AbilityTrainingRequirement;
import com.chaosbuffalo.mkcore.abilities.training.IAbilityTrainingEntity;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AbilityTrainingOption extends SimpleOption<List<AbilityTrainingOption.AbilityTrainingOptionEntry>> {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "ability_trainings");

    public static class AbilityTrainingOptionEntry {
        private MKAbility ability;
        protected final List<AbilityTrainingRequirement> requirements = new ArrayList<>();

        public AbilityTrainingOptionEntry(MKAbility ability, List<AbilityTrainingRequirement> requirements) {
            this.ability = ability;
            this.requirements.addAll(requirements);
        }

        public <D> AbilityTrainingOptionEntry(Dynamic<D> dynamic) {
            deserialize(dynamic);
        }

        public <D> D serialize(DynamicOps<D> ops) {
            ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
            builder.put(ops.createString("ability"), ops.createString(ability.getAbilityId().toString()));
            builder.put(ops.createString("reqs"), ops.createList(requirements.stream().map(x -> x.serialize(ops))));
            return ops.createMap(builder.build());
        }

        public <D> void deserialize(Dynamic<D> dynamic) {

            ability = dynamic.get("ability").asString()
                    .resultOrPartial(MKNpc.LOGGER::error)
                    .map(ResourceLocation::new)
                    .map(MKCoreRegistry::getAbility)
                    .orElseThrow(IllegalArgumentException::new);

            List<Optional<AbilityTrainingRequirement>> optReqs = dynamic.get("reqs").asList(x -> {
                ResourceLocation typeName = AbilityTrainingRequirement.getType(x);
                Function<Dynamic<?>, AbilityTrainingRequirement> deserializer = AbilityManager.getAbilityTrainingReqDeserializer(typeName);
                if (deserializer != null) {
                    return Optional.of(deserializer.apply(x));
                } else {
                    return Optional.empty();
                }
            });
            for (Optional<AbilityTrainingRequirement> optReq : optReqs) {
                optReq.ifPresent(requirements::add);
            }
        }
    }

    public AbilityTrainingOption() {
        super(NAME);
        setValue(new ArrayList<>());
    }

    public AbilityTrainingOption withTrainingOption(MKAbility ability, AbilityTrainingRequirement... reqs) {
        getValue().add(new AbilityTrainingOptionEntry(ability, Arrays.asList(reqs)));
        return this;
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        List<AbilityTrainingOptionEntry> entries = dynamic.get("value").asList(AbilityTrainingOptionEntry::new);
        setValue(entries);
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("value"), ops.createList(getValue().stream().map(x -> x.serialize(ops))));
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity, List<AbilityTrainingOptionEntry> value) {
        if (entity instanceof IAbilityTrainingEntity) {
            for (AbilityTrainingOptionEntry entry : value) {
                if (entry.ability != null) {
                    AbilityTrainingEntry trainingEntry = ((IAbilityTrainingEntity) entity).getAbilityTrainer()
                            .addTrainedAbility(entry.ability);
                    for (AbilityTrainingRequirement req : entry.requirements) {
                        trainingEntry.addRequirement(req);
                    }
                }

            }
        }
    }
}
