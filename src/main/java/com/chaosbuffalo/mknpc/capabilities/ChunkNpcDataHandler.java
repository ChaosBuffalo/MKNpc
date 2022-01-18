package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.chunk.Chunk;

public class ChunkNpcDataHandler implements IChunkNpcData {
    private final Chunk chunk;

    public ChunkNpcDataHandler(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }
}
