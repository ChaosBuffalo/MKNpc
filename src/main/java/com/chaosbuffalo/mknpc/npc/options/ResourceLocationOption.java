package com.chaosbuffalo.mknpc.npc.options;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;

public abstract class ResourceLocationOption extends SimpleOption<ResourceLocation> {

    public ResourceLocationOption(ResourceLocation name) {
        super(name);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup, ImmutableMap.of(
                ops.createString("value"), ops.createString(getValue().toString())
        )).result().orElse(sup);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        setValue(new ResourceLocation(dynamic.get("value").asString(
                String.format("%s.invalid_decode", getName().toString()))));
    }
}
