package com.chaosbuffalo.mknpc.quest.data.player;

import com.chaosbuffalo.mkcore.sync.IMKSerializable;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerQuestChainInstance implements IMKSerializable<CompoundNBT> {
    private Consumer<PlayerQuestChainInstance> dirtyNotifier;
    private UUID questId;
    private ITextComponent questName;
    private String currentQuest;
    private boolean questComplete;
    private final LinkedHashMap<String, PlayerQuestData> questData = new LinkedHashMap<>();

    public PlayerQuestChainInstance(UUID questId){
        this.questId = questId;
        questComplete = false;
    }

    public LinkedHashMap<String, PlayerQuestData> getQuestData() {
        return questData;
    }

    public PlayerQuestChainInstance(CompoundNBT nbt){
        deserialize(nbt);
    }

    public boolean isQuestComplete() {
        return questComplete;
    }

    public UUID getQuestId() {
        return questId;
    }

    public void setQuestName(ITextComponent questName) {
        this.questName = questName;
    }

    public void setupQuestChain(QuestChainInstance instance){
        setQuestName(instance.getDefinition().getQuestName());
    }

    public ITextComponent getQuestName() {
        return questName;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("questName", ITextComponent.Serializer.toJson(questName));
        tag.putUniqueId("questId", questId);
        tag.putString("currentQuest", currentQuest);
        tag.putBoolean("questComplete", questComplete);
        ListNBT quests = new ListNBT();
        for (Map.Entry<String, PlayerQuestData> entry : questData.entrySet()){
            quests.add(entry.getValue().serializeNBT());
        }
        tag.put("quests", quests);
        return tag;
    }

    public void addQuestData(PlayerQuestData questData){
        this.questData.put(questData.getQuestName(), questData);
    }

    public PlayerQuestData getQuestData(String questName){
        return questData.get(questName);
    }

    public String getCurrentQuest() {
        return currentQuest;
    }


    public void setCurrentQuest(String currentQuest) {
        this.currentQuest = currentQuest;
    }

    @Override
    public boolean deserialize(CompoundNBT compoundNBT) {
        questName = ITextComponent.Serializer.getComponentFromJson(compoundNBT.getString("questName"));
        questId = compoundNBT.getUniqueId("questId");
        currentQuest = compoundNBT.getString("currentQuest");
        ListNBT questData = compoundNBT.getList("quests", Constants.NBT.TAG_COMPOUND);
        for (INBT questNbt : questData){
            PlayerQuestData newData = new PlayerQuestData((CompoundNBT) questNbt);
            this.questData.put(newData.getQuestName(), newData);
        }
        questComplete = compoundNBT.getBoolean("questComplete");
        return true;
    }

    public void setQuestComplete(boolean questComplete) {
        this.questComplete = questComplete;
    }

    public void setDirtyNotifier(Consumer<PlayerQuestChainInstance> dirtyNotifier) {
        this.dirtyNotifier = dirtyNotifier;
    }

    public void notifyDirty(){
        dirtyNotifier.accept(this);
    }
}
