package com.chaosbuffalo.mknpc.quest.data;

import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.data.objective.ObjectiveInstanceData;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

// This is the backing data for a specialized version of a Quest
public class QuestData {

    private final Quest quest;
    private final Map<String, ObjectiveInstanceData> objectives = new HashMap<>();

    public QuestData(Quest quest) {
        this.quest = quest;
    }

    public void putObjective(String objectiveName, ObjectiveInstanceData data) {
        objectives.put(objectiveName, data);
    }

    public ObjectiveInstanceData getObjective(String objectiveName) {
        return objectives.get(objectiveName);
    }

    public <T extends ObjectiveInstanceData> T getObjective(QuestObjective<T> objective) {
        return (T) getObjective(objective.getObjectiveName());
    }

    public String getQuestName() {
        return quest.getQuestName();
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT objectiveNbt = new CompoundNBT();
        for (Map.Entry<String, ObjectiveInstanceData> entry : objectives.entrySet()) {
            objectiveNbt.put(entry.getKey(), entry.getValue().serializeNBT());
        }
        nbt.put("objectives", objectiveNbt);
        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt, Quest quest) {
        CompoundNBT objectiveNbt = nbt.getCompound("objectives");
        for (String key : objectiveNbt.keySet()) {
            QuestObjective<?> obj = quest.getObjective(key);
            if (obj != null) {
                putObjective(obj.getObjectiveName(), obj.loadInstanceData(objectiveNbt.getCompound(key)));
            }
        }
    }
}
