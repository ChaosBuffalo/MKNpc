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

public class OnQuestChainCondition extends DialogueCondition {

    public static final ResourceLocation conditionTypeName = new ResourceLocation(MKNpc.MODID, "on_quest_chain_condition");
    private UUID questId;
    private boolean allowRepeat;

    public OnQuestChainCondition(UUID questId, boolean allowRepeat){
        super(conditionTypeName);
        this.questId = questId;
        this.allowRepeat = allowRepeat;
    }

    public OnQuestChainCondition(){
        this(UUID.randomUUID(), false);
    }

    @Override
    public boolean meetsCondition(ServerPlayerEntity player, LivingEntity source) {
        return MKNpc.getPlayerQuestData(player).map(x -> x.isOnQuest(questId, allowRepeat)).orElse(false);
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        this.questId = UUID.fromString(dynamic.get("questId").asString(questId.toString()));
        allowRepeat = dynamic.get("allowRepeat").asBoolean(false);
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("questId"), ops.createString(questId.toString()));
        builder.put(ops.createString("allowRepeat"), ops.createBoolean(allowRepeat));
    }
}