package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.UUID;
import java.util.function.BiFunction;

public class SimpleOption<T> extends NpcDefinitionOption {
    private T value;
    private BiFunction<Gson, JsonObject, T> jsonDeserializer;
    private TriConsumer<NpcDefinition, Entity, T> entityApplicator;

    public SimpleOption(ResourceLocation name, BiFunction<Gson, JsonObject, T> jsonDeserializer,
                        TriConsumer<NpcDefinition, Entity, T> entityApplicator){
        super(name, ApplyOrder.MIDDLE);
        this.entityApplicator = entityApplicator;
        this.jsonDeserializer = jsonDeserializer;
    }


    public T getValue() {
        return value;
    }

    @Override
    public void fromJson(Gson gson, JsonObject object) {
        value = jsonDeserializer.apply(gson, object);
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity) {
        entityApplicator.accept(definition, entity, value);
    }
}
