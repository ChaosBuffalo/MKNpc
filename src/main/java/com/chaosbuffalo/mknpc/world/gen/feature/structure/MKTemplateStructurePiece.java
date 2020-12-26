package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;

import java.util.Random;
import java.util.UUID;

public abstract class MKTemplateStructurePiece extends TemplateStructurePiece {
    private final ResourceLocation structureName;
    private final UUID instanceId;

    public MKTemplateStructurePiece(IStructurePieceType structurePieceTypeIn, int componentTypeIn,
                                    ResourceLocation structureName, UUID instanceId) {
        super(structurePieceTypeIn, componentTypeIn);
        this.structureName = structureName;
        this.instanceId = instanceId;
    }

    public MKTemplateStructurePiece(IStructurePieceType structurePieceTypeIn, CompoundNBT nbt){
        super(structurePieceTypeIn, nbt);
        structureName = new ResourceLocation(nbt.getString("structureName"));
        instanceId = UUID.fromString(nbt.getString("instanceId"));
    }

    public UUID getInstanceId() {
        return instanceId;
    }

    public ResourceLocation getStructureName() {
        return structureName;
    }

    @Override
    protected void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putString("structureName", structureName.toString());
        tagCompound.putString("instanceId", instanceId.toString());
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {
        if (function.equals("mkspawner")) {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            TileEntity tileentity = worldIn.getTileEntity(pos.down());
            if (tileentity instanceof MKSpawnerTileEntity) {
                MKSpawnerTileEntity spawner = (MKSpawnerTileEntity) tileentity;
                spawner.regenerateSpawnID();
                spawner.setStructureName(structureName);
                spawner.setStructureId(instanceId);
            }
        }
        if (function.equals("mk_spawn_point")){
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            if (worldIn instanceof WorldGenRegion){
                MKNpc.LOGGER.info("Setting spawn point");
                worldIn.getWorld().setSpawnPoint(pos);
            }
        }

    }
}
