package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class NotableOption extends BooleanOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "notable");
    public NotableOption() {
        super(NAME);
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity, Boolean value) {
        MKNpc.getNpcData(entity).ifPresent(cap -> cap.setNotable(value));
    }
}
