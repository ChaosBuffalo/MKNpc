package com.chaosbuffalo.mknpc.quest.dialogue.conditions;

import com.chaosbuffalo.mkchat.dialogue.conditions.DialogueCondition;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class PendingGenerationCondition extends DialogueCondition {

    public static final ResourceLocation conditionTypeName = new ResourceLocation(MKNpc.MODID, "pendering_generation");

    public PendingGenerationCondition(){
        super(conditionTypeName);
    }


    @Override
    public boolean meetsCondition(ServerPlayerEntity player, LivingEntity source) {
        return source.getCapability(NpcCapabilities.ENTITY_NPC_DATA_CAPABILITY).map(
                x -> x.shouldHaveQuest() && !x.hasGeneratedQuest()).orElse(false);
    }
}
