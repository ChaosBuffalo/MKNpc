package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.UUID;

//public abstract class MKStructureStart<C extends FeatureConfiguration> extends StructureStart<C> implements IAdditionalStartData {
//    private UUID instanceId;
//
//    public MKStructureStart(StructureFeature<C> structure, int chunkX, int chunkY,
//                            BoundingBox boundingBox, int refCount, long seed) {
//        super(structure, chunkX, chunkY, boundingBox, refCount, seed);
//        instanceId = UUID.randomUUID();
//    }
//
//
//    @Override
//    public CompoundTag createTag(int chunkX, int chunkZ) {
//        CompoundTag tag = super.createTag(chunkX, chunkZ);
//        if (isValid()){
//            tag.putUUID("instanceId", instanceId);
//        }
//        return tag;
//    }
//
//    @Override
//    public void readAdditional(CompoundTag tag){
//        if (tag.contains("instanceId")){
//            instanceId = tag.getUUID("instanceId");
//        }
//    }
//
//    public UUID getInstanceId() {
//        return instanceId;
//    }
//
//
//    protected BlockPos getStructurePos(ChunkGenerator generator,
//                                       StructureManager templateManagerIn,
//                                       int chunkX, int chunkZ, Biome biomeIn){
//        return new BlockPos(chunkX * 16, generator.getSpawnHeight(), chunkZ * 16);
//    }
//
//    protected Rotation getStructureRotation(ChunkGenerator generator,
//                                            StructureManager templateManagerIn,
//                                            int chunkX, int chunkZ, Biome biomeIn){
//        return Rotation.values()[this.random.nextInt(Rotation.values().length)];
//    }
//
//    public abstract void getComponents(MKStructurePieceArgs args, C config);
//
//    @Override
//    public void generatePieces(RegistryAccess registries, ChunkGenerator chunkGenerator,
//                               StructureManager templateManager, int chunkX, int chunkZ,
//                               Biome biome, C config) {
//        MKStructurePieceArgs args = new MKStructurePieceArgs(chunkGenerator, getFeature(), templateManager,
//                getStructurePos(chunkGenerator, templateManager, chunkX, chunkZ, biome),
//                getStructureRotation(chunkGenerator, templateManager, chunkX, chunkZ, biome),
//                random, getInstanceId(), pieces);
//        getComponents(args, config);
//        calculateBoundingBox();
//    }
//
////    @Override
////    public void init(ChunkGenerator generator, TemplateManager templateManagerIn,
////                     int chunkX, int chunkZ, Biome biomeIn) {
////        MKStructurePieceArgs args = new MKStructurePieceArgs(generator, getStructure(), templateManagerIn,
////                getStructurePos(generator, templateManagerIn, chunkX, chunkZ, biomeIn),
////                getStructureRotation(generator, templateManagerIn, chunkX, chunkZ, biomeIn),
////                rand, getInstanceId(), components);
////        getComponents(args, getConfig(generator, biomeIn));
////        recalculateStructureSize();
////    }
//}
