package com.chaosbuffalo.mknpc.quest.data.player;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class PlayerQuestData implements INBTSerializable<CompoundNBT> {

    private final LinkedHashMap<String, PlayerQuestObjectiveData> objectives = new LinkedHashMap<>();
    private String questName;
    private IFormattableTextComponent description;
    private final List<PlayerQuestReward> playerQuestRewards = new ArrayList<>();

    public PlayerQuestData(String questName, IFormattableTextComponent description){
        this.questName = questName;
        this.description = description;
    }

    public PlayerQuestData(CompoundNBT nbt){
        deserializeNBT(nbt);
    }

    public void putObjective(String objectiveName, PlayerQuestObjectiveData data){
        objectives.put(objectiveName, data);
    }

    public List<PlayerQuestReward> getQuestRewards() {
        return playerQuestRewards;
    }

    public void addReward(PlayerQuestReward questReward){
        playerQuestRewards.add(questReward);
    }

    public boolean isComplete(){
        return objectives.values().stream().allMatch(PlayerQuestObjectiveData::isComplete);
    }

    public IFormattableTextComponent getDescription() {
        return description;
    }

    public Collection<PlayerQuestObjectiveData> getObjectives(){
        return objectives.values();
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
        ListNBT objectiveNbt = new ListNBT();
        for (Map.Entry<String, PlayerQuestObjectiveData> entry : objectives.entrySet()){
            objectiveNbt.add(entry.getValue().serializeNBT());
        }
        nbt.put("objectives", objectiveNbt);
        nbt.putString("questName", questName);
        nbt.putString("description", ITextComponent.Serializer.toJson(description));
        ListNBT rewardNbt = new ListNBT();
        for (PlayerQuestReward reward : playerQuestRewards){
            rewardNbt.add(reward.serializeNBT());
        }
        nbt.put("rewards", rewardNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT objectiveNbt = nbt.getList("objectives", Constants.NBT.TAG_COMPOUND);
        for (INBT objNbt : objectiveNbt){
            PlayerQuestObjectiveData objective = new PlayerQuestObjectiveData((CompoundNBT) objNbt);
            objectives.put(objective.getObjectiveName(), objective);
        }
        questName = nbt.getString("questName");
        description = ITextComponent.Serializer.getComponentFromJson(nbt.getString("description"));
        ListNBT rewardNbts = nbt.getList("rewards", Constants.NBT.TAG_COMPOUND);
        for (INBT rewardNbt : rewardNbts){
            addReward(new PlayerQuestReward((CompoundNBT) rewardNbt));
        }
    }
}
