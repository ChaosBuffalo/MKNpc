package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;
import java.util.UUID;

public class MKAbstractJigsawPiece extends AbstractVillagePiece implements IMKStructurePiece {
    private final ResourceLocation structureName;
    private final UUID instanceId;
    protected final TemplateManager manager;

    public MKAbstractJigsawPiece(TemplateManager templateManager, JigsawPiece jigsawPiece, BlockPos blockPos,
                                 int groundLevelDelta, Rotation rotation, MutableBoundingBox boundingBox,
                                 ResourceLocation structureName, UUID instanceId) {
        super(templateManager, jigsawPiece, blockPos, groundLevelDelta, rotation, boundingBox);
        this.structurePieceType = MKNpcWorldGen.MK_JIGSAW_PIECE_TYPE;
        this.structureName = structureName;
        this.instanceId = instanceId;
        this.manager = templateManager;

    }

    public MKAbstractJigsawPiece(TemplateManager templateManager, CompoundNBT compoundNBT) {
        super(templateManager, compoundNBT);
        this.structurePieceType = MKNpcWorldGen.MK_JIGSAW_PIECE_TYPE;
        this.manager = templateManager;
        structureName = new ResourceLocation(compoundNBT.getString("structureName"));
        instanceId = compoundNBT.getUniqueId("instanceId");

    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putUniqueId("instanceId", instanceId);
        tagCompound.putString("structureName", structureName.toString());
    }

    @Override
    public boolean func_237001_a_(ISeedReader seedReader, StructureManager structureManager,
                                  ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox,
                                  BlockPos blockPos, boolean bool) {

        if (jigsawPiece instanceof IMKJigsawPiece){
            return ((IMKJigsawPiece) jigsawPiece).mkPlace(manager, seedReader, structureManager, chunkGenerator,
                    this.pos, blockPos, this.rotation, boundingBox, random, bool, this);
        } else {
            return this.jigsawPiece.func_230378_a_(manager, seedReader, structureManager, chunkGenerator,
                    this.pos, blockPos, this.rotation, boundingBox, random, bool);
        }
    }

    @Override
    public ResourceLocation getStructureName() {
        return structureName;
    }

    @Override
    public UUID getInstanceId() {
        return instanceId;
    }
}
