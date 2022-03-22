package com.chaosbuffalo.mknpc.world.gen.feature;

import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.LiquidsConfig;
import net.minecraft.world.gen.feature.SpringFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

import java.util.Random;
import java.util.stream.Stream;

public class MKSpringFeature extends SpringFeature {

    public MKSpringFeature(Codec<LiquidsConfig> p_i231995_1_) {
        super(p_i231995_1_);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, LiquidsConfig config) {
        Stream<? extends StructureStart<?>> ours = Stream.of();
        for (Structure<?> struct : MKNpcWorldGen.getNoWaterStructures()) {
            if (struct != null) {
                ours = Stream.concat(ours, reader.func_241827_a(SectionPos.from(pos), struct));
            }
        }
        if (ours.findAny().isPresent()) {
            return false;
        }
        return super.generate(reader, generator, rand, pos, config);
    }
}
