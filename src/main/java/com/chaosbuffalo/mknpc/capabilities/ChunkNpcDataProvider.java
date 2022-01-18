package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;

public class ChunkNpcDataProvider extends NpcCapabilities.Provider<Chunk, IChunkNpcData> {

    public ChunkNpcDataProvider(Chunk chunk) {
        super(chunk);
    }

    @Override
    IChunkNpcData makeData(Chunk attached) {
        return new ChunkNpcDataHandler(attached);
    }

    @Override
    Capability<IChunkNpcData> getCapability() {
        return NpcCapabilities.CHUNK_NPC_DATA_CAPABILITY;
    }
}

