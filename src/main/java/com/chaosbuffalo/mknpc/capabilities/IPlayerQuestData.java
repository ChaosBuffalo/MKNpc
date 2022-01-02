package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.UUID;

public interface IPlayerQuestData extends INBTSerializable<CompoundNBT> {

    PlayerEntity getPlayer();

    Collection<PlayerQuestChainInstance> getQuestChains();

    void startQuest(IWorldNpcData worldHandler, UUID questId);
}
