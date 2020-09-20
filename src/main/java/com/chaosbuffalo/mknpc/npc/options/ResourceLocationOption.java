package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.util.TriConsumer;

public class ResourceLocationOption extends SimpleOption<ResourceLocation> {

    public ResourceLocationOption(ResourceLocation name, TriConsumer<NpcDefinition, Entity, ResourceLocation> entityApplicator) {
        super(name, (gson, object) -> new ResourceLocation(object.get(name.toString()).getAsString()),
                entityApplicator);
    }
}
