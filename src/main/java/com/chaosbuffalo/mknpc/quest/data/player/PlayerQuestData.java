package com.chaosbuffalo.mknpc.quest.data.player;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

public class PlayerQuestData implements INBTSerializable<CompoundNBT> {

    private final Map<String, PlayerQuestObjectiveData> objectives = new HashMap<>();
    private String questName;

    public void putObjective(String objectiveName, PlayerQuestObjectiveData data){
        objectives.put(objectiveName, data);
    }

    public PlayerQuestObjectiveData getObjective(String objectiveName){
        return objectives.get(objectiveName);
    }



    @Override
    public CompoundNBT serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }
}
