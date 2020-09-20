package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;

public class StringOption extends SimpleOption<String> {
    public StringOption(ResourceLocation name, TriConsumer<NpcDefinition, Entity, String> entityApplicator) {
        super(name, (gson, object) -> object.get(name.toString()).getAsString(), entityApplicator);
    }
}
