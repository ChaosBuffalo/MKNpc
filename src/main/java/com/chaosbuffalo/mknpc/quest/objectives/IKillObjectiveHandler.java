package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public interface IKillObjectiveHandler {

    boolean onPlayerKillNpcDefEntity(PlayerEntity player, PlayerQuestObjectiveData objectiveData,
                                  NpcDefinition def, LivingDeathEvent event, QuestData quest,
                                  PlayerQuestChainInstance playerChain);
}
