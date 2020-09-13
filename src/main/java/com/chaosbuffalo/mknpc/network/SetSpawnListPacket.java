package com.chaosbuffalo.mknpc.network;

import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.spawn.SpawnList;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SetSpawnListPacket {
    protected final BlockPos tileEntityLoc;
    protected final SpawnList spawnList;
    protected final int spawnTime;

    public SetSpawnListPacket(MKSpawnerTileEntity entity){
        tileEntityLoc = entity.getPos();
        spawnList = entity.getSpawnList();
        spawnTime = entity.getRespawnTime();
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeBlockPos(tileEntityLoc);
        buffer.writeInt(spawnTime);
        buffer.writeCompoundTag(spawnList.serializeNBT());
    }

    public SetSpawnListPacket(PacketBuffer buffer){
        tileEntityLoc = buffer.readBlockPos();
        spawnTime = buffer.readInt();
        spawnList = new SpawnList();
        CompoundNBT tag = buffer.readCompoundTag();
        if (tag != null){
            spawnList.deserializeNBT(tag);
        }
    }

    protected void setSpawnerFromPacket(MKSpawnerTileEntity spawner){
        spawner.getSpawnList().copyList(spawnList);
        spawner.setRespawnTime(spawnTime);
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
                MKSpawnerTileEntity spawner = (MKSpawnerTileEntity) tileEntity;
                setSpawnerFromPacket(spawner);
            }
        });
        ctx.setPacketHandled(true);
    }
}
