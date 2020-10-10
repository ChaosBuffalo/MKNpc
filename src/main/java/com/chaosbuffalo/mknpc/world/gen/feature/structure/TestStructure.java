package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IglooPieces;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.function.Function;

public class TestStructure extends SingleChunkStructure {

    public TestStructure(Function<Dynamic<?>, ? extends SingleChunkConfig> configFactoryIn) {
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
        return 3;
    }

    public static class Start extends StructureStart {

        public Start(Structure<?> p_i225876_1_, int p_i225876_2_, int p_i225876_3_,
                     MutableBoundingBox p_i225876_4_, int p_i225876_5_, long p_i225876_6_) {
            super(p_i225876_1_, p_i225876_2_, p_i225876_3_, p_i225876_4_, p_i225876_5_, p_i225876_6_);
        }

        @Override
        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn,
                         int chunkX, int chunkZ, Biome biomeIn) {
            SingleChunkConfig config = generator.getStructureConfig(biomeIn, MKNpcWorldGen.TEST_STRUCTURE);
            BlockPos blockpos = new BlockPos(chunkX * 16, generator.getGroundHeight(), chunkZ * 16);
            Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];
            TestStructurePieces.getPieces(templateManagerIn, blockpos, rotation, this.components, this.rand, config);
            recalculateStructureSize();
        }
    }
}
