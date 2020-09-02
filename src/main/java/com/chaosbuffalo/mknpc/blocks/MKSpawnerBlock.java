package com.chaosbuffalo.mknpc.blocks;

import com.chaosbuffalo.mknpc.client.gui.screens.MKSpawnerScreen;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class MKSpawnerBlock extends Block {
    private final VoxelShape shape = Block.makeCuboidShape(0, 0, 0, 16.0, 1.0, 16.0);
    public static final Material SPAWNER_MATERIAL = new Material(MaterialColor.AIR, false,
            false, true, false, true, false,
            true, PushReaction.IGNORE);

    public MKSpawnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
                                             Hand handIn, BlockRayTraceResult hit) {
        if (handIn.equals(Hand.MAIN_HAND)){
            if (worldIn.isRemote()){
                Minecraft.getInstance().displayGuiScreen(
                        new MKSpawnerScreen((MKSpawnerTileEntity) worldIn.getTileEntity(pos)));
            }
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MKSpawnerTileEntity();
    }
}
