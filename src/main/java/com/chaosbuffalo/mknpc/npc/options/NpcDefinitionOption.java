package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class NpcDefinitionOption {
    public final static ResourceLocation INVALID_OPTION = new ResourceLocation(MKNpc.MODID, "npc_option.invalid");
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

    public <D> D serialize(DynamicOps<D> ops){
        return ops.createMap(ImmutableMap.of(
                ops.createString("optionType"), ops.createString(getName().toString())
        ));
    }

    public static <D> ResourceLocation getType(Dynamic<D> dynamic){
        return new ResourceLocation(dynamic.get("optionType").asString().result().orElse(INVALID_OPTION.toString()));
    }

    public abstract <D> void deserialize(Dynamic<D> dynamic);

    public ApplyOrder getOrdering() {
        return ordering;
    }

    public ResourceLocation getName() {
        return name;
    }

    public abstract void applyToEntity(NpcDefinition definition, Entity entity);

    public boolean canBeBossStage(){
        return false;
    }
}
