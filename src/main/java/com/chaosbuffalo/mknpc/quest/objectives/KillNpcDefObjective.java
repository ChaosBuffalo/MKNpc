package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mkcore.serialization.attributes.IntAttribute;
import com.chaosbuffalo.mkcore.serialization.attributes.ResourceLocationAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.EmptyInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.List;
import java.util.Map;

public class KillNpcDefObjective extends QuestObjective<EmptyInstanceData> implements IKillObjectiveHandler{
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "objective.kill_npc_def");
    protected ResourceLocationAttribute npcDefinition = new ResourceLocationAttribute("npcDefinition", NpcDefinitionManager.INVALID_NPC_DEF);
    protected IntAttribute count = new IntAttribute("count", 1);

    public KillNpcDefObjective(String name, ResourceLocation definition, int count) {
        super(NAME, name, defaultDescription);
        npcDefinition.setValue(definition);
        this.count.setValue(count);
        addAttributes(npcDefinition, this.count);
    }

    public KillNpcDefObjective() {
        super(NAME, "invalid", defaultDescription);
        addAttributes(npcDefinition, this.count);
    }

    @Override
    public EmptyInstanceData generateInstanceData(Map<ResourceLocation, List<MKStructureEntry>> questStructures) {
        return new EmptyInstanceData();
    }

    @Override
    public EmptyInstanceData instanceDataFactory() {
        return new EmptyInstanceData();
    }

    @Override
    public PlayerQuestObjectiveData playerDataFactory() {
        return new PlayerQuestObjectiveData(getObjectiveName(), getDescription());
    }

    @Override
    public IFormattableTextComponent getDescription() {
        return getDescriptionWithKillCount(0);
    }

    private IFormattableTextComponent getDescriptionWithKillCount(int count){
        NpcDefinition def = NpcDefinitionManager.getDefinition(npcDefinition.getValue());
        return new TranslationTextComponent("mknpc.objective.kill_npc_def.desc", def.getDisplayName(),
                count, this.count.value());
    }

    @Override
    public PlayerQuestObjectiveData generatePlayerData(IWorldNpcData worldData, QuestData questData) {
        PlayerQuestObjectiveData newObj = playerDataFactory();
        newObj.putInt("killCount", 0);
        return newObj;
    }

    @Override
    public void onPlayerKillNpcDefEntity(PlayerEntity player, PlayerQuestObjectiveData objectiveData, NpcDefinition def,
                                         LivingDeathEvent event, QuestData questData, PlayerQuestChainInstance playerChain) {
        if (def.getDefinitionName().equals(npcDefinition.getValue()) && !isComplete(objectiveData)){
            int currentCount = objectiveData.getInt("killCount");
            currentCount++;
            objectiveData.putInt("killCount", currentCount);
            objectiveData.setDescription(getDescriptionWithKillCount(currentCount));
            player.sendMessage(getDescriptionWithKillCount(currentCount).mergeStyle(TextFormatting.GOLD), Util.DUMMY_UUID);
            if (currentCount == count.value()){
                signalCompleted(objectiveData);
            }
            playerChain.notifyDirty();

        }
    }
}
