package com.chaosbuffalo.mknpc.quest.requirements;

import com.chaosbuffalo.mkcore.serialization.ISerializableAttributeContainer;
import com.chaosbuffalo.mkcore.serialization.attributes.ISerializableAttribute;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

import java.util.List;

public class QuestRequirements implements ISerializableAttributeContainer {
    @Override
    public List<ISerializableAttribute<?>> getAttributes() {
        return null;
    }

    @Override
    public void addAttribute(ISerializableAttribute<?> iSerializableAttribute) {

    }

    @Override
    public void addAttributes(ISerializableAttribute<?>... iSerializableAttributes) {

    }

    public <D> D serialize(DynamicOps<D> ops){
        ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
        return ops.createMap(builder.build());
    }

    public <D> void deserialize(Dynamic<D> dynamic){

    }
}
