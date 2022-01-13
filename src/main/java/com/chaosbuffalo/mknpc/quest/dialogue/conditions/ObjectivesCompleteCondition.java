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

import java.util.*;
import java.util.stream.Collectors;

public class ObjectivesCompleteCondition extends DialogueCondition implements IReceivesChainId {
    public static final ResourceLocation conditionTypeName = new ResourceLocation(MKNpc.MODID, "objectives_complete");
    private final List<String> objectiveNames = new ArrayList<>();
    private String questName;
    private UUID chainId;


    public ObjectivesCompleteCondition(String questName, String... objectiveNames){
        super(conditionTypeName);
        this.objectiveNames.addAll(Arrays.asList(objectiveNames));
        this.chainId = UUID.randomUUID();
        this.questName = questName;
    }

    public ObjectivesCompleteCondition(){
        this("default");
    }

    @Override
    public boolean meetsCondition(ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity) {
        return MKNpc.getPlayerQuestData(serverPlayerEntity).map(x -> {
            Optional<PlayerQuestChainInstance> chainInstance = x.getQuestChain(chainId);
            if (chainInstance.isPresent()){
                PlayerQuestChainInstance chain = chainInstance.get();
                return objectiveNames.stream().allMatch(name -> {
                    PlayerQuestData questData = chain.getQuestData(questName);
                    if (questData == null){
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
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("objectiveNames"), ops.createList(objectiveNames.stream().map(ops::createString)));
        builder.put(ops.createString("chainId"), ops.createString(chainId.toString()));
        builder.put(ops.createString("questName"), ops.createString(questName));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        this.objectiveNames.addAll(dynamic.get("objectiveNames").asList(x -> x.asString().result()).stream()
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
        questName = dynamic.get("questName").asString("default");
        chainId = dynamic.get("chainId").asString().result().map(UUID::fromString).orElse(UUID.randomUUID());
    }

    @Override
    public void setChainId(UUID chainId) {
        this.chainId = chainId;
    }
}
