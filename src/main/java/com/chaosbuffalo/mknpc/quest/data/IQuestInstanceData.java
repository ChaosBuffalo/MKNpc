package com.chaosbuffalo.mknpc.quest.data;

import com.chaosbuffalo.mknpc.quest.QuestDefinition;
import net.minecraft.nbt.CompoundNBT;

public interface IQuestInstanceData {

    CompoundNBT serializeNBT();
    void deserializeNBT(CompoundNBT nbt, QuestDefinition definition);
}
