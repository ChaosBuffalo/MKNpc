package com.chaosbuffalo.mknpc.quest.dialogue.conditions;

import com.chaosbuffalo.mkchat.dialogue.conditions.DialogueCondition;
import com.chaosbuffalo.mknpc.MKNpc;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class OnQuestCondition extends DialogueCondition {

    public static final ResourceLocation conditionTypeName = new ResourceLocation(MKNpc.MODID, "on_quest_condition");
    private UUID questId;
    private String questStep;
    private boolean allowRepeat;

    public OnQuestCondition(UUID questId, String questStep, boolean allowRepeat){
        super(conditionTypeName);
        this.questId = questId;
        this.questStep = questStep;
        this.allowRepeat = allowRepeat;
    }

    public OnQuestCondition(){
        this(UUID.randomUUID(), "invalid", false);
    }

    @Override
    public boolean meetsCondition(ServerPlayerEntity player, LivingEntity source) {
        return MKNpc.getPlayerQuestData(player).map(
                x -> x.isOnQuest(questId, allowRepeat) && x.getCurrentQuestStep(questId).orElse("invalid").equals(questStep))
                .orElse(false);
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("questId"), ops.createString(questId.toString()));
        builder.put(ops.createString("questStep"), ops.createString(questStep));
        builder.put(ops.createString("allowRepeat"), ops.createBoolean(allowRepeat));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        this.questId = UUID.fromString(dynamic.get("questId").asString(questId.toString()));
        this.questStep = dynamic.get("questStep").asString("invalid");
        allowRepeat = dynamic.get("allowRepeat").asBoolean(false);
    }

}
