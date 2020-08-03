package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NpcDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final NpcDataHandler data;

    public NpcDataProvider(LivingEntity entity){
        data = new NpcDataHandler();
        data.attach(entity);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return Capabilities.NPC_DATA_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> data));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) Capabilities.NPC_DATA_CAPABILITY.getStorage().writeNBT(
                Capabilities.NPC_DATA_CAPABILITY, data, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        Capabilities.NPC_DATA_CAPABILITY.getStorage().readNBT(
                Capabilities.NPC_DATA_CAPABILITY, data, null, nbt);
    }


}
