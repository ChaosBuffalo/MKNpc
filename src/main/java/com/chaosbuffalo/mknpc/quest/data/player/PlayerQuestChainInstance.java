package com.chaosbuffalo.mknpc.quest.data.player;

import com.chaosbuffalo.mkcore.sync.IMKSerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.UUID;
import java.util.function.Consumer;

public class PlayerQuestChainInstance implements IMKSerializable<CompoundNBT> {
    private Consumer<PlayerQuestChainInstance> dirtyNotifier;
    private UUID questId;
    private ITextComponent questName;
    private String currentQuest;

    public PlayerQuestChainInstance(UUID questId){
        this.questId = questId;
    }

    public UUID getQuestId() {
        return questId;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("questName", ITextComponent.Serializer.toJson(questName));
        tag.putUniqueId("questId", questId);
        tag.putString("currentQuest", currentQuest);
        return tag;
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
        return true;
    }

    public void setDirtyNotifier(Consumer<PlayerQuestChainInstance> dirtyNotifier) {
        this.dirtyNotifier = dirtyNotifier;
    }
}
