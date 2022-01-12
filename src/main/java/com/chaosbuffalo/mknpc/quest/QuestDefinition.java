package com.chaosbuffalo.mknpc.quest;

import com.chaosbuffalo.mkchat.dialogue.DialogueNode;
import com.chaosbuffalo.mkchat.dialogue.DialoguePrompt;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.quest.requirements.QuestRequirement;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class QuestDefinition {
    private ResourceLocation name;
    private final List<Quest> questChain;
    private final Map<String, Quest> questIndex;
    private boolean repeatable;
    private DialogueNode startQuestResponse;
    private DialogueNode startQuestHail;
    private DialoguePrompt hailPrompt;
    private ITextComponent questName;
    private static final ITextComponent defaultQuestName = new StringTextComponent("Default");
    private final List<QuestRequirement> requirements;


    public QuestDefinition(ResourceLocation name){
        this.name = name;
        this.questChain = new ArrayList<>();
        this.questIndex = new HashMap<>();
        this.requirements = new ArrayList<>();
        this.repeatable = false;
        this.questName = defaultQuestName;
    }

    public List<QuestRequirement> getRequirements() {
        return requirements;
    }

    public void addRequirement(QuestRequirement requirement){
        this.requirements.add(requirement);
    }

    public void setQuestName(ITextComponent questName) {
        this.questName = questName;
    }

    public ITextComponent getQuestName() {
        return questName;
    }

    public void setStartQuestResponse(DialogueNode startQuestResponse) {
        this.startQuestResponse = startQuestResponse;
    }

    public DialogueNode getStartQuestResponse() {
        return startQuestResponse;
    }

    public DialoguePrompt getHailPrompt(){
        return hailPrompt;
    }

    public void setHailPrompt(DialoguePrompt hailPrompt) {
        this.hailPrompt = hailPrompt;
    }

    public DialogueNode getStartQuestHail() {
        return startQuestHail;
    }

    public void setStartQuestHail(DialogueNode startQuestHail) {
        this.startQuestHail = startQuestHail;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public boolean isRepeatable() {
        return repeatable;
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


    public <D> D serialize(DynamicOps<D> ops){
        ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
        builder.put(ops.createString("quests"), ops.createList(questChain.stream().map(x -> x.serialize(ops))));
        builder.put(ops.createString("repeatable"), ops.createBoolean(isRepeatable()));
        builder.put(ops.createString("startQuestResponse"), startQuestResponse.serialize(ops));
        builder.put(ops.createString("hailQuestResponse"), startQuestHail.serialize(ops));
        builder.put(ops.createString("hailPrompt"), hailPrompt.serialize(ops));
        builder.put(ops.createString("questName"), ops.createString(ITextComponent.Serializer.toJson(questName)));
        builder.put(ops.createString("requirements"), ops.createList(requirements.stream().map(x -> x.serialize(ops))));
        return ops.createMap(builder.build());
    }

    public <D> void deserialize(Dynamic<D> dynamic){
        List<Quest> dQuests = dynamic.get("quests").asList(d -> {
            Quest q = new Quest();
            q.deserialize(d);
            return q;
        });
        questIndex.clear();
        questChain.clear();
        repeatable = dynamic.get("repeatable").asBoolean(false);
        startQuestResponse = new DialogueNode();
        dynamic.get("startQuestResponse").result().ifPresent(x -> startQuestResponse.deserialize(x));
        startQuestHail = new DialogueNode();
        dynamic.get("hailQuestResponse").result().ifPresent(x -> startQuestHail.deserialize(x));
        hailPrompt = new DialoguePrompt();
        dynamic.get("hailPrompt").result().ifPresent(x -> hailPrompt.deserialize(x));
        for (Quest quest : dQuests){
            addQuest(quest);
        }
        questName = ITextComponent.Serializer.getComponentFromJson(
                dynamic.get("questName").asString(ITextComponent.Serializer.toJson(defaultQuestName)));
        List<Optional<QuestRequirement>> reqs = dynamic.get("requirements").asList(x -> {
            ResourceLocation type = QuestRequirement.getType(x);
            Supplier<QuestRequirement> deserializer = QuestDefinitionManager.getRequirementDeserializer(type);
            if (deserializer == null){
                return Optional.empty();
            } else {
                QuestRequirement req = deserializer.get();
                req.deserialize(x);
                return Optional.of(req);
            }
        });
        reqs.forEach(x -> x.ifPresent(this::addRequirement));
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
