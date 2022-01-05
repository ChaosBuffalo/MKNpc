package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import net.minecraft.entity.player.PlayerEntity;


public interface IContainerObjectiveHandler {

    boolean onLootChest(PlayerEntity player, PlayerQuestObjectiveData objectiveData, QuestData questData, IChestNpcData chestData);

}
