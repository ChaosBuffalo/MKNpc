package com.chaosbuffalo.mknpc.quest.data.objective;

import net.minecraft.nbt.CompoundNBT;

public class EmptyInstanceData extends ObjectiveInstanceData {

    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }
}
