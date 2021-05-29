package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public abstract class SimpleOption<T> extends NpcDefinitionOption {
    private T value;

    public SimpleOption(ResourceLocation name) {
        super(name, ApplyOrder.MIDDLE);
    }

    public abstract void applyToEntity(NpcDefinition definition, Entity entity, T value);

    public T getValue() {
        return value;
    }

    public SimpleOption<T> setValue(T value){
        this.value = value;
        return this;
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity) {
        applyToEntity(definition, entity, getValue());
    }
}
