package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public interface ITradeObjectiveHandler {

    void onPlayerTradeSuccess(PlayerEntity player, PlayerQuestObjectiveData objectiveData,
                                     QuestData questData, PlayerQuestChainInstance playerChain, LivingEntity trader);

    @Nullable
    int[] findMatches(List<ItemStack> nonEmptyInventoryContents);
}
