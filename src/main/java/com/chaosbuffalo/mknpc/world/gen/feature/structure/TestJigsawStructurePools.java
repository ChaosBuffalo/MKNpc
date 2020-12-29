package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.MKNpc;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.PillagerOutpostPools;
import net.minecraft.world.gen.feature.template.ProcessorLists;

public class TestJigsawStructurePools {

    private static final ResourceLocation DIGGER_BIG_1 = new ResourceLocation(MKNpc.MODID, "digger/diggerbig1");
    private static final ResourceLocation DIGGER_ROAD_1 = new ResourceLocation(MKNpc.MODID, "digger/diggerrd1");
    private static final ResourceLocation DIGGER_TENT_DBL_1 = new ResourceLocation(MKNpc.MODID, "digger/diggertentdbl1");
    private static final ResourceLocation DIGGER_TENT_SGL_1 = new ResourceLocation(MKNpc.MODID, "digger/diggertentsgl1");

    public static final int GEN_DEPTH = 7;

    public static final JigsawPattern BASE_PATTERN = JigsawPatternRegistry.func_244094_a(
            new JigsawPattern(new ResourceLocation(MKNpc.MODID, "digger/diggerbase"),
                    new ResourceLocation("empty"), ImmutableList.of(
                            Pair.of(MKSingleJigsawPiece.getMKSingleJigsaw(DIGGER_BIG_1), 1)), JigsawPattern.PlacementBehaviour.RIGID));

    public static void registerPatterns() {
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation(MKNpc.MODID, "digger/diggerroad"), new ResourceLocation("empty"),
                ImmutableList.of(Pair.of(MKSingleJigsawPiece.getMKSingleJigsaw(DIGGER_ROAD_1), 1)), JigsawPattern.PlacementBehaviour.RIGID));
        JigsawPatternRegistry.func_244094_a(new JigsawPattern(new ResourceLocation(MKNpc.MODID, "digger/diggercamp"), new ResourceLocation("empty"),
                ImmutableList.of(
                        Pair.of(MKSingleJigsawPiece.getMKSingleJigsaw(DIGGER_TENT_DBL_1), 1),
                        Pair.of(MKSingleJigsawPiece.getMKSingleJigsaw(DIGGER_TENT_SGL_1), 1),
                        Pair.of(JigsawPiece.func_242864_g(), 2)
                ),
                JigsawPattern.PlacementBehaviour.RIGID));
    }
}
