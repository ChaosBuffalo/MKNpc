package com.chaosbuffalo.mknpc.world.gen.feature;

import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

public class MKSpringFeature extends SpringFeature {

    public MKSpringFeature(Codec<SpringConfiguration> p_i231995_1_) {
        super(p_i231995_1_);
    }

    @Override
    public boolean place(FeaturePlaceContext<SpringConfiguration> p_160404_) {
        Stream<? extends StructureStart<?>> ours = Stream.of();
        for (StructureFeature<?> struct : MKNpcWorldGen.NO_WATER_STRUCTURES){
            ours = Stream.concat(ours, p_160404_.level().startsForFeature(SectionPos.of(p_160404_.origin()), struct));
        }
        if (ours.findAny().isPresent()){
            return false;
        }
        return super.place(p_160404_);
    }
}
