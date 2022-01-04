package com.chaosbuffalo.mknpc.quest.data;

import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.data.objective.ObjectiveInstanceData;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

public class QuestData{

    private final Map<String, ObjectiveInstanceData> objectives = new HashMap<>();
    private String questName;

    public QuestData(String questName){
        this.questName = questName;
    }

    public void putObjective(String objectiveName, ObjectiveInstanceData data){
        objectives.put(objectiveName, data);
    }

    public ObjectiveInstanceData getObjective(String objectiveName){
        return objectives.get(objectiveName);
    }

    public String getQuestName() {
        return questName;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT objectiveNbt = new CompoundNBT();
        for (Map.Entry<String, ObjectiveInstanceData> entry : objectives.entrySet()){
            objectiveNbt.put(entry.getKey(), entry.getValue().serializeNBT());
        }
        nbt.put("objectives", objectiveNbt);
        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt, Quest quest) {
        CompoundNBT objectiveNbt = nbt.getCompound("objectives");
        for (String key : objectiveNbt.keySet()){
            QuestObjective<?> obj = quest.getObjective(key);
            if (obj != null){
                putObjective(obj.getObjectiveName(), obj.loadInstanceData(objectiveNbt.getCompound(key)));
            }

        }
    }
}
