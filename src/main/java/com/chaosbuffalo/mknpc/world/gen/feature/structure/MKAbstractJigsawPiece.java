//package com.chaosbuffalo.mknpc.world.gen.feature.structure;
//
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.Rotation;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.ChunkPos;
//import net.minecraft.util.math.MutableBoundingBox;
//import net.minecraft.world.IWorld;
//import net.minecraft.world.gen.ChunkGenerator;
//import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
//import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
//import net.minecraft.world.gen.feature.structure.IStructurePieceType;
//import net.minecraft.world.gen.feature.template.TemplateManager;
//
//import java.util.Random;
//import java.util.UUID;
//
//public abstract class MKAbstractJigsawPiece extends AbstractVillagePiece implements IMKStructurePiece {
//    private final ResourceLocation structureName;
//    private final UUID instanceId;
//    protected final TemplateManager manager;
//
//    public MKAbstractJigsawPiece(IStructurePieceType structurePieceTypeIn, TemplateManager templateManagerIn,
//                                 JigsawPiece jigsawPieceIn, BlockPos posIn, int groundLevelDelta, Rotation rotation,
//                                 MutableBoundingBox boundingBox, ResourceLocation structureName, UUID instanceId) {
//        super(structurePieceTypeIn, templateManagerIn, jigsawPieceIn, posIn, groundLevelDelta, rotation, boundingBox);
//        this.structureName = structureName;
//        this.instanceId = instanceId;
//        this.manager = templateManagerIn;
//    }
//
//    public MKAbstractJigsawPiece(TemplateManager templateManagerIn, CompoundNBT nbt, IStructurePieceType structurePieceTypeIn) {
//        super(templateManagerIn, nbt, structurePieceTypeIn);
//        this.manager = templateManagerIn;
//        structureName = new ResourceLocation(nbt.getString("structureName"));
//        instanceId = UUID.fromString(nbt.getString("instanceId"));
//    }
//
//    @Override
//    public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn,
//                          MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn) {
//        if (jigsawPiece instanceof IMKJigsawPiece){
//            return ((IMKJigsawPiece)jigsawPiece).mkPlace(manager, worldIn, chunkGeneratorIn, this.pos, this.rotation,
//                    mutableBoundingBoxIn, randomIn, this);
//        } else {
//            return this.jigsawPiece.place(manager, worldIn, chunkGeneratorIn, this.pos, this.rotation,
//                    mutableBoundingBoxIn, randomIn);
//        }
//    }
//
//    @Override
//    public ResourceLocation getStructureName() {
//        return structureName;
//    }
//
//    @Override
//    public UUID getInstanceId() {
//        return instanceId;
//    }
//}
