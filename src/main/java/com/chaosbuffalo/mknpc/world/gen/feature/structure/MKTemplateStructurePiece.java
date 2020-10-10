package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.IglooPieces;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public abstract class MKTemplateStructurePiece extends TemplateStructurePiece {

    public MKTemplateStructurePiece(IStructurePieceType structurePieceTypeIn, int componentTypeIn) {
        super(structurePieceTypeIn, componentTypeIn);
    }

    public MKTemplateStructurePiece(IStructurePieceType structurePieceTypeIn, CompoundNBT nbt){
        super(structurePieceTypeIn, nbt);
    }


    @Override
    protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
        if ("mkspawner".equals(function)) {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            TileEntity tileentity = worldIn.getTileEntity(pos.down());
            if (tileentity instanceof MKSpawnerTileEntity) {
                ((MKSpawnerTileEntity)tileentity).regenerateSpawnID();
            }
        }
    }
}
