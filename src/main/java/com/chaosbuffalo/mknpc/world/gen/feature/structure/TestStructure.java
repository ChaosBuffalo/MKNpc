package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.feature.StructureFeature;


import net.minecraft.world.level.levelgen.feature.StructureFeature.StructureStartFactory;

//public class TestStructure extends SingleChunkStructure {
//
//
//    public TestStructure(Codec<ChunkPosConfig> config) {
//        super(config);
//    }
//
//    @Override
//    public StructureStartFactory getStartFactory() {
//        return TestStructure.Start::new;
//    }
//
//    @Override
//    public String getFeatureName() {
//        return MKNpcWorldGen.TEST_STRUCTURE_NAME.toString();
//    }
//
//
//    public static class Start extends MKStructureStart<ChunkPosConfig> {
//
//        public Start(StructureFeature<ChunkPosConfig> structure, int chunkX, int chunkY,
//                     BoundingBox boundingBox, int refCount, long seed) {
//            super(structure, chunkX, chunkY, boundingBox, refCount, seed);
//        }
//
//        @Override
//        public void getComponents(MKStructurePieceArgs args, ChunkPosConfig config) {
//            TestStructurePieces.getPieces(args, config);
//        }
//    }
//}
