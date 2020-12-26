package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.UUID;

public abstract class MKStructureStart<C extends IFeatureConfig> extends StructureStart {
    private final UUID instanceId;

    public MKStructureStart(Structure<?> structure, int chunkX, int chunkY,
                            MutableBoundingBox boundingBox, int refCount, long seed) {
        super(structure, chunkX, chunkY, boundingBox, refCount, seed);
        instanceId = UUID.randomUUID();
    }


    @Override
    public CompoundNBT write(int chunkX, int chunkZ) {
        CompoundNBT tag = super.write(chunkX, chunkZ);
        if (isValid()){
            tag.putString("instanceId", instanceId.toString());
        }
        return tag;
    }

    public UUID getInstanceId() {
        return instanceId;
    }

    protected C getConfig(ChunkGenerator<?> generator, Biome biomeIn){
        return (C) generator.getStructureConfig(biomeIn, getStructure());
    }

    protected BlockPos getStructurePos(ChunkGenerator<?> generator,
                                       TemplateManager templateManagerIn,
                                       int chunkX, int chunkZ, Biome biomeIn){
        return new BlockPos(chunkX * 16, generator.getGroundHeight(), chunkZ * 16);
    }

    protected Rotation getStructureRotation(ChunkGenerator<?> generator,
                                            TemplateManager templateManagerIn,
                                            int chunkX, int chunkZ, Biome biomeIn){
        return Rotation.values()[this.rand.nextInt(Rotation.values().length)];
    }

    public abstract void getComponents(MKStructurePieceArgs args, C config);

    @Override
    public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn,
                     int chunkX, int chunkZ, Biome biomeIn) {
        MKStructurePieceArgs args = new MKStructurePieceArgs(getStructure(), templateManagerIn,
                getStructurePos(generator, templateManagerIn, chunkX, chunkZ, biomeIn),
                getStructureRotation(generator, templateManagerIn, chunkX, chunkZ, biomeIn),
                rand, getInstanceId(), components);
        getComponents(args, getConfig(generator, biomeIn));
        recalculateStructureSize();
    }
}
