package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.util.ResourceLocation;

public class ExperienceOption extends IntOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "experience");

    public ExperienceOption(){
        super(NAME, ((definition, entity, integer) -> MKNpc.getNpcData(entity)
                .ifPresent(cap -> cap.setBonusXp(integer))));
    }
}
