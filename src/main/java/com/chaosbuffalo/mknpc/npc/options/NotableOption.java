package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.util.ResourceLocation;

public class NotableOption extends BooleanOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "notable");
    public NotableOption() {
        super(NAME, (definition, entity, value) -> MKNpc.getNpcData(entity).ifPresent(cap -> cap.setNotable(value)));
    }
}
