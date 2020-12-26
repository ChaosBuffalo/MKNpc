package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;

public class BooleanOption extends SimpleOption<Boolean> {
    public BooleanOption(ResourceLocation name, TriConsumer<NpcDefinition, Entity, Boolean> entityApplicator) {
        super(name, (gson, object) -> object.get(name.toString()).getAsBoolean(), entityApplicator);
    }
}
