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

    public OnQuestCondition(UUID questId, String questStep){
        super(conditionTypeName);
        this.questId = questId;
        this.questStep = questStep;
    }

    public OnQuestCondition(){
        this(UUID.randomUUID(), "invalid");
    }

    @Override
    public boolean meetsCondition(ServerPlayerEntity player, LivingEntity source) {
        return MKNpc.getPlayerQuestData(player).map(
                x -> x.isOnQuest(questId) && x.getCurrentQuestStep(questId).orElse("invalid").equals(questStep))
                .orElse(false);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        super.deserialize(dynamic);
        this.questId = UUID.fromString(dynamic.get("questId").asString(questId.toString()));
        this.questStep = dynamic.get("questStep").asString("invalid");
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D ret = super.serialize(ops);
        return ops.mergeToMap(ret, ImmutableMap.of(
                ops.createString("questId"), ops.createString(questId.toString()),
                ops.createString("questStep"), ops.createString(questStep)
        )).result().orElse(ret);
    }
}
