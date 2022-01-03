package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;


public interface IContainerObjectiveHandler {

    boolean onLootChest(PlayerQuestObjectiveData objectiveData, QuestData questData, IChestNpcData chestData);

}
