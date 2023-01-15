package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkillOption extends NpcDefinitionOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "skills");
    private final Set<Attribute> minorSkills = new HashSet<>();
    private final Set<Attribute> majorSkills = new HashSet<>();
    private final Set<Attribute> remedialSkills = new HashSet<>();

    public SkillOption(ResourceLocation name, ApplyOrder order) {
        super(name, order);
    }

    public SkillOption() {
        super(NAME, ApplyOrder.EARLY);
    }


    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity, double difficultyLevel) {
        if (entity instanceof LivingEntity) {
            AttributeModifierManager manager =((LivingEntity) entity).getAttributeManager();
            for (Attribute attr : remedialSkills) {
                ModifiableAttributeInstance instance = manager.createInstanceIfAbsent(attr);
                if (instance != null) {
                    instance.setBaseValue(difficultyLevel * .4);
                }
            }
            for (Attribute attr : minorSkills) {
                ModifiableAttributeInstance instance = manager.createInstanceIfAbsent(attr);
                if (instance != null) {
                    instance.setBaseValue(difficultyLevel * .6);
                }
            }
            for (Attribute attr : majorSkills) {
                ModifiableAttributeInstance instance = manager.createInstanceIfAbsent(attr);
                if (instance != null) {
                    instance.setBaseValue(difficultyLevel);
                }
            }
        }
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        List<Attribute> minor_skill_entries = dynamic.get("minor_skills").asList(d ->
                ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(
                        d.asString("mknpc:invalid_attribute"))));
        minorSkills.clear();
        minorSkills.addAll(minor_skill_entries);
        List<Attribute> major_skill_entries = dynamic.get("major_skills").asList(d ->
                ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(
                        d.asString("mknpc:invalid_attribute"))));
        majorSkills.clear();
        majorSkills.addAll(major_skill_entries);
        List<Attribute> remedial_skill_entries = dynamic.get("remedial_skills").asList(d ->
                ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(
                        d.asString("mknpc:invalid_attribute"))));
        remedialSkills.clear();
        remedialSkills.addAll(remedial_skill_entries);
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("minor_skills"), ops.createList(minorSkills.stream()
                .map(x -> ops.createString(x.getRegistryName().toString()))));
        builder.put(ops.createString("major_skills"), ops.createList(majorSkills.stream()
                .map(x -> ops.createString(x.getRegistryName().toString()))));
        builder.put(ops.createString("remedial_skills"), ops.createList(remedialSkills.stream()
                .map(x -> ops.createString(x.getRegistryName().toString()))));
    }

    public SkillOption addMajorSkill(Attribute skill) {
        majorSkills.add(skill);
        return this;
    }

    public SkillOption addMinorSkill(Attribute skill) {
        minorSkills.add(skill);
        return this;
    }

    public SkillOption addRemedialSkill(Attribute skill) {
        remedialSkills.add(skill);
        return this;
    }
}
