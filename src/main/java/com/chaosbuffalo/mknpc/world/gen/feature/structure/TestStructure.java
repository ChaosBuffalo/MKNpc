package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.mojang.serialization.Codec;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.structure.Structure;


public class TestStructure extends SingleChunkStructure {


    public TestStructure(Codec<ChunkPosConfig> config) {
        super(config);
    }

    @Override
    public IStartFactory getStartFactory() {
        return TestStructure.Start::new;
    }

    @Override
    public String getStructureName() {
        return MKNpcWorldGen.TEST_STRUCTURE_NAME.toString();
    }


    public static class Start extends MKStructureStart<ChunkPosConfig> {

        public Start(Structure<ChunkPosConfig> structure, int chunkX, int chunkY,
                     MutableBoundingBox boundingBox, int refCount, long seed) {
            super(structure, chunkX, chunkY, boundingBox, refCount, seed);
        }

        @Override
        public void getComponents(MKStructurePieceArgs args, ChunkPosConfig config) {
            TestStructurePieces.getPieces(args, config);
        }
    }
}
