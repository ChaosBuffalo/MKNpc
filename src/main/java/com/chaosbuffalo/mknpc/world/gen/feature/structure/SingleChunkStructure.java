package com.chaosbuffalo.mknpc.world.gen.feature.structure;


import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.Random;
import java.util.function.Function;

public abstract class SingleChunkStructure extends Structure<SingleChunkConfig> {

    public SingleChunkStructure(Function<Dynamic<?>, ? extends SingleChunkConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public boolean canBeGenerated(BiomeManager biomeManagerIn,
                                  ChunkGenerator<?> generatorIn, Random randIn, int chunkX,
                                  int chunkZ, Biome biomeIn) {
        SingleChunkConfig config = generatorIn.getStructureConfig(biomeIn, this);
        if (config == null){
            return false;
        } else {
            return isInSingleChunk(config, chunkX, chunkZ);
        }

    }

    private boolean isInSingleChunk(SingleChunkConfig config, int chunkX, int chunkZ){
        return chunkX == config.xChunk && chunkZ == config.zChunk;
    }
}
