package com.chaosbuffalo.mknpc.world.gen;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.Random;

public class MKJigsawManager {

    public static void func_242837_a(DynamicRegistries p_242837_0_, VillageConfig p_242837_1_,
                                     JigsawManager.IPieceFactory p_242837_2_, ChunkGenerator p_242837_3_,
                                     TemplateManager p_242837_4_, BlockPos p_242837_5_,
                                     List<? super AbstractVillagePiece> p_242837_6_, Random p_242837_7_,
                                     boolean p_242837_8_, boolean p_242837_9_) {
        Structure.init();
        MutableRegistry<JigsawPattern> mutableregistry = p_242837_0_.getRegistry(Registry.JIGSAW_POOL_KEY);
        Rotation rotation = Rotation.randomRotation(p_242837_7_);
        JigsawPattern jigsawpattern = p_242837_1_.func_242810_c().get();
        JigsawPiece jigsawpiece = jigsawpattern.getRandomPiece(p_242837_7_);
        AbstractVillagePiece abstractvillagepiece = p_242837_2_.create(p_242837_4_, jigsawpiece, p_242837_5_, jigsawpiece.getGroundLevelDelta(), rotation, jigsawpiece.getBoundingBox(p_242837_4_, p_242837_5_, rotation));
        MutableBoundingBox mutableboundingbox = abstractvillagepiece.getBoundingBox();
        int i = (mutableboundingbox.maxX + mutableboundingbox.minX) / 2;
        int j = (mutableboundingbox.maxZ + mutableboundingbox.minZ) / 2;
        int k;
        if (p_242837_9_) {
            k = p_242837_5_.getY() + p_242837_3_.getNoiseHeight(i, j, Heightmap.Type.WORLD_SURFACE_WG);
        } else {
            k = p_242837_5_.getY();
        }

        int l = mutableboundingbox.minY + abstractvillagepiece.getGroundLevelDelta();
        abstractvillagepiece.offset(0, k - l, 0);
        p_242837_6_.add(abstractvillagepiece);
        if (p_242837_1_.func_236534_a_() > 0) {
            int i1 = 80;
            AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)(i - 80), (double)(k - 80), (double)(j - 80), (double)(i + 80 + 1), (double)(k + 80 + 1), (double)(j + 80 + 1));
            JigsawManager.Assembler jigsawmanager$assembler = new JigsawManager.Assembler(mutableregistry, p_242837_1_.func_236534_a_(), p_242837_2_, p_242837_3_, p_242837_4_, p_242837_6_, p_242837_7_);
            jigsawmanager$assembler.availablePieces.addLast(new JigsawManager.Entry(abstractvillagepiece, new MutableObject<>(VoxelShapes.combineAndSimplify(VoxelShapes.create(axisalignedbb), VoxelShapes.create(AxisAlignedBB.toImmutable(mutableboundingbox)), IBooleanFunction.ONLY_FIRST)), k + 80, 0));

            while(!jigsawmanager$assembler.availablePieces.isEmpty()) {
                JigsawManager.Entry jigsawmanager$entry = jigsawmanager$assembler.availablePieces.removeFirst();
                jigsawmanager$assembler.func_236831_a_(jigsawmanager$entry.villagePiece, jigsawmanager$entry.free, jigsawmanager$entry.boundsTop, jigsawmanager$entry.depth, p_242837_8_);
            }

        }
    }

}
