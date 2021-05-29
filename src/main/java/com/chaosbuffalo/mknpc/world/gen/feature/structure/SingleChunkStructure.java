package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;


public abstract class SingleChunkStructure extends Structure<ChunkPosConfig> {

    public SingleChunkStructure(Codec<ChunkPosConfig> config) {
        super(config);
    }

    @Override
    protected boolean func_230363_a_(ChunkGenerator chunkGenerator, BiomeProvider biomeProvider,
                                     long seed, SharedSeedRandom random,
                                     int chunkX, int chunkZ, Biome biome,
                                     ChunkPos chunkPos, ChunkPosConfig config) {
        return isInChunk(config, chunkX, chunkZ);
    }


    private boolean isInChunk(ChunkPosConfig config, int chunkX, int chunkZ){
        return chunkX == config.xChunk && chunkZ == config.zChunk;
    }
}
