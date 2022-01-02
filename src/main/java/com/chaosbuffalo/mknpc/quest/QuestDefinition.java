package com.chaosbuffalo.mknpc.quest;

import com.chaosbuffalo.mkcore.serialization.ISerializableAttributeContainer;
import com.chaosbuffalo.mkcore.serialization.attributes.ISerializableAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class QuestDefinition implements ISerializableAttributeContainer {
    private ResourceLocation name;
    private final List<Quest> questChain;
    private final Map<String, Quest> questIndex;


    public QuestDefinition(ResourceLocation name){
        this.name = name;
        this.questChain = new ArrayList<>();
        this.questIndex = new HashMap<>();
    }

    @Override
    public List<ISerializableAttribute<?>> getAttributes() {
        return null;
    }

    @Override
    public void addAttribute(ISerializableAttribute<?> iSerializableAttribute) {

    }

    public Quest getFirstQuest(){
        return questChain.get(0);
    }

    public void addQuest(Quest quest){
        if (questIndex.containsKey(quest.getQuestName())){
            MKNpc.LOGGER.error("Trying to add quest with existing quest name {} to quest definition: {}", quest.getQuestName(), name.toString());
        } else {
            questChain.add(quest);
            questIndex.put(quest.getQuestName(), quest);
        }
    }

    @Nullable
    public Quest getQuest(String name){
        return questIndex.get(name);
    }

    public List<Quest> getQuestChain() {
        return questChain;
    }

    public ResourceLocation getName() {
        return name;
    }

    @Override
    public void addAttributes(ISerializableAttribute<?>... iSerializableAttributes) {

    }

    public <D> D serialize(DynamicOps<D> ops){
        ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
        return ops.createMap(builder.build());
    }

    public <D> void deserialize(Dynamic<D> dynamic){

    }

    public Map<ResourceLocation, Integer> getStructuresNeeded(){

        List<Pair<ResourceLocation, Integer>> allObjectives = questChain
                .stream()
                .map(Quest::getStructuresNeeded)
                .flatMap(Collection::stream).collect(Collectors.toList());

        Map<ResourceLocation, Integer> finals = new HashMap<>();
        for (Pair<ResourceLocation, Integer> pair : allObjectives){
            if (!finals.containsKey(pair.getFirst()) || finals.get(pair.getFirst()) < pair.getSecond()){
                finals.put(pair.getFirst(), pair.getSecond());
            }
        }
        return finals;
    }

    public boolean doesStructureMeetRequirements(MKStructureEntry entry){
        return questChain.stream().allMatch(x -> x.isStructureRelevant(entry));
    }

    public QuestChainInstance generate(Map<ResourceLocation, List<MKStructureEntry>> questStructures){
        QuestChainInstance instance = new QuestChainInstance(this, questStructures);
        return instance;
    }


}
