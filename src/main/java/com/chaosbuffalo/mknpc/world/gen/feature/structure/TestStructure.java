package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.function.Function;

public class TestStructure extends SingleChunkStructure {

    public TestStructure(Function<Dynamic<?>, ? extends ChunkPosConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public IStartFactory getStartFactory() {
        return TestStructure.Start::new;
    }

    @Override
    public String getStructureName() {
        return MKNpcWorldGen.TEST_STRUCTURE_NAME.toString();
    }

    @Override
    public int getSize() {
        return 1;
    }

    public static class Start extends MKStructureStart<ChunkPosConfig> {

        public Start(Structure<?> structure, int chunkX, int chunkY,
                     MutableBoundingBox boundingBox, int refCount, long seed) {
            super(structure, chunkX, chunkY, boundingBox, refCount, seed);
        }

        @Override
        public void getComponents(MKStructurePieceArgs args, ChunkPosConfig config) {
            TestStructurePieces.getPieces(args, config);
        }
    }
}
