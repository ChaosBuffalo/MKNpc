package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ChunkNpcDataHandler implements IChunkNpcData{
    private Chunk chunk;

    @Override
    public void attach(Chunk chunk) {
        this.chunk = chunk;
    }

    @Override
    public CompoundNBT serializeNBT() {
        return new CompoundNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

    public static class Storage implements Capability.IStorage<IChunkNpcData> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<IChunkNpcData> capability, IChunkNpcData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IChunkNpcData> capability, IChunkNpcData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
