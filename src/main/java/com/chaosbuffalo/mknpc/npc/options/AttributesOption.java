package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcAttributeEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttributesOption extends NpcDefinitionOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "attributes");
    private final List<NpcAttributeEntry> attributes;

    public AttributesOption(){
        super(NAME, ApplyOrder.MIDDLE);
        attributes = new ArrayList<>();
    }

    public void addAttributeEntry(NpcAttributeEntry entry){
        attributes.add(entry);
    }

    @Override
    public void fromJson(Gson gson, JsonObject object) {
        JsonArray attributeArray  = object.getAsJsonArray(NAME.toString());
        for (JsonElement attr : attributeArray){
            NpcAttributeEntry entry = gson.fromJson(attr, NpcAttributeEntry.class);
            addAttributeEntry(entry);
        }
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity) {
        if (entity instanceof LivingEntity){
            AbstractAttributeMap attributeMap = ((LivingEntity)entity).getAttributes();
            for (NpcAttributeEntry entry : attributes){
                IAttributeInstance attribute = attributeMap.getAttributeInstanceByName(entry.getAttributeName());
                if (attribute != null){
                    attribute.setBaseValue(entry.getValue());
                }
            }
        }
    }
}
