package com.chaosbuffalo.mknpc.npc.options;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class ResourceLocationListOption extends SimpleOption<List<ResourceLocation>> {
    public ResourceLocationListOption(ResourceLocation name) {
        super(name);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup, ImmutableMap.of(
                ops.createString("value"), ops.createList(getValue().stream().map(
                        x -> ops.createString(x.toString())))
        )).result().orElse(sup);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        List<ResourceLocation> val = new ArrayList<>();
        List<DataResult<String>> decoded = dynamic.get("value").asList(Dynamic::asString);
        for (DataResult<String> data : decoded){
            data.result().ifPresent(s -> val.add(new ResourceLocation(s)));
        }
        setValue(val);
    }
}