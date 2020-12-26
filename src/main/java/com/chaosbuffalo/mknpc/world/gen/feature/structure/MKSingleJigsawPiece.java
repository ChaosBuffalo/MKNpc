//package com.chaosbuffalo.mknpc.world.gen.feature.structure;
//
//import com.chaosbuffalo.mknpc.world.gen.StructureUtils;
//import com.mojang.datafixers.Dynamic;
//import net.minecraft.util.Rotation;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.MutableBoundingBox;
//import net.minecraft.world.IWorld;
//import net.minecraft.world.gen.ChunkGenerator;
//import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
//import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
//import net.minecraft.world.gen.feature.template.PlacementSettings;
//import net.minecraft.world.gen.feature.template.StructureProcessor;
//import net.minecraft.world.gen.feature.template.Template;
//import net.minecraft.world.gen.feature.template.TemplateManager;
//
//import java.util.List;
//import java.util.Random;
//
//public class MKSingleJigsawPiece extends SingleJigsawPiece implements IMKJigsawPiece{
//    public MKSingleJigsawPiece(String location, List<StructureProcessor> processors,
//                               JigsawPattern.PlacementBehaviour placementBehaviour) {
//        super(location, processors, placementBehaviour);
//    }
//
//    public MKSingleJigsawPiece(Dynamic<?> dynamic) {
//        super(dynamic);
//    }
//
//    @Override
//    public boolean mkPlace(TemplateManager templateManager, IWorld world, ChunkGenerator<?> chunkGenerator,
//                           BlockPos blockPos, Rotation rot, MutableBoundingBox boundingBox, Random rand,
//                           MKAbstractJigsawPiece parent) {
//        Template template = templateManager.getTemplateDefaulted(this.location);
//        PlacementSettings placementsettings = this.createPlacementSettings(rot, boundingBox);
//        if (!template.addBlocksToWorld(world, blockPos, placementsettings, 18)) {
//            return false;
//        } else {
//            for(Template.BlockInfo blockInfo : Template.processBlockInfos(template, world, blockPos,
//                    placementsettings, this.getDataMarkers(templateManager, blockPos, rot, false))) {
//                mkHandleDataMarker(world, blockInfo, blockPos, rot, rand, boundingBox, parent);
//            }
//            return true;
//        }
//    }
//}
