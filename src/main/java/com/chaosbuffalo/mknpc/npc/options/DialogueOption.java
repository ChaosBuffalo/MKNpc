package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mkchat.capabilities.ChatCapabilities;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.util.ResourceLocation;

public class DialogueOption  extends ResourceLocationOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "dialogue");

    public DialogueOption() {
        super(NAME, (definition, entity, resourceLocation) -> {
            entity.getCapability(ChatCapabilities.NPC_DIALOGUE_CAPABILITY).ifPresent(cap -> {
                cap.setDialogueTree(resourceLocation);
            });
        });
    }
}
