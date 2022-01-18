package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;

public class PlayerQuestingDataProvider extends NpcCapabilities.Provider<PlayerEntity, IPlayerQuestingData> {

    public PlayerQuestingDataProvider(PlayerEntity attached) {
        super(attached);
    }

    @Override
    IPlayerQuestingData makeData(PlayerEntity attached) {
        return new PlayerQuestingDataHandler(attached);
    }

    @Override
    Capability<IPlayerQuestingData> getCapability() {
        return NpcCapabilities.PLAYER_QUEST_DATA_CAPABILITY;
    }
}