package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mkchat.dialogue.*;
import com.chaosbuffalo.mkchat.dialogue.conditions.DialogueCondition;
import com.chaosbuffalo.mkchat.dialogue.effects.DialogueEffect;
import com.chaosbuffalo.mkcore.serialization.attributes.ResourceLocationAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.npc.NotableNpcEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.QuestDefinition;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.UUIDInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mknpc.quest.dialogue.NpcDialogueUtils;
import com.chaosbuffalo.mknpc.quest.dialogue.conditions.OnQuestCondition;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.IReceivesChainId;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;

import java.util.*;

public class TalkToNpcObjective extends StructureInstanceObjective<UUIDInstanceData> {

    public static class HailEntry {
        private DialogueNode node;
        private DialogueResponse response;

        public HailEntry(DialogueNode node, DialogueResponse response){
            this.node = node;
            this.response = response;
        }

        public <D> HailEntry(Dynamic<D> dynamic){
            deserialize(dynamic);
        }

        public <D> D serialize(DynamicOps<D> ops){
            ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
            builder.put(ops.createString("node"), node.serialize(ops));
            builder.put(ops.createString("response"), response.serialize(ops));
            return ops.createMap(builder.build());
        }

        public <D> void deserialize(Dynamic<D> dynamic) {
            node = DialogueNode.fromDynamicField(dynamic.get("node"));
            response = DialogueResponse.fromDynamicField(dynamic.get("response"));
        }
    }

    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "objective.talk_to_npc");
    protected ResourceLocationAttribute npcDefinition = new ResourceLocationAttribute("npcDefinition", NpcDefinitionManager.INVALID_NPC_DEF);
    protected List<HailEntry> hailResponses = new ArrayList<>();
    protected List<DialogueNode> additionalNodes = new ArrayList<>();
    protected List<DialoguePrompt> additionalPrompts = new ArrayList<>();

    public TalkToNpcObjective(String name, ResourceLocation structure, int index, ResourceLocation npcDefinition, IFormattableTextComponent... description){
        super(NAME, name, structure, index, description);
        addAttribute(this.npcDefinition);
        this.npcDefinition.setValue(npcDefinition);
    }

    public TalkToNpcObjective(){
        super(NAME, "invalid", defaultDescription);
        addAttribute(this.npcDefinition);
    }

    public ResourceLocation getNpcDefinition(){
        return npcDefinition.getValue();
    }

    @Override
    public UUIDInstanceData generateInstanceData(Map<ResourceLocation, List<MKStructureEntry>> questStructures) {
        MKStructureEntry entry = questStructures.get(getStructureName()).get(structureIndex.value());
        Optional<NotableNpcEntry> chest = entry.getFirstNotableOfType(npcDefinition.getValue());
        return chest.map(x -> new UUIDInstanceData(x.getSpawnerId())).orElse(new UUIDInstanceData());
    }

    public TalkToNpcObjective withHailResponse(DialogueNode hailNode, DialogueResponse hailResponse){
        this.hailResponses.add(new HailEntry(hailNode, hailResponse));
        return this;
    }

    public TalkToNpcObjective withAdditionalNode(DialogueNode node){
        this.additionalNodes.add(node);
        return this;
    }

    public TalkToNpcObjective withAdditionalPrompts(DialoguePrompt prompt){
        this.additionalPrompts.add(prompt);
        return this;
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("nodes"), ops.createList(additionalNodes.stream().map(x -> x.serialize(ops))));
        builder.put(ops.createString("prompts"), ops.createList(additionalPrompts.stream().map(x -> x.serialize(ops))));
        builder.put(ops.createString("hailResponses"), ops.createList(hailResponses.stream().map(x -> x.serialize(ops))));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        additionalNodes.clear();
        dynamic.get("nodes").asList(DialogueNode::fromDynamic)
                .forEach(dr -> dr.resultOrPartial(DialogueUtils::throwParseException).ifPresent(additionalNodes::add));

        additionalPrompts.clear();
        dynamic.get("prompts").asList(DialoguePrompt::fromDynamic)
                .forEach(dr -> dr.resultOrPartial(DialogueUtils::throwParseException).ifPresent(additionalPrompts::add));
        List<HailEntry> hailResponses = dynamic.get("hailResponses").asList(HailEntry::new);
        this.hailResponses.clear();
        this.hailResponses.addAll(hailResponses);
    }

    private DialogueNode copyNodeAndSetUUID(DialogueNode node, UUID questId,
                                            Map<ResourceLocation, List<MKStructureEntry>> questStructures,
                                            QuestChainInstance questChain){
        DialogueNode newNode = node.copy();
        for (DialogueEffect effect : newNode.getEffects()){
            if (effect instanceof IReceivesChainId){
                IReceivesChainId advEffect = (IReceivesChainId) effect;
                advEffect.setChainId(questId);
            }
        }
        handleQuestRawMessageManipulation(newNode, questStructures, questChain);
        return newNode;
    }

    private DialogueResponse copyResponseAndAddQuestCondition(DialogueResponse response, UUID questId, String questName){
        DialogueResponse hrResponse = response.copy();
        hrResponse.addCondition(new OnQuestCondition(questId, questName));
        for (DialogueCondition condition : hrResponse.getConditions()){
            if (condition instanceof IReceivesChainId){
                ((IReceivesChainId) condition).setChainId(questId);
            }
        }
        return hrResponse;
    }

    private void handleQuestRawMessageManipulation(DialogueObject dialogueObj,
                                                   Map<ResourceLocation, List<MKStructureEntry>> questStructures,
                                                   QuestChainInstance questChain){
        String rawMsg = dialogueObj.getRawMessage();
        String newMsg = NpcDialogueUtils.parseQuestDialogueMessage(rawMsg, questStructures, questChain);
        dialogueObj.setRawMessage(newMsg);
    }

    public void generateDialogueForNpc(Quest quest, QuestChainInstance questChain, ResourceLocation npcDefinitionName,
                                       UUID npcId, DialogueTree tree,
                                       Map<ResourceLocation, List<MKStructureEntry>> questStructures,
                                       QuestDefinition definition){
        DialoguePrompt hailPrompt = tree.getHailPrompt();
        for (HailEntry entry : hailResponses){
            DialogueNode hrCopy = copyNodeAndSetUUID(entry.node, questChain.getQuestId(), questStructures, questChain);
            DialogueResponse hrResponse = copyResponseAndAddQuestCondition(entry.response, questChain.getQuestId(), quest.getQuestName());
            tree.addNode(hrCopy);
            if (hailPrompt != null){
                hailPrompt.addResponse(hrResponse);
            }
        }
        for (DialogueNode node : additionalNodes){
            DialogueNode newNode = copyNodeAndSetUUID(node, questChain.getQuestId(), questStructures, questChain);

            tree.addNode(newNode);
        }
        for (DialoguePrompt prompt : additionalPrompts){
            DialoguePrompt copyPrompt = prompt.copy();
            for (DialogueResponse resp : copyPrompt.getResponses()){
                resp.addCondition(new OnQuestCondition(questChain.getQuestId(), quest.getQuestName()));
            }
            tree.addPrompt(copyPrompt);
        }


    }

    @Override
    public UUIDInstanceData instanceDataFactory() {
        return new UUIDInstanceData();
    }

    @Override
    public boolean isStructureRelevant(MKStructureEntry entry) {
        return structureName.getValue().equals(entry.getStructureName()) && entry.hasNotableOfType(npcDefinition.getValue());
    }

    @Override
    public PlayerQuestObjectiveData generatePlayerData(IWorldNpcData worldData, QuestData questData) {
        UUIDInstanceData objData = getInstanceData(questData);
        PlayerQuestObjectiveData newObj = playerDataFactory();
        newObj.putBlockPos("npcPos", worldData.getNotableNpc(objData.getUuid()).getLocation());
        newObj.putBool("hasSpoken", false);
        return newObj;
    }

    @Override
    public PlayerQuestObjectiveData playerDataFactory() {
        return new PlayerQuestObjectiveData(getObjectiveName(), getDescription());
    }

    @Override
    public void signalCompleted(PlayerQuestObjectiveData objectiveData) {
        super.signalCompleted(objectiveData);
        objectiveData.putBool("hasSpoken", true);

    }
}
