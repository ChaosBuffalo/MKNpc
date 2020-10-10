package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

import static com.chaosbuffalo.mknpc.init.MKNpcWorldGen.TEST_PIECE_TYPE;

public class TestStructurePieces {

    private static final ResourceLocation PIECE_ONE = new ResourceLocation(MKNpc.MODID, "test");



    public static void getPieces(TemplateManager templateManager, BlockPos blockPos, Rotation rotation,
                                 List<StructurePiece> pieceList, Random random, SingleChunkConfig config){
        pieceList.add(new Piece(templateManager, PIECE_ONE, blockPos, rotation));
    }


    public static class Piece extends MKTemplateStructurePiece {
        private final ResourceLocation loc;
        private final Rotation rotation;


        public Piece(TemplateManager templateManager, ResourceLocation loc,
                     BlockPos blockPos, Rotation rotation) {
            super(TEST_PIECE_TYPE, 0);
            this.loc = loc;
            this.rotation = rotation;
            this.templatePosition = blockPos;
            setManager(templateManager);
        }

        public Piece(TemplateManager templateManager, CompoundNBT nbt){
            super(TEST_PIECE_TYPE, nbt);
            this.loc = new ResourceLocation(nbt.getString("template"));
            this.rotation = Rotation.valueOf(nbt.getString("rot"));
            setManager(templateManager);
        }

        //this is actually writeAdditional
        protected void readAdditional(CompoundNBT tagCompound) {
            super.readAdditional(tagCompound);
            tagCompound.putString("template", this.loc.toString());
            tagCompound.putString("rot", this.rotation.name());
        }

        @Override
        public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn,
                              MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn) {
            int x = chunkPosIn.x * 16;
            int z = chunkPosIn.z * 16;
            int y = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG,  x, z);
            this.templatePosition = new BlockPos(x, y, z);
            boolean didGen = super.create(worldIn, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn);
            return didGen;
        }

        private void setManager(TemplateManager manager) {
            Template template = manager.getTemplateDefaulted(loc);
            PlacementSettings placementsettings = (new PlacementSettings())
                    .setRotation(rotation).setMirror(Mirror.NONE)
                    .setCenterOffset(new BlockPos(5, 0, 4))
                    .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
            this.setup(template, this.templatePosition, placementsettings);
        }
    }
}
