package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.MKNpc;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;

public class TestJigsawStructurePools {

    private static final ResourceLocation DIGGER_BIG_1 = new ResourceLocation(MKNpc.MODID, "digger/diggerbig1");
    private static final ResourceLocation DIGGER_ROAD_1 = new ResourceLocation(MKNpc.MODID, "digger/diggerrd1");
    private static final ResourceLocation DIGGER_TENT_DBL_1 = new ResourceLocation(MKNpc.MODID, "digger/diggertentdbl1");
    private static final ResourceLocation DIGGER_TENT_SGL_1 = new ResourceLocation(MKNpc.MODID, "digger/diggertentsgl1");

    public static final int GEN_DEPTH = 7;

    public static final StructureTemplatePool BASE_PATTERN = Pools.register(
            new StructureTemplatePool(new ResourceLocation(MKNpc.MODID, "digger/diggerbase"),
                    new ResourceLocation("empty"), ImmutableList.of(
                            Pair.of(MKSingleJigsawPiece.getMKSingleJigsaw(DIGGER_BIG_1, false), 1)), StructureTemplatePool.Projection.RIGID));

    public static void registerPatterns() {
        Pools.register(new StructureTemplatePool(new ResourceLocation(MKNpc.MODID, "digger/diggerroad"), new ResourceLocation("empty"),
                ImmutableList.of(Pair.of(MKSingleJigsawPiece.getMKSingleJigsaw(DIGGER_ROAD_1, false), 1)), StructureTemplatePool.Projection.RIGID));
        Pools.register(new StructureTemplatePool(new ResourceLocation(MKNpc.MODID, "digger/diggercamp"), new ResourceLocation("empty"),
                ImmutableList.of(
                        Pair.of(MKSingleJigsawPiece.getMKSingleJigsaw(DIGGER_TENT_DBL_1, false), 1),
                        Pair.of(MKSingleJigsawPiece.getMKSingleJigsaw(DIGGER_TENT_SGL_1, false), 1),
                        Pair.of(StructurePoolElement.empty(), 2)
                ),
                StructureTemplatePool.Projection.RIGID));
    }
}
