package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.Random;
import java.util.UUID;

public class MKAbstractJigsawPiece extends PoolElementStructurePiece implements IMKStructurePiece {
    private final ResourceLocation structureName;
    private final UUID instanceId;

    public MKAbstractJigsawPiece(StructureManager templateManager, StructurePoolElement jigsawPiece, BlockPos blockPos,
                                 int groundLevelDelta, Rotation rotation, BoundingBox boundingBox,
                                 ResourceLocation structureName, UUID instanceId) {
        super(templateManager, jigsawPiece, blockPos, groundLevelDelta, rotation, boundingBox);
        this.type = MKNpcWorldGen.MK_JIGSAW_PIECE_TYPE;
        this.structureName = structureName;
        this.instanceId = instanceId;

    }

    public MKAbstractJigsawPiece(ServerLevel serverLevel, CompoundTag compoundNBT) {
        super(serverLevel, compoundNBT);
        this.type = MKNpcWorldGen.MK_JIGSAW_PIECE_TYPE;
        structureName = new ResourceLocation(compoundNBT.getString("structureName"));
        instanceId = compoundNBT.getUUID("instanceId");

    }

    @Override
    protected void addAdditionalSaveData(ServerLevel p_163121_, CompoundTag tagCompound) {
        super.addAdditionalSaveData(p_163121_, tagCompound);
        tagCompound.putUUID("instanceId", instanceId);
        tagCompound.putString("structureName", structureName.toString());
    }


    @Override
    public boolean place(WorldGenLevel seedReader, StructureFeatureManager structureManager,
                         ChunkGenerator chunkGenerator, Random random, BoundingBox boundingBox,
                         BlockPos blockPos, boolean bool) {

        if (element instanceof IMKJigsawPiece){
            return ((IMKJigsawPiece) element).mkPlace(this.structureManager, seedReader, structureManager, chunkGenerator,
                    this.position, blockPos, this.rotation, boundingBox, random, bool, this);
        } else {
            return this.element.place(this.structureManager, seedReader, structureManager, chunkGenerator,
                    this.position, blockPos, this.rotation, boundingBox, random, bool);
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
