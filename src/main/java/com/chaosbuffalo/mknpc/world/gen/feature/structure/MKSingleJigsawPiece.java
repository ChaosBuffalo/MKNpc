package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.world.gen.StructureUtils;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.BastionRemnantsPieces;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.*;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.chaosbuffalo.mknpc.world.gen.feature.structure.MKJigsawDeserializers.MK_SINGLE_JIGSAW_DESERIALIZER;

public class MKSingleJigsawPiece extends SingleJigsawPiece implements IMKJigsawPiece{

    public static final Codec<MKSingleJigsawPiece> codec = RecordCodecBuilder.create((builder) ->
            builder.group(func_236846_c_(), func_236844_b_(), func_236848_d_(), Codec.BOOL.fieldOf("bWaterlog")
                    .forGetter(MKSingleJigsawPiece::doWaterlog))
                    .apply(builder, MKSingleJigsawPiece::new));

    private boolean bWaterlogBlocks;

    protected MKSingleJigsawPiece(Either<ResourceLocation, Template> templateEither,
                                  Supplier<StructureProcessorList> structureProcessor,
                                  JigsawPattern.PlacementBehaviour placementBehaviour, boolean waterlogBlocks) {
        super(templateEither, structureProcessor, placementBehaviour);
        bWaterlogBlocks = waterlogBlocks;
    }

    public MKSingleJigsawPiece(Template template) {
        super(template);
    }

    public boolean doWaterlog(){
        return bWaterlogBlocks;
    }



    public Either<ResourceLocation, Template> getPieceEither(){
        return field_236839_c_;
    }



    @Override
    public boolean mkPlace(TemplateManager templateManager, ISeedReader seedReader, StructureManager structureManager,
                           ChunkGenerator chunkGenerator, BlockPos structurePos, BlockPos blockPos, Rotation rot,
                           MutableBoundingBox boundingBox, Random rand, boolean keepJigsaw, MKAbstractJigsawPiece parent) {
        Template template = this.func_236843_a_(templateManager);
        PlacementSettings placementsettings = this.func_230379_a_(rot, boundingBox, keepJigsaw);
        placementsettings.removeProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
//        placementsettings.addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
        placementsettings.field_204765_h = doWaterlog();
        if (!template.func_237146_a_(seedReader, structurePos, blockPos, placementsettings, rand, 18)) {
            return false;
        } else {
            List<Template.BlockInfo> dataMarkers = this.getDataMarkers(templateManager, structurePos, rot, false);
            PlacementSettings processSettings = placementsettings.copy();
//            processSettings.removeProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
            for(Template.BlockInfo blockinfo : Template.processBlockInfos(
                    seedReader, structurePos, blockPos, processSettings,
                    dataMarkers, template)) {
                if (boundingBox.isVecInside(blockinfo.pos)){
                    mkHandleDataMarker(seedReader, blockinfo, blockinfo.pos, rot, rand, boundingBox, parent);
                }
            }
            return true;
        }
    }

    @Override
    public IJigsawDeserializer<?> getType() {
        return MK_SINGLE_JIGSAW_DESERIALIZER;
    }

    public static Function<JigsawPattern.PlacementBehaviour, MKSingleJigsawPiece> getMKSingleJigsaw(ResourceLocation pieceName, boolean doWaterlog) {
        return (placementBehaviour) -> new MKSingleJigsawPiece(Either.left(pieceName),
                () -> ProcessorLists.EMPTY, placementBehaviour, doWaterlog);
    }
}
