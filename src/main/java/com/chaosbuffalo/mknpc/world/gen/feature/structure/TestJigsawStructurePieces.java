package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.jigsaw.*;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.UUID;

public class TestJigsawStructurePieces {

    private static final ResourceLocation DIGGER_BIG_1 = new ResourceLocation(MKNpc.MODID, "diggerbig1");
    private static final ResourceLocation DIGGER_ROAD_1 = new ResourceLocation(MKNpc.MODID, "diggerrd1");
    private static final ResourceLocation DIGGER_TENT_DBL_1 = new ResourceLocation(MKNpc.MODID, "diggertentdbl1");
    private static final ResourceLocation DIGGER_TENT_SGL_1 = new ResourceLocation(MKNpc.MODID, "diggertendsgl1");

    private static final int GEN_DEPTH = 7;

//    public static void getPieces(MKStructurePieceArgs args, ChunkPosConfig config){
//        JigsawManager.addPieces(
//                DIGGER_BIG_1,
//                GEN_DEPTH,(templateManager, piece, pos, groundLevel, rot, bbox) ->
//                        new TestJigsawPiece(templateManager, piece, pos, groundLevel, rot, bbox,
//                                args.structure.getRegistryName(), args.structureId),
//                args.generator, args.templateManager, args.blockPos, args.componentsOut, args.random);
//
//    }
//
//    static {
//        JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation("pillager_outpost/towers"),
//                new ResourceLocation("empty"), ImmutableList.of(Pair.of(
//                        new MKSingleJigsawPiece(DIGGER_BIG_1.toString(), ImmutableList.of(),
//                                JigsawPattern.PlacementBehaviour.RIGID), 1)),
//                JigsawPattern.PlacementBehaviour.RIGID));
//        JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation("pillager_outpost/towers"),
//                new ResourceLocation("empty"), ImmutableList.of(Pair.of(
//                new MKSingleJigsawPiece(DIGGER_BIG_1.toString(), ImmutableList.of(),
//                        JigsawPattern.PlacementBehaviour.RIGID), 1)),
//                JigsawPattern.PlacementBehaviour.RIGID));
//        JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation("pillager_outpost/towers"),
//                new ResourceLocation("empty"), ImmutableList.of(Pair.of(
//                new MKSingleJigsawPiece(DIGGER_BIG_1.toString(), ImmutableList.of(),
//                        JigsawPattern.PlacementBehaviour.RIGID), 1)),
//                JigsawPattern.PlacementBehaviour.RIGID));
//        JigsawManager.REGISTRY.register(new JigsawPattern(new ResourceLocation("pillager_outpost/towers"),
//                new ResourceLocation("empty"), ImmutableList.of(Pair.of(
//                new MKSingleJigsawPiece(DIGGER_BIG_1.toString(), ImmutableList.of(),
//                        JigsawPattern.PlacementBehaviour.RIGID), 1)),
//                JigsawPattern.PlacementBehaviour.RIGID));
//    }

//    public static class TestJigsawPiece extends MKAbstractJigsawPiece {
//
//        public TestJigsawPiece(TemplateManager templateManagerIn,
//                               JigsawPiece jigsawPieceIn, BlockPos posIn, int groundLevelDelta, Rotation rotation,
//                               MutableBoundingBox boundingBox, ResourceLocation structureName, UUID instanceId) {
//            super(MKNpcWorldGen.TEST_JIGSAW_PIECE_TYPE, templateManagerIn, jigsawPieceIn, posIn, groundLevelDelta, rotation,
//                    boundingBox, structureName, instanceId);
//        }
//
//        public TestJigsawPiece(TemplateManager templateManagerIn, CompoundNBT nbt, IStructurePieceType structurePieceTypeIn) {
//            super(templateManagerIn, nbt, structurePieceTypeIn);
//        }
//    }
}
