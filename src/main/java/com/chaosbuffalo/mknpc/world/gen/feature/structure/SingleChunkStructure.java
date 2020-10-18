package com.chaosbuffalo.mknpc.world.gen.feature.structure;


import com.mojang.datafixers.Dynamic;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Random;
import java.util.function.Function;

public abstract class SingleChunkStructure extends Structure<ChunkPosConfig> {

    public SingleChunkStructure(Function<Dynamic<?>, ? extends ChunkPosConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean canBeGenerated(BiomeManager biomeManagerIn,
                                  ChunkGenerator<?> generatorIn, Random randIn, int chunkX,
                                  int chunkZ, Biome biomeIn) {
        ChunkPosConfig config = generatorIn.getStructureConfig(biomeIn, this);
        if (config == null){
            return false;
        } else {
            return isInChunk(config, chunkX, chunkZ);
        }

    }

    private boolean isInChunk(ChunkPosConfig config, int chunkX, int chunkZ){
        return chunkX == config.xChunk && chunkZ == config.zChunk;
    }
}
