package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface IPlayerQuestingData extends INBTSerializable<CompoundNBT> {

    PlayerEntity getPlayer();

    Collection<PlayerQuestChainInstance> getQuestChains();

    Optional<PlayerQuestChainInstance> getQuestChain(UUID questId);

    void advanceQuest(IWorldNpcData worldHandler, QuestChainInstance questChainInstance);

    void questProgression(IWorldNpcData worldHandler, QuestChainInstance questChainInstance);

    void startQuest(IWorldNpcData worldHandler, UUID questId);

    boolean isOnQuest(UUID questId);

    Optional<String> getCurrentQuestStep(UUID questId);
}
