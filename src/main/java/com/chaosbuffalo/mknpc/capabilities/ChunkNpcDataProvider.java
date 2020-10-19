package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChunkNpcDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final ChunkNpcDataHandler data;

    public ChunkNpcDataProvider(Chunk chunk) {
        data = new ChunkNpcDataHandler();
        data.attach(chunk);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return NpcCapabilities.CHUNK_NPC_DATA_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> data));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) NpcCapabilities.CHUNK_NPC_DATA_CAPABILITY.getStorage().writeNBT(
                NpcCapabilities.CHUNK_NPC_DATA_CAPABILITY, data, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        NpcCapabilities.CHUNK_NPC_DATA_CAPABILITY.getStorage().readNBT(
                NpcCapabilities.CHUNK_NPC_DATA_CAPABILITY, data, null, nbt);
    }
}
