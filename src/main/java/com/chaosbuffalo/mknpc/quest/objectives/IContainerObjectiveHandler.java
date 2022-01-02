package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;

import java.util.UUID;

public interface IContainerObjectiveHandler {


    boolean onLootChest(PlayerQuestObjectiveData objectiveData, QuestData questData, UUID chestId);

    void populateChest(IChestNpcData chestData, QuestData questData);
}
