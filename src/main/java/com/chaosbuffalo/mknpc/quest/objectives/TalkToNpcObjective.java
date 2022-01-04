package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mkchat.dialogue.DialogueNode;
import com.chaosbuffalo.mkchat.dialogue.DialoguePrompt;
import com.chaosbuffalo.mkchat.dialogue.DialogueResponse;
import com.chaosbuffalo.mkchat.dialogue.DialogueTree;
import com.chaosbuffalo.mkchat.dialogue.effects.DialogueEffect;
import com.chaosbuffalo.mkcore.serialization.attributes.ResourceLocationAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.npc.NotableNpcEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.UUIDInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mknpc.quest.dialogue.conditions.OnQuestCondition;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.AdvanceQuestChainEffect;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.*;

public class TalkToNpcObjective extends StructureInstanceObjective<UUIDInstanceData> {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "objective.talk_to_npc");
    protected ResourceLocationAttribute npcDefinition = new ResourceLocationAttribute("npcDefinition", NpcDefinitionManager.INVALID_NPC_DEF);
    protected DialogueNode hailResponse;
    protected List<DialogueNode> additionalNodes = new ArrayList<>();
    protected List<DialoguePrompt> additionalPrompts= new ArrayList<>();

    public TalkToNpcObjective(String name, ResourceLocation structure, int index, ResourceLocation npcDefinition, ITextComponent description){
        super(NAME, name, structure, index, description);
        addAttribute(this.npcDefinition);
        this.npcDefinition.setValue(npcDefinition);
    }

    public TalkToNpcObjective(){
        super(NAME, "invalid", new StringTextComponent("placeholder"));
        addAttribute(this.npcDefinition);
    }

    public ResourceLocation getNpcDefinition(){
        return npcDefinition.getValue();
    }

    @Override
    public UUIDInstanceData generateInstanceData(Map<ResourceLocation, List<MKStructureEntry>> questStructures) {
        MKStructureEntry entry = questStructures.get(getStructureName()).get(structureIndex.getValue());
        Optional<NotableNpcEntry> chest = entry.getFirstNotableOfType(npcDefinition.getValue());
        return chest.map(x -> new UUIDInstanceData(x.getSpawnerId())).orElse(new UUIDInstanceData());
    }

    public TalkToNpcObjective withHailResponse(DialogueNode hailResponse){
        this.hailResponse = hailResponse;
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
    public <D> void putAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.putAdditionalData(ops, builder);
        builder.put(ops.createString("nodes"), ops.createList(additionalNodes.stream().map(x -> x.serialize(ops))));
        builder.put(ops.createString("prompts"), ops.createList(additionalPrompts.stream().map(x -> x.serialize(ops))));
        builder.put(ops.createString("hailResponse"), hailResponse.serialize(ops));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        List<DialogueNode> nodes = dynamic.get("nodes").asList(x -> {
            DialogueNode node = new DialogueNode();
            node.deserialize(x);
            return node;
        });
        additionalNodes.clear();
        this.additionalNodes.addAll(nodes);
        List<DialoguePrompt> prompts = dynamic.get("prompts").asList(x -> {
            DialoguePrompt prompt = new DialoguePrompt();
            prompt.deserialize(x);
            return prompt;
        });
        additionalPrompts.clear();
        additionalPrompts.addAll(prompts);
        hailResponse = new DialogueNode();
        dynamic.get("hailResponse").result().ifPresent(x -> hailResponse.deserialize(x));
    }

    private DialogueNode copyNodeAndSetUUID(DialogueNode node, UUID questId){
        DialogueNode newNode = node.copy();
        for (DialogueEffect effect : newNode.getEffects()){
            if (effect instanceof AdvanceQuestChainEffect){
                AdvanceQuestChainEffect advEffect = (AdvanceQuestChainEffect) effect;
                advEffect.setChainId(questId);
            }
        }
        return newNode;
    }

    public void generateDialogueForNpc(Quest quest, QuestChainInstance questChain, ResourceLocation npcDefinitionName,
                                       UUID npcId, DialogueTree tree,
                                       Map<ResourceLocation, List<MKStructureEntry>> questStructures){
        DialogueNode hrCopy = copyNodeAndSetUUID(hailResponse, questChain.getQuestId());
        DialogueResponse hrResponse = new DialogueResponse(hrCopy.getId());
        hrResponse.addCondition(new OnQuestCondition(questChain.getQuestId(), quest.getQuestName()));
        for (DialogueNode node : additionalNodes){
            tree.addNode(copyNodeAndSetUUID(node, questChain.getQuestId()));
        }
        for (DialoguePrompt prompt : additionalPrompts){
            DialoguePrompt copyPrompt = prompt.copy();
            for (DialogueResponse resp : copyPrompt.getResponses()){
                resp.addCondition(new OnQuestCondition(questChain.getQuestId(), quest.getQuestName()));
            }
            tree.addPrompt(copyPrompt);
        }
        tree.addNode(hrCopy);
        DialoguePrompt hailPrompt = tree.getHailPrompt();
        if (hailPrompt != null){
            hailPrompt.addResponse(hrResponse);
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
    public boolean isComplete(PlayerQuestObjectiveData playerData) {
        return playerData.getBool("hasSpoken");
    }
}
