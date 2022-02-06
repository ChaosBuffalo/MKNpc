package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcAttributeEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AttributesOption extends NpcDefinitionOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "attributes");
    private final List<NpcAttributeEntry> attributes;

    public AttributesOption() {
        super(NAME, ApplyOrder.MIDDLE);
        attributes = new ArrayList<>();
    }

    public AttributesOption addAttributeEntry(NpcAttributeEntry entry) {
        attributes.add(entry);
        return this;
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        List<NpcAttributeEntry> entries = dynamic.get("attributes").asList(d -> {
            NpcAttributeEntry entry = new NpcAttributeEntry();
            entry.deserialize(d);
            return entry;
        });
        attributes.clear();
        attributes.addAll(entries);
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("attributes"), ops.createList(attributes.stream().map(x -> x.serialize(ops))));
    }

    @Override
    public boolean canBeBossStage() {
        return true;
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity) {
        if (entity instanceof LivingEntity) {
            AttributeModifierManager manager = ((LivingEntity) entity).getAttributeManager();
            for (NpcAttributeEntry entry : attributes) {
                ModifiableAttributeInstance instance = manager.createInstanceIfAbsent(entry.getAttribute());
                if (instance != null) {
                    instance.setBaseValue(entry.getValue());
                }
            }
        }
    }
}
