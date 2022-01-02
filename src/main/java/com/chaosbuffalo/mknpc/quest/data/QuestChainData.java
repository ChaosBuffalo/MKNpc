package com.chaosbuffalo.mknpc.quest.data;

import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.QuestDefinition;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestChainData implements IQuestInstanceData{

    private Map<String, QuestData> questData;


    public QuestChainData(QuestDefinition definition, Map<ResourceLocation, List<MKStructureEntry>> questStructures){
        questData = new HashMap<>();
        for (Quest quest : definition.getQuestChain()){
            QuestData qData = new QuestData(quest.getQuestName());
            for (QuestObjective<?> objective : quest.getObjectives()){
                objective.createDataForQuest(qData, questStructures);
            }
            questData.put(quest.getQuestName(), qData);
        }

    }

    public QuestChainData(QuestDefinition definition, CompoundNBT nbt){
        questData = new HashMap<>();
        deserializeNBT(nbt, definition);
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT questNbt = new CompoundNBT();
        for (Map.Entry<String, QuestData> entry : questData.entrySet()){
            questNbt.put(entry.getKey(), entry.getValue().serializeNBT());
        }
        nbt.put("questData", questNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt, QuestDefinition definition) {
        CompoundNBT questNbt = nbt.getCompound("questData");
        for (String key : questNbt.keySet()){
            Quest source = definition.getQuest(key);
            if (source != null){
                QuestData data = new QuestData(source.getQuestName());
                data.deserializeNBT(questNbt.getCompound(key), source);
            }
        }
    }
}