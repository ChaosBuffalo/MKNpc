package com.chaosbuffalo.mknpc.quest.data.player;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

public class PlayerQuestData implements INBTSerializable<CompoundNBT> {

    private final Map<String, PlayerQuestObjectiveData> objectives = new HashMap<>();
    private String questName;

    public PlayerQuestData(String questName){
        this.questName = questName;
    }

    public PlayerQuestData(CompoundNBT nbt){
        deserializeNBT(nbt);
    }

    public void putObjective(String objectiveName, PlayerQuestObjectiveData data){
        objectives.put(objectiveName, data);
    }

    public PlayerQuestObjectiveData getObjective(String objectiveName){
        return objectives.get(objectiveName);
    }


    public String getQuestName() {
        return questName;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT objectiveNbt = new CompoundNBT();
        for (Map.Entry<String, PlayerQuestObjectiveData> entry : objectives.entrySet()){
            objectiveNbt.put(entry.getKey(), entry.getValue().serializeNBT());
        }
        nbt.put("objectives", objectiveNbt);
        nbt.putString("questName", questName);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT objectiveNbt = nbt.getCompound("objectives");
        for (String key : objectiveNbt.keySet()){
            objectives.put(key, new PlayerQuestObjectiveData(objectiveNbt.getCompound(key)));
        }
        questName = nbt.getString("questName");
    }
}
