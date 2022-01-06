package com.chaosbuffalo.mknpc.quest;

import com.chaosbuffalo.mkchat.dialogue.DialogueTree;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IPlayerQuestingData;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.PlayerQuestingDataHandler;
import com.chaosbuffalo.mknpc.capabilities.WorldNpcDataHandler;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.quest.data.QuestChainData;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.UUIDInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import com.chaosbuffalo.mknpc.quest.objectives.StructureInstanceObjective;
import com.chaosbuffalo.mknpc.quest.objectives.TalkToNpcObjective;
import com.chaosbuffalo.mknpc.quest.requirements.QuestRequirements;
import com.chaosbuffalo.mknpc.quest.rewards.QuestReward;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Quest {

    private boolean autoComplete;
    private final List<QuestObjective<?>> objectives;
    private final Map<String, QuestObjective<?>> objectiveIndex;
    private final List<QuestReward> rewards;
    private final List<QuestRequirements> requirements;
    private String questName;

    public Quest(String questName){
        this.questName = questName;
        this.objectives = new ArrayList<>();
        this.objectiveIndex = new HashMap<>();
        this.rewards = new ArrayList<>();
        this.requirements = new ArrayList<>();
    }

    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    public boolean shouldAutoComplete(){
        return autoComplete;
    }

    public Quest(){
        this("default");
    }

    public String getQuestName() {
        return questName;
    }

    public void generateDialogueForNpc(QuestChainInstance questChain, ResourceLocation npcDefinitionName,
                                       UUID npcId, DialogueTree tree,
                                       Map<ResourceLocation, List<MKStructureEntry>> questStructures){
        QuestData questData = questChain.getQuestChainData().getQuestData(getQuestName());
        for (QuestObjective<?> obj : getObjectives()) {
            if (obj instanceof TalkToNpcObjective) {
                TalkToNpcObjective talkObj = (TalkToNpcObjective) obj;
                UUIDInstanceData instanceData = talkObj.getInstanceData(questData);
                if (instanceData.getUuid().equals(npcId)){
                    talkObj.generateDialogueForNpc(this, questChain, npcDefinitionName, npcId, tree, questStructures);
                }
            }
        }
    }

    public void addObjective(QuestObjective<?> objective){
        if (objectiveIndex.containsKey(objective.getObjectiveName())){
            MKNpc.LOGGER.error("Failed to add objective {} to quest {}", objective.getObjectiveName(), getQuestName());
        } else {
            objectives.add(objective);
            objectiveIndex.put(objective.getObjectiveName(), objective);
        }
    }

    public QuestObjective<?> getObjective(String name){
        return objectiveIndex.get(name);
    }

    public <D> D serialize(DynamicOps<D> ops){
        ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
        builder.put(ops.createString("questName"), ops.createString(questName));
        builder.put(ops.createString("objectives"), ops.createList(objectives.stream().map(x -> x.serialize(ops))));
        builder.put(ops.createString("autoComplete"), ops.createBoolean(autoComplete));
        return ops.createMap(builder.build());
    }

    public <D> void deserialize(Dynamic<D> dynamic){
        questName = dynamic.get("questName").asString("default");
        autoComplete = dynamic.get("autoComplete").asBoolean(false);
        List<Optional<QuestObjective<?>>> objectives = dynamic.get("objectives").asList(x -> {
            ResourceLocation type = QuestObjective.getType(x);
            Supplier<QuestObjective<?>> sup = QuestDefinitionManager.getObjectiveDeserializer(type);
            if (sup != null){
                QuestObjective<?> obj = sup.get();
                obj.deserialize(x);
                return Optional.of(obj);
            }
            return Optional.empty();
        });
        for (Optional<QuestObjective<?>> objOpt : objectives){
            objOpt.ifPresent(this::addObjective);
        }
    }

    public List<QuestObjective<?>> getObjectives() {
        return objectives;
    }

    public List<Pair<ResourceLocation, Integer>> getStructuresNeeded(){
        return objectives.stream().filter(x -> x instanceof StructureInstanceObjective)
                .map(x -> (StructureInstanceObjective<?>) x)
                .map(x -> new Pair<>(x.getStructureName(), x.getStructureIndex() + 1))
                .collect(Collectors.toList());
    }


    public boolean isStructureRelevant(MKStructureEntry entry){
        return objectives.stream().allMatch(x -> x.isStructureRelevant(entry));
    }

    public PlayerQuestData generatePlayerQuestData(IWorldNpcData worldData, QuestData instanceData){
        PlayerQuestData data = new PlayerQuestData(getQuestName());
        objectives.forEach(x -> {
            PlayerQuestObjectiveData obj = x.generatePlayerData(worldData, instanceData);
            obj.putBool("isComplete", false);
            data.putObjective(x.getObjectiveName(), obj);
        });
        return data;
    }

    public boolean isComplete(PlayerQuestData data){
        return objectives.stream().allMatch(x -> x.isComplete(data.getObjective(x.getObjectiveName())));
    }

    public void grantRewards(IPlayerQuestingData playerData){


    }
}
