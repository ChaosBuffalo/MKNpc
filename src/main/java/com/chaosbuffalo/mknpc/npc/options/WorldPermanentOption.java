package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public abstract class WorldPermanentOption extends NpcDefinitionOption {
    public WorldPermanentOption(ResourceLocation name) {
        super(name);
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity) {
        MKNpc.getWorldNpcData(entity.getEntityWorld()).ifPresent((worldCap) -> {
            if (!worldCap.hasEntityOptionEntry(definition, this, entity)){
                generateWorldEntry(definition, entity, worldCap);
            }
            applyFromWorld(definition, entity, worldCap);
        });
    }

    protected void applyFromWorld(NpcDefinition definition, Entity entity, IWorldNpcData worldData){
        worldData.getEntityOptionEntry(definition, this, entity).applyToEntity(entity);
    }

    protected abstract INpcOptionEntry makeOptionEntry(NpcDefinition definition, Entity entity);

    protected void generateWorldEntry(NpcDefinition definition, Entity entity, IWorldNpcData worldNpcData){
        worldNpcData.addEntityOptionEntry(definition, this, entity, makeOptionEntry(definition, entity));
    }

}
