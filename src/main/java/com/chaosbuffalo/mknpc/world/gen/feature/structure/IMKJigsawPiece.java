package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.world.gen.StructureUtils;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

//public interface IMKJigsawPiece {
//
//    boolean mkPlace(TemplateManager templateManager, IWorld world, ChunkGenerator chunkGenerator,
//                    BlockPos blockPos, Rotation rot, MutableBoundingBox boundingBox, Random rand,
//                    MKAbstractJigsawPiece parent);
//
//    default void mkHandleDataMarker(IWorld worldIn, Template.BlockInfo blockInfo, BlockPos pos, Rotation rotationIn,
//                                   Random rand, MutableBoundingBox boundingBox, MKAbstractJigsawPiece parent) {
//        StructureUtils.handleMKDataMarker(blockInfo.nbt.getString("metadata"), pos, worldIn, rand, boundingBox,
//                parent.getStructureName(), parent.getInstanceId());
//    }
//}
