package com.chaosbuffalo.mknpc.blocks;

import com.chaosbuffalo.mknpc.init.MKNpcBlocks;
import com.chaosbuffalo.mknpc.network.OpenMKSpawnerPacket;
import com.chaosbuffalo.mknpc.network.PacketHandler;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.jigsaw.JigsawOrientation;
import net.minecraftforge.fml.network.NetworkDirection;

import javax.annotation.Nullable;




public class MKSpawnerBlock extends Block {
    public enum MKSpawnerOrientation implements IStringSerializable {
        EAST("east", Direction.EAST),
        WEST("west", Direction.WEST),
        SOUTH("south", Direction.SOUTH),
        NORTH("north", Direction.NORTH);

        private final String name;
        private final Direction direction;

        MKSpawnerOrientation(String name, Direction direction){
            this.name = name;
            this.direction = direction;
        }

        public Direction getDirection() {
            return direction;
        }

        public float getAngleInDegrees(){
            switch (this){
                case EAST:
                    return 270;
                case WEST:
                    return 90;
                case SOUTH:
                    return 0;
                default:
                case NORTH:
                    return 180;
            }
        }

        public MKSpawnerOrientation rotate(Rotation rot){
            switch(rot){
                case NONE:
                    return this;
                case CLOCKWISE_90:
                    switch (this){
                        case EAST:
                            return SOUTH;
                        case SOUTH:
                            return WEST;
                        case WEST:
                            return NORTH;
                        case NORTH:
                            return EAST;
                    }
                case CLOCKWISE_180:
                    switch (this){
                        case EAST:
                            return WEST;
                        case SOUTH:
                            return NORTH;
                        case WEST:
                            return EAST;
                        case NORTH:
                            return SOUTH;
                    }
                case COUNTERCLOCKWISE_90:
                    switch (this){
                        case EAST:
                            return NORTH;
                        case SOUTH:
                            return EAST;
                        case WEST:
                            return SOUTH;
                        case NORTH:
                            return WEST;
                    }
            }
            return this;
        }

        @Override
        public String getString() {
            return name;
        }
    }
    public static final EnumProperty<MKSpawnerOrientation> ORIENTATION = EnumProperty.create("orientation", MKSpawnerOrientation.class);

    private final VoxelShape shape = Block.makeCuboidShape(0, 0, 0, 16.0, 1.0, 16.0);
    public static final Material SPAWNER_MATERIAL = new Material(MaterialColor.AIR, false,
            false, true, false, false, false,
            PushReaction.IGNORE);

    public MKSpawnerBlock(Properties properties) {

        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(ORIENTATION, MKSpawnerOrientation.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ORIENTATION);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        return state.with(ORIENTATION, state.get(ORIENTATION).rotate(direction));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(ORIENTATION, state.get(ORIENTATION).rotate(rot));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.with(ORIENTATION, state.get(ORIENTATION).rotate(Rotation.CLOCKWISE_180));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
                                             Hand handIn, BlockRayTraceResult hit) {
        if (handIn.equals(Hand.MAIN_HAND)){
            if (!worldIn.isRemote() && player.isCreative()){
                if (player.isSneaking()){
                    worldIn.setBlockState(pos, state.with(ORIENTATION, getNextOrientation(state.get(ORIENTATION))));
                    TileEntity spawner = worldIn.getTileEntity(pos);
                    if (spawner instanceof MKSpawnerTileEntity){
                        ((MKSpawnerTileEntity) spawner).clearSpawn();
                    }
                }
                else {
                    ((ServerPlayerEntity) player).connection.sendPacket(
                            PacketHandler.getNetworkChannel().toVanillaPacket(
                                    new OpenMKSpawnerPacket((MKSpawnerTileEntity) worldIn.getTileEntity(pos)),
                                    NetworkDirection.PLAY_TO_CLIENT));
                }

            }
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.PASS;
        }
    }

    protected MKSpawnerOrientation getNextOrientation(MKSpawnerOrientation in){
        switch (in){
            case EAST:
                return MKSpawnerOrientation.SOUTH;
            case SOUTH:
                return MKSpawnerOrientation.WEST;
            case WEST:
                return MKSpawnerOrientation.NORTH;
            case NORTH:
            default:
                return MKSpawnerOrientation.EAST;
        }
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new MKSpawnerTileEntity();
    }
}
