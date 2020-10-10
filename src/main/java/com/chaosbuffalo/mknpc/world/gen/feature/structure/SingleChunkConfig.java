package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.stream.IntStream;

public class SingleChunkConfig implements IFeatureConfig {
    public final int xChunk;
    public final int zChunk;

    public SingleChunkConfig(int xChunk, int zChunk){
        this.xChunk = xChunk;
        this.zChunk = zChunk;
    }

    @Override
    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("xzChunkCoords"),
                ops.createIntList(IntStream.of(xChunk, zChunk)))));
    }

    public static SingleChunkConfig deserialize(Dynamic<?> p_214722_0_) {
        IntStream pos = p_214722_0_.get("xzChunkCoords").asIntStream();
        int[] arr = pos.toArray();
        return new SingleChunkConfig(arr[0], arr[1]);
    }
}
