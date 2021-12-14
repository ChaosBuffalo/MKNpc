package com.chaosbuffalo.mknpc.network;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.spawn.SpawnList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FinalizeMKSpawnerPacket {
    protected final BlockPos tileEntityLoc;


    public FinalizeMKSpawnerPacket(MKSpawnerTileEntity entity){
        tileEntityLoc = entity.getPos();
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeBlockPos(tileEntityLoc);
    }

    public FinalizeMKSpawnerPacket(PacketBuffer buffer){
        tileEntityLoc = buffer.readBlockPos();
    }


    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity entity = ctx.getSender();
            if (entity == null || !entity.isCreative()) {
                return;
            }
            TileEntity tileEntity = entity.getServerWorld().getTileEntity(tileEntityLoc);
            if (tileEntity instanceof MKSpawnerTileEntity){
                BlockState dataState = Blocks.STRUCTURE_BLOCK.getStateForPlacement(null);
                if (dataState != null){
                    entity.getServerWorld().setBlockState(tileEntityLoc.up(), dataState, 3);
                    TileEntity other = entity.getServerWorld().getTileEntity(tileEntityLoc.up());
                    if (other instanceof StructureBlockTileEntity){
                        ((StructureBlockTileEntity) other).setMetadata("mkspawner");
                    }
                }
                ((MKSpawnerTileEntity) tileEntity).clearSpawn();
            }
        });
        ctx.setPacketHandled(true);
    }
}
