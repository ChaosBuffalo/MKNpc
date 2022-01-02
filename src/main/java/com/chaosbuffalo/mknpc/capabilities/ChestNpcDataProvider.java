package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChestNpcDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final ChestNpcDataHandler data;

    public ChestNpcDataProvider(ChestTileEntity entity){
        data = new ChestNpcDataHandler();
        data.attach(entity);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return NpcCapabilities.CHEST_NPC_DATA_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> data));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) NpcCapabilities.CHEST_NPC_DATA_CAPABILITY.getStorage().writeNBT(
                NpcCapabilities.CHEST_NPC_DATA_CAPABILITY, data, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        NpcCapabilities.CHEST_NPC_DATA_CAPABILITY.getStorage().readNBT(
                NpcCapabilities.CHEST_NPC_DATA_CAPABILITY, data, null, nbt);
    }


}
