package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.world.gen.IStructurePlaced;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IChestNpcData extends INBTSerializable<CompoundNBT>, IStructurePlaced {


    @Nullable
    UUID getChestId();

    @Nullable
    String getChestLabel();

    void attach(ChestTileEntity entity);

    ChestTileEntity getTileEntity();

    void generateChestId(String chestLabel);

    void tick();
}
