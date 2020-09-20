package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldNpcDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final WorldNpcDataHandler data;

    public WorldNpcDataProvider(World world){
        data = new WorldNpcDataHandler();
        data.attach(world);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return NpcCapabilities.WORLD_NPC_DATA_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> data));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) NpcCapabilities.WORLD_NPC_DATA_CAPABILITY.getStorage().writeNBT(
                NpcCapabilities.WORLD_NPC_DATA_CAPABILITY, data, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        NpcCapabilities.WORLD_NPC_DATA_CAPABILITY.getStorage().readNBT(
                NpcCapabilities.WORLD_NPC_DATA_CAPABILITY, data, null, nbt);
    }


}

