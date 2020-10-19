package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;

public interface IChunkNpcData extends INBTSerializable<CompoundNBT> {

    void attach(Chunk chunk);
}
