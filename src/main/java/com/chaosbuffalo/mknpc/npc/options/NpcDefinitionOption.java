package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public abstract class NpcDefinitionOption {
    private final ResourceLocation name;
    public enum ApplyOrder {
        EARLY,
        MIDDLE,
        LATE
    }
    private final ApplyOrder ordering;

    public NpcDefinitionOption(ResourceLocation name, ApplyOrder order){
        this.name = name;
        this.ordering = order;
    }

    public ApplyOrder getOrdering() {
        return ordering;
    }

    public ResourceLocation getName() {
        return name;
    }

    public abstract void fromJson(Gson gson, JsonObject object);

    public abstract void applyToEntity(NpcDefinition definition, Entity entity);
}
