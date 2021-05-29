package com.chaosbuffalo.mknpc.npc.options;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;

public abstract class FloatOption extends SimpleOption<Float> {

    public FloatOption(ResourceLocation name) {
        super(name);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup, ImmutableMap.of(
                ops.createString("value"), ops.createFloat(getValue())
        )).result().orElse(sup);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        setValue(dynamic.get("value").asFloat(1.0f));
    }
}
