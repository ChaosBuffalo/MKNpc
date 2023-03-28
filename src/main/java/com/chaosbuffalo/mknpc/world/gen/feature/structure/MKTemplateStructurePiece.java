package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Random;
import java.util.UUID;

//public abstract class MKTemplateStructurePiece extends TemplateStructurePiece implements IMKStructurePiece {
//    private final ResourceLocation structureName;
//    private final UUID instanceId;
//
//    public MKTemplateStructurePiece(StructurePieceType structurePieceTypeIn, int componentTypeIn,
//                                    ResourceLocation structureName, UUID instanceId) {
//        super(structurePieceTypeIn, componentTypeIn);
//        this.structureName = structureName;
//        this.instanceId = instanceId;
//    }
//
//    public MKTemplateStructurePiece(StructurePieceType structurePieceTypeIn, CompoundTag nbt){
//        super(structurePieceTypeIn, nbt);
//        structureName = new ResourceLocation(nbt.getString("structureName"));
//        instanceId = UUID.fromString(nbt.getString("instanceId"));
//    }
//
//    @Override
//    public UUID getInstanceId() {
//        return instanceId;
//    }
//
//    @Override
//    public ResourceLocation getStructureName() {
//        return structureName;
//    }
//
//    @Override
//    protected void addAdditionalSaveData(CompoundTag tagCompound) {
//        super.addAdditionalSaveData(tagCompound);
//        tagCompound.putString("structureName", structureName.toString());
//        tagCompound.putString("instanceId", instanceId.toString());
//    }
//
//    @Override
//    protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, Random rand, BoundingBox sbb) {
//        handleMKDataMarker(function, pos, worldIn, rand, sbb);
//    }
//
//    @Override
//    protected void setup(StructureTemplate templateIn, BlockPos pos, StructurePlaceSettings settings) {
//        settings.keepLiquids = false;
//        super.setup(templateIn, pos, settings);
//    }
//}
