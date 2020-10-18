package com.chaosbuffalo.mknpc.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

public class AtBlockPos extends Placement<PositionConfig> {


    public AtBlockPos(Function<Dynamic<?>, ? extends PositionConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn,
                                         Random random, PositionConfig configIn, BlockPos pos) {
        return Stream.of(configIn.blockPos);
    }
}
