package com.chaosbuffalo.mknpc.quest;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.WorldNpcDataHandler;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import com.chaosbuffalo.mknpc.quest.objectives.StructureInstanceObjective;
import com.chaosbuffalo.mknpc.quest.requirements.QuestRequirements;
import com.chaosbuffalo.mknpc.quest.rewards.QuestReward;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public String getQuestName() {
        return questName;
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
        return ops.createMap(builder.build());
    }

    public <D> void deserialize(Dynamic<D> dynamic){

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

    public PlayerQuestData generatePlayerQuestData(WorldNpcDataHandler worldData, QuestData instanceData){
        PlayerQuestData data = new PlayerQuestData();
        objectives.forEach(x -> {
            PlayerQuestObjectiveData obj = x.generatePlayerData(worldData, instanceData);
            data.putObjective(x.getObjectiveName(), obj);
        });
        return data;
    }

    public boolean isComplete(PlayerQuestData data){
        return objectives.stream().allMatch(x -> x.isComplete(data.getObjective(x.getObjectiveName())));
    }
}
