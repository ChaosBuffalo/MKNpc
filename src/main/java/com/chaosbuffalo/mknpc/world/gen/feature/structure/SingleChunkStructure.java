package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeature;


//public abstract class SingleChunkStructure extends StructureFeature<ChunkPosConfig> {
//
//    public SingleChunkStructure(Codec<ChunkPosConfig> config) {
//        super(config);
//    }
//
//    @Override
//    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeSource biomeProvider,
//                                     long seed, WorldgenRandom random,
//                                     int chunkX, int chunkZ, Biome biome,
//                                     ChunkPos chunkPos, ChunkPosConfig config) {
//        return isInChunk(config, chunkX, chunkZ);
//    }
//
//
//    private boolean isInChunk(ChunkPosConfig config, int chunkX, int chunkZ){
//        return chunkX == config.xChunk && chunkZ == config.zChunk;
//    }
//}
