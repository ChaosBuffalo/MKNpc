package com.chaosbuffalo.mknpc.npc.entries;

import com.chaosbuffalo.mkchat.dialogue.DialogueNode;
import com.chaosbuffalo.mkchat.dialogue.DialoguePrompt;
import com.chaosbuffalo.mkchat.dialogue.DialogueResponse;
import com.chaosbuffalo.mkchat.dialogue.DialogueTree;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.quest.QuestDefinition;
import com.chaosbuffalo.mknpc.quest.QuestDefinitionManager;
import com.chaosbuffalo.mknpc.quest.dialogue.conditions.OnQuestChainCondition;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.StartQuestChainEffect;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public class QuestOfferingEntry implements INBTSerializable<CompoundNBT> {
    private ResourceLocation questDef;
    @Nullable
    private UUID questId;
    @Nullable
    private DialogueTree tree;

    public QuestOfferingEntry(ResourceLocation questDef){
        this.questDef = questDef;
        this.questId = null;
    }

    @Nullable
    public DialogueTree getTree() {
        return tree;
    }

    public ResourceLocation getQuestDef() {
        return questDef;
    }

    public QuestOfferingEntry(CompoundNBT nbt){
        deserializeNBT(nbt);
    }

    @Nullable
    public UUID getQuestId() {
        return questId;
    }

    public void setQuestId(@Nullable UUID questId) {
        this.questId = questId;
        if (questId == null){
            return;
        }
        QuestDefinition definition = QuestDefinitionManager.getDefinition(questDef);
        DialogueNode startQuest = definition.getStartQuestResponse().copy();
        DialogueNode hailQuest = definition.getStartQuestHail().copy();
        startQuest.addEffect(new StartQuestChainEffect(questId));
        DialogueTree giverTree = new DialogueTree(new ResourceLocation(MKNpc.MODID, String.format("give_quest.%s", questId.toString())));
        DialoguePrompt hailPrompt = new DialoguePrompt("hail");
        hailPrompt.addResponse(new DialogueResponse(hailQuest.getId()).addCondition(
                new OnQuestChainCondition(questId).setInvert(true)));
        giverTree.addPrompt(hailPrompt);
        giverTree.setHailPrompt(hailPrompt);
        giverTree.addNode(startQuest);
        giverTree.addNode(hailQuest);
        this.tree = giverTree;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("questDef", questDef.toString());
        if (questId != null){
            nbt.putUniqueId("questId", questId);
        }
        if (tree != null){
            nbt.put("dialogue", tree.serialize(NBTDynamicOps.INSTANCE));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        questDef = new ResourceLocation(nbt.getString("questDef"));
        if (nbt.contains("questId")){
            questId = nbt.getUniqueId("questId");
        }
        if (nbt.contains("dialogue")){
            tree = new DialogueTree(new ResourceLocation(MKNpc.MODID, String.format("give_quest.%s", questId.toString())));
            tree.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt.get("dialogue")));
        }
    }
}
