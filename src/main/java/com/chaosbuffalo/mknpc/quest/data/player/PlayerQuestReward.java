package com.chaosbuffalo.mknpc.quest.data.player;

import com.chaosbuffalo.mknpc.quest.rewards.QuestReward;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerQuestReward implements INBTSerializable<CompoundNBT> {
    IFormattableTextComponent description;


    public PlayerQuestReward(QuestReward questReward){
        this.description = questReward.getDescription();
    }

    public PlayerQuestReward(CompoundNBT nbt){
        deserializeNBT(nbt);
    }

    public IFormattableTextComponent getDescription() {
        return description;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("description", ITextComponent.Serializer.toJson(description));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        description = ITextComponent.Serializer.getComponentFromJson(nbt.getString("description"));
    }
}
