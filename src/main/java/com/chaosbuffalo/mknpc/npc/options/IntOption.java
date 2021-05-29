package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;


public abstract class IntOption extends SimpleOption<Integer> {

    public IntOption(ResourceLocation name) {
        super(name);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup, ImmutableMap.of(
                ops.createString("value"), ops.createInt(getValue())
        )).result().orElse(sup);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        setValue(dynamic.get("value").asInt(0));
    }
}
