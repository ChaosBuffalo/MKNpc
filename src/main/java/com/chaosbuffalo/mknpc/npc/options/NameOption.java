package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class NameOption extends StringOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "name");
    public NameOption() {
        super(NAME, (definition, entity, name) -> {
            if (!name.equals("")) {
                entity.setCustomName(new StringTextComponent(name));
            }
        });
    }
}
