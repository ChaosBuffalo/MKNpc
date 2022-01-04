package com.chaosbuffalo.mknpc.quest.data.objective;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class ObjectiveInstanceData implements INBTSerializable<CompoundNBT> {

    public ObjectiveInstanceData(){

    }

    public ObjectiveInstanceData(CompoundNBT nbt){
        deserializeNBT(nbt);
    }
}
