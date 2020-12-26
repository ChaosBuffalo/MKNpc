package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.UUID;
import java.util.function.BiFunction;

public abstract class SimpleOption<T> extends NpcDefinitionOption {
    private T value;

    public SimpleOption(ResourceLocation name) {
        super(name, ApplyOrder.MIDDLE);
    }

    public abstract void applyToEntity(NpcDefinition definition, Entity entity, T value);

    public T getValue() {
        return value;
    }

    @Override
    public void fromJson(Gson gson, JsonObject object) {
//        value = jsonDeserializer.apply(gson, object);
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
