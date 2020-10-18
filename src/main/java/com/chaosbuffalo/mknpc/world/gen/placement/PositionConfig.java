package com.chaosbuffalo.mknpc.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.IPlacementConfig;

import java.util.stream.IntStream;

public class PositionConfig implements IPlacementConfig {
    public final BlockPos blockPos;

    public PositionConfig(BlockPos blockPos){
        this.blockPos = blockPos;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("blockPos"),
                ops.createIntList(IntStream.of(blockPos.getX(), blockPos.getY(), blockPos.getZ())))));
    }

    public static PositionConfig deserialize(Dynamic<?> p_214722_0_) {
        IntStream pos = p_214722_0_.get("blockPos").asIntStream();
        int[] arr = pos.toArray();
        return new PositionConfig(new BlockPos(arr[0], arr[1], arr[2]));
    }
}
