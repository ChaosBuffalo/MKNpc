package com.chaosbuffalo.mknpc.quest;

import com.chaosbuffalo.mkchat.dialogue.DialogueTree;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IPlayerQuestingData;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.quest.data.QuestChainData;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.UUIDInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import com.chaosbuffalo.mknpc.quest.objectives.TalkToNpcObjective;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class QuestChainInstance implements INBTSerializable<CompoundNBT> {

    private UUID questId;
    private QuestDefinition definition;
    private QuestChainData questChainData;
    private Map<UUID, DialogueTree> dialogueTrees;
    private UUID questSourceNpc;

    public QuestChainInstance(QuestDefinition definition, Map<ResourceLocation, List<MKStructureEntry>> questStructures){
        questId = UUID.randomUUID();
        this.definition = definition;
        questChainData = new QuestChainData(definition, questStructures);
        dialogueTrees = questChainData.generateDialogue(this, getDialogueTreeName(), definition, questStructures, getSpeakingRoles());
    }

    public void setQuestSourceNpc(UUID questSourceNpc) {
        this.questSourceNpc = questSourceNpc;
    }

    public Map<ResourceLocation, UUID> getSpeakingRoles(){
        Map<ResourceLocation, UUID> speakingRoles = new HashMap<>();
        for (Quest quest : definition.getQuestChain()){
            QuestData questData = questChainData.getQuestData(quest.getQuestName());
            for (QuestObjective<?> obj : quest.getObjectives()){
                if (obj instanceof TalkToNpcObjective){
                    TalkToNpcObjective talkObj = (TalkToNpcObjective) obj;
                    UUIDInstanceData instanceData = talkObj.getInstanceData(questData);
                    if (speakingRoles.containsKey(talkObj.getNpcDefinition())){
                        MKNpc.LOGGER.warn("Error: quest chain has 2 different npc definition with speaking roles {}", this);
                    }
                    speakingRoles.put(talkObj.getNpcDefinition(), instanceData.getUuid());
                }
            }
        }
        return speakingRoles;
    }

    public QuestChainInstance(CompoundNBT nbt){
        deserializeNBT(nbt);
    }

    public UUID getQuestId() {
        return questId;
    }

    public String getStartingQuestName(){
        return definition.getFirstQuest().getQuestName();
    }


    public Optional<DialogueTree> getTreeForEntity(Entity entity){
        return MKNpc.getNpcData(entity).map(x -> {
            UUID entityId = x.getSpawnID();
            return Optional.ofNullable(dialogueTrees.get(entityId));
        }).orElse(Optional.empty());
    }

    public Optional<Quest> getNextQuest(String currentQuest){
        Quest current = definition.getQuest(currentQuest);
        int currentIndex = definition.getQuestChain().indexOf(current);
        if (definition.getQuestChain().size() > currentIndex + 1){
            return Optional.of(definition.getQuestChain().get(currentIndex + 1));
        } else {
            return Optional.empty();
        }
    }

    public QuestChainData getQuestChainData() {
        return questChainData;
    }

    public QuestDefinition getDefinition() {
        return definition;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("questId", questId);
        nbt.putString("definitionId", definition.getName().toString());
        nbt.put("questData", questChainData.serializeNBT());
        if (questSourceNpc != null){
            nbt.putUniqueId("questSource", questSourceNpc);
        }
//        nbt.put("dialogueTree", dialogueTree.serialize(NBTDynamicOps.INSTANCE));
        return nbt;
    }

    protected ResourceLocation getDialogueTreeName(){
        return new ResourceLocation(MKNpc.MODID, String.format("quest_dialogue.%s", questId.toString()));
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        questId = nbt.getUniqueId("questId");
        definition = QuestDefinitionManager.getDefinition(new ResourceLocation(nbt.getString("definitionId")));
        questChainData = new QuestChainData(definition, nbt.getCompound("questData"));
        if (nbt.contains("questSource")){
            questSourceNpc = nbt.getUniqueId("questSource");
        }
//        dialogueTree = new DialogueTree(getDialogueTreeName());
//        dialogueTree.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt.get("dialogueTree")));
    }

    public void signalQuestProgress(IWorldNpcData worldData, IPlayerQuestingData questingData, Quest currentQuest, PlayerQuestChainInstance playerInstance, boolean manualAdvance){
        PlayerQuestData playerData = playerInstance.getQuestData(currentQuest.getQuestName());
        questingData.questProgression(worldData, worldData.getQuest(playerInstance.getQuestId()));
        if (currentQuest.isComplete(playerData) || manualAdvance){
            if (currentQuest.shouldAutoComplete() || manualAdvance){
                questingData.advanceQuest(worldData, worldData.getQuest(playerInstance.getQuestId()));
            }
        }
    }
}
