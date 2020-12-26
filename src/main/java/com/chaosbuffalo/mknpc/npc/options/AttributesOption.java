package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcAbilityEntry;
import com.chaosbuffalo.mknpc.npc.NpcAttributeEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AttributesOption extends NpcDefinitionOption{
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "attributes");
    private final List<NpcAttributeEntry> attributes;

    public AttributesOption(){
        super(NAME, ApplyOrder.MIDDLE);
        attributes = new ArrayList<>();
    }

    public AttributesOption addAttributeEntry(NpcAttributeEntry entry){
        attributes.add(entry);
        return this;
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        List<NpcAttributeEntry> entries = dynamic.get("options").asList(d -> {
            NpcAttributeEntry entry = new NpcAttributeEntry();
            entry.deserialize(d);
            return entry;
        });
        attributes.clear();
        attributes.addAll(entries);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup,
                ops.createString("options"),
                ops.createList(attributes.stream().map(x -> x.serialize(ops)))
        ).result().orElse(sup);
    }

    @Override
    public void fromJson(Gson gson, JsonObject object) {
        // WE NEED TO FIX
        JsonArray attributeArray  = object.getAsJsonArray(NAME.toString());
        for (JsonElement attr : attributeArray){
            NpcAttributeEntry entry = gson.fromJson(attr, NpcAttributeEntry.class);
            addAttributeEntry(entry);
        }
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity) {
        if (entity instanceof LivingEntity){
            AttributeModifierManager manager = ((LivingEntity)entity).getAttributeManager();
            for (NpcAttributeEntry entry : attributes){
                ModifiableAttributeInstance instance = manager.createInstanceIfAbsent(entry.getAttribute());
                if (instance != null){
                    instance.setBaseValue(entry.getValue());
                }
            }
        }
    }
}
