package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.Entity;

public class InvalidOption extends BooleanOption{

    public InvalidOption() {
        super(NpcDefinitionOption.INVALID_OPTION);
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity, Boolean value) {

    }
}
