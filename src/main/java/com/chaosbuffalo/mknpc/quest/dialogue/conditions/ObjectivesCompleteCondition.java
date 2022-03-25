package com.chaosbuffalo.mknpc.quest.dialogue.conditions;

import com.chaosbuffalo.mkchat.dialogue.conditions.DialogueCondition;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.IReceivesChainId;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

import java.util.*;

public class ObjectivesCompleteCondition extends DialogueCondition implements IReceivesChainId {
    public static final ResourceLocation conditionTypeName = new ResourceLocation(MKNpc.MODID, "objectives_complete");
    private final List<String> objectiveNames = new ArrayList<>();
    private String questName;
    private UUID chainId;


    public ObjectivesCompleteCondition(String questName, String... objectiveNames) {
        this(questName, Arrays.asList(objectiveNames));
    }

    public ObjectivesCompleteCondition(String questName, List<String> objectiveNames) {
        this(questName);
        this.objectiveNames.addAll(objectiveNames);
    }

    public ObjectivesCompleteCondition(String questName) {
        super(conditionTypeName);
        this.chainId = Util.DUMMY_UUID;
        this.questName = questName;
    }

    public ObjectivesCompleteCondition() {
        this("default");
    }

    @Override
    public void setChainId(UUID chainId) {
        this.chainId = chainId;
    }

    @Override
    public boolean meetsCondition(ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity) {
        return MKNpc.getPlayerQuestData(serverPlayerEntity).map(questLog -> {
            Optional<PlayerQuestChainInstance> chainInstance = questLog.getQuestChain(chainId);
            if (chainInstance.isPresent()) {
                PlayerQuestChainInstance chain = chainInstance.get();
                return objectiveNames.stream().allMatch(name -> {
                    PlayerQuestData questData = chain.getQuestData(questName);
                    if (questData == null) {
                        return false;
                    }
                    PlayerQuestObjectiveData pObj = questData.getObjective(name);
                    return pObj != null && pObj.isComplete();
                });
            }
            return false;
        }).orElse(false);
    }

    @Override
    public ObjectivesCompleteCondition copy() {
        return new ObjectivesCompleteCondition(questName, objectiveNames);
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("objectiveNames"), ops.createList(objectiveNames.stream().map(ops::createString)));
        if (!chainId.equals(Util.DUMMY_UUID)) {
            builder.put(ops.createString("chainId"), ops.createString(chainId.toString()));
        }
        builder.put(ops.createString("questName"), ops.createString(questName));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);

        objectiveNames.clear();
        dynamic.get("objectiveNames").asStream()
                .map(x -> x.asString().result().orElseThrow(() -> new IllegalStateException("Failed to parse objective name from: " + x)))
                .forEach(objectiveNames::add);

        questName = dynamic.get("questName").asString("default");
        chainId = dynamic.get("chainId").asString().result().map(UUID::fromString).orElse(Util.DUMMY_UUID);
    }
}
