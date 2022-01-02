package com.chaosbuffalo.mknpc.quest;

import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.quest.data.QuestChainData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestChainInstance implements INBTSerializable<CompoundNBT> {

    private UUID questId;
    private QuestDefinition definition;
    private QuestChainData questChainData;

    public QuestChainInstance(QuestDefinition definition, Map<ResourceLocation, List<MKStructureEntry>> questStructures){
        questId = UUID.randomUUID();
        this.definition = definition;
        questChainData = new QuestChainData(definition, questStructures);
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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putUniqueId("questId", questId);
        nbt.putString("definitionId", definition.getName().toString());
        nbt.put("questData", questChainData.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        questId = nbt.getUniqueId("questId");
        definition = QuestDefinitionManager.getDefinition(new ResourceLocation(nbt.getString("definitionId")));
        questChainData = new QuestChainData(definition, nbt.getCompound("questData"));
    }
}
