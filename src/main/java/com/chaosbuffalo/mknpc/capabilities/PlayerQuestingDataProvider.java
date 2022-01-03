package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerQuestingDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final PlayerQuestingDataHandler data;

    public PlayerQuestingDataProvider(PlayerEntity player){
        data = new PlayerQuestingDataHandler();
        data.attach(player);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return NpcCapabilities.PLAYER_QUEST_DATA_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> data));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) NpcCapabilities.PLAYER_QUEST_DATA_CAPABILITY.getStorage().writeNBT(
                NpcCapabilities.PLAYER_QUEST_DATA_CAPABILITY, data, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        NpcCapabilities.PLAYER_QUEST_DATA_CAPABILITY.getStorage().readNBT(
                NpcCapabilities.PLAYER_QUEST_DATA_CAPABILITY, data, null, nbt);
    }


}