package com.chaosbuffalo.mknpc.quest;

import com.chaosbuffalo.mkchat.dialogue.DialogueTree;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IPlayerQuestingData;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.UUIDInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestReward;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import com.chaosbuffalo.mknpc.quest.objectives.StructureInstanceObjective;
import com.chaosbuffalo.mknpc.quest.objectives.TalkToNpcObjective;
import com.chaosbuffalo.mknpc.quest.requirements.QuestRequirement;
import com.chaosbuffalo.mknpc.quest.rewards.QuestReward;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.*;
import java.util.stream.Collectors;

public class Quest {

    private boolean autoComplete;
    private final List<QuestObjective<?>> objectives;
    private final Map<String, QuestObjective<?>> objectiveIndex;
    private final List<QuestReward> rewards;
    private final List<QuestRequirement> requirements;
    private String questName;
    private IFormattableTextComponent description;
    public static final IFormattableTextComponent defaultDescription = new StringTextComponent("Placeholder Quest Description");

    public Quest(String questName, IFormattableTextComponent description) {
        this();
        this.questName = questName;
        this.description = description;
    }

    public Quest(Dynamic<?> dynamic) {
        this();
        deserialize(dynamic);
    }

    private Quest() {
        this.objectives = new ArrayList<>();
        this.objectiveIndex = new HashMap<>();
        this.rewards = new ArrayList<>();
        this.requirements = new ArrayList<>();
    }

    public String getQuestName() {
        return questName;
    }

    public IFormattableTextComponent getDescription() {
        return description;
    }

    public void addObjective(QuestObjective<?> objective) {
        if (objectiveIndex.containsKey(objective.getObjectiveName())) {
            MKNpc.LOGGER.error("Failed to add objective {} to quest {}", objective.getObjectiveName(), getQuestName());
        } else {
            objectives.add(objective);
            objectiveIndex.put(objective.getObjectiveName(), objective);
        }
    }

    public QuestObjective<?> getObjective(String name) {
        return objectiveIndex.get(name);
    }

    public void addReward(QuestReward reward) {
        rewards.add(reward);
    }

    public void setAutoComplete(boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    public boolean shouldAutoComplete() {
        return autoComplete;
    }

    public DialogueTree generateDialogueForNpc(QuestChainInstance questChain, ResourceLocation npcDefinitionName,
                                               UUID npcId, DialogueTree tree,
                                               Map<ResourceLocation, List<MKStructureEntry>> questStructures,
                                               QuestDefinition definition) {
        QuestData questData = questChain.getQuestChainData().getQuestData(this);
        for (QuestObjective<?> obj : getObjectives()) {
            if (obj instanceof TalkToNpcObjective) {
                TalkToNpcObjective talkObj = (TalkToNpcObjective) obj;
                UUIDInstanceData instanceData = questData.getObjective(talkObj);
                if (instanceData.getUUID().equals(npcId)) {
                    tree = talkObj.generateDialogueForNpc(this, questChain, npcDefinitionName, npcId, tree, questStructures, definition);
                }
            }
        }
        return tree;
    }

    public <D> D serialize(DynamicOps<D> ops) {
        ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
        builder.put(ops.createString("questName"), ops.createString(questName));
        builder.put(ops.createString("objectives"), ops.createList(objectives.stream().map(x -> x.serialize(ops))));
        builder.put(ops.createString("description"), ops.createString(ITextComponent.Serializer.toJson(description)));
        builder.put(ops.createString("autoComplete"), ops.createBoolean(autoComplete));
        builder.put(ops.createString("rewards"), ops.createList(rewards.stream().map(x -> x.serialize(ops))));
        return ops.createMap(builder.build());
    }

    public <D> void deserialize(Dynamic<D> dynamic) {
        questName = dynamic.get("questName").asString("default");
        autoComplete = dynamic.get("autoComplete").asBoolean(false);
        description = dynamic.get("description").asString().result()
                .map(ITextComponent.Serializer::getComponentFromJson)
                .orElse(defaultDescription);

        dynamic.get("objectives").asStream().forEach(entry -> {
            QuestObjective<?> objective = QuestObjective.getType(entry)
                    .flatMap(QuestDefinitionManager::getObjectiveDeserializer)
                    .map(f -> f.apply(entry))
                    .orElseThrow(() -> new IllegalStateException(String.format(Locale.ENGLISH, "Failed to parse quest " +
                            "objective type from: %s", entry)));

            addObjective(objective);
        });

        dynamic.get("rewards").asStream().forEach(entry -> {
            QuestReward objective = QuestReward.getType(entry)
                    .flatMap(QuestDefinitionManager::getRewardDeserializer)
                    .map(f -> f.apply(entry))
                    .orElseThrow(() -> new IllegalStateException(String.format(Locale.ENGLISH, "Failed to parse quest " +
                            "reward type from: %s", entry)));

            addReward(objective);
        });
    }

    public List<QuestObjective<?>> getObjectives() {
        return objectives;
    }

    public List<Pair<ResourceLocation, Integer>> getStructuresNeeded() {
        return objectives.stream().filter(x -> x instanceof StructureInstanceObjective)
                .map(x -> (StructureInstanceObjective<?>) x)
                .map(x -> new Pair<>(x.getStructureName(), x.getStructureIndex() + 1))
                .collect(Collectors.toList());
    }


    public boolean isStructureRelevant(MKStructureEntry entry) {
        return objectives.stream().allMatch(x -> x.isStructureRelevant(entry));
    }

    public PlayerQuestData generatePlayerQuestData(IWorldNpcData worldData, QuestData instanceData) {
        PlayerQuestData data = new PlayerQuestData(getQuestName(), getDescription());
        objectives.forEach(x -> {
            PlayerQuestObjectiveData obj = x.generatePlayerData(worldData, instanceData);
            obj.putBool("isComplete", false);
            data.putObjective(x.getObjectiveName(), obj);
        });
        rewards.forEach(x -> {
            PlayerQuestReward questReward = new PlayerQuestReward(x);
            data.addReward(questReward);
        });
        return data;
    }

    public boolean isComplete(PlayerQuestData data) {
        return objectives.stream().allMatch(x -> x.isComplete(data.getObjective(x.getObjectiveName())));
    }

    public void grantRewards(IPlayerQuestingData playerData) {
        for (QuestReward reward : rewards) {
            reward.grantReward(playerData.getPlayer());
        }
    }
}
