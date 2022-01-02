package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerQuestData extends INBTSerializable<CompoundNBT> {

    PlayerEntity getPlayer();
}
