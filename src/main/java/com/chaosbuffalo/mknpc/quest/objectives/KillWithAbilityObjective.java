package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.core.damage.MKDamageSource;
import com.chaosbuffalo.mkcore.serialization.attributes.IntAttribute;
import com.chaosbuffalo.mkcore.serialization.attributes.RegistryEntryAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.EmptyInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KillWithAbilityObjective extends QuestObjective<EmptyInstanceData> implements IKillObjectiveHandler{
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "objective.kill_w_ability");
    protected RegistryEntryAttribute<MKAbility> ability = new RegistryEntryAttribute<>("ability", MKCoreRegistry.ABILITIES, MKCoreRegistry.INVALID_ABILITY);
    protected IntAttribute count = new IntAttribute("count", 1);

    public KillWithAbilityObjective(String name, MKAbility ability, int count) {
        super(NAME, name, defaultDescription);
        this.ability.setValue(ability.getAbilityId());
        this.count.setValue(count);
        addAttributes(this.ability, this.count);
    }

    public KillWithAbilityObjective() {
        super(NAME, "invalid", defaultDescription);
        addAttributes(this.ability, this.count);
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
    public List<IFormattableTextComponent> getDescription() {
        return Collections.singletonList(getDescriptionWithKillCount(0));
    }

    private IFormattableTextComponent getDescriptionWithKillCount(int count){
        return this.ability.resolve().map(
                x -> new TranslationTextComponent("mknpc.objective.kill_w_ability.desc", x.getAbilityName(),
                        count, this.count.value()))
                .orElse(new TranslationTextComponent("mknpc.objective.kill_w_ability.desc",
                        new StringTextComponent("Ability Not Found"), count, this.count.value()));
    }

    @Override
    public PlayerQuestObjectiveData generatePlayerData(IWorldNpcData worldData, QuestData questData) {
        PlayerQuestObjectiveData newObj = playerDataFactory();
        newObj.putInt("killCount", 0);
        return newObj;
    }

    @Override
    public boolean onPlayerKillNpcDefEntity(PlayerEntity player, PlayerQuestObjectiveData objectiveData, NpcDefinition def,
                                            LivingDeathEvent event, QuestData questData, PlayerQuestChainInstance playerChain) {
        if (event.getSource() instanceof MKDamageSource.AbilityDamage && !isComplete(objectiveData)){
            MKDamageSource.AbilityDamage src = (MKDamageSource.AbilityDamage) event.getSource();
            int currentCount = objectiveData.getInt("killCount");
            if (src.getAbilityId() != null && src.getAbilityId().equals(ability.getValue())){
                currentCount++;
                objectiveData.putInt("killCount", currentCount);
                objectiveData.setDescription(getDescriptionWithKillCount(currentCount));
                player.sendMessage(getDescriptionWithKillCount(currentCount).mergeStyle(TextFormatting.GOLD), Util.DUMMY_UUID);
                if (currentCount == count.value()){
                    signalCompleted(objectiveData);
                }
                playerChain.notifyDirty();
                return true;
            }
        }
        return false;
    }
}
