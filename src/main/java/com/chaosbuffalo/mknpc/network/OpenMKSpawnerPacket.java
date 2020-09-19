package com.chaosbuffalo.mknpc.network;

import com.chaosbuffalo.mknpc.client.gui.screens.MKSpawnerScreen;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenMKSpawnerPacket extends SetSpawnListPacket {

    public OpenMKSpawnerPacket(MKSpawnerTileEntity entity) {
        super(entity);
    }

    public OpenMKSpawnerPacket(PacketBuffer buffer){
        super(buffer);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                World world = Minecraft.getInstance().player.getEntityWorld();
                TileEntity tileEntity = world.getTileEntity(tileEntityLoc);
                if (tileEntity instanceof MKSpawnerTileEntity){
                    MKSpawnerTileEntity spawner = (MKSpawnerTileEntity) tileEntity;
                    setSpawnerFromPacket(spawner);
                    Minecraft.getInstance().displayGuiScreen(new MKSpawnerScreen(spawner));
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
