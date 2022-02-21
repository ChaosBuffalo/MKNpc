package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.serialization.attributes.DoubleAttribute;
import com.chaosbuffalo.mkcore.serialization.attributes.IntAttribute;
import com.chaosbuffalo.mkcore.serialization.attributes.ResourceLocationAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.UUIDInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.List;
import java.util.Map;

public class QuestLootNpcObjective extends StructureInstanceObjective<UUIDInstanceData> implements IKillObjectiveHandler {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "objective.quest_loot_npc");
    protected ResourceLocationAttribute npcDefinition = new ResourceLocationAttribute("npcDefinition", NpcDefinitionManager.INVALID_NPC_DEF);
    protected DoubleAttribute chanceToFind = new DoubleAttribute("chance", 1.0);
    protected IntAttribute count = new IntAttribute("count", 1);
    protected IFormattableTextComponent itemDescription;
    protected static final IFormattableTextComponent defaultItemDescription = new StringTextComponent("Placeholder Item");


    public QuestLootNpcObjective(String name, ResourceLocation structure, int index, ResourceLocation npcDefinition,
                                 double chance, int count, IFormattableTextComponent itemDescription,
                                 IFormattableTextComponent... description) {
        super(NAME, name,  structure, index, description);
        this.npcDefinition.setValue(npcDefinition);
        this.chanceToFind.setValue(chance);
        this.count.setValue(count);
        this.itemDescription = itemDescription;
        addAttributes(this.npcDefinition, this.chanceToFind, this.count);
    }

    public QuestLootNpcObjective() {
        super(NAME, "invalid", defaultDescription);
        itemDescription = defaultItemDescription;
        addAttributes(npcDefinition, chanceToFind, count);
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("itemDescription"),  ops.createString(ITextComponent.Serializer.toJson(itemDescription)));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        itemDescription = ITextComponent.Serializer.getComponentFromJson(dynamic.get("itemDescription").asString()
                .resultOrPartial(MKNpc.LOGGER::error).orElseThrow(IllegalArgumentException::new));
    }

    private IFormattableTextComponent getDescriptionWithCount(int count){
        NpcDefinition def = NpcDefinitionManager.getDefinition(npcDefinition.getValue());
        return new TranslationTextComponent("mknpc.objective.quest_loot_npc.desc", itemDescription, def.getDisplayName(),
                MKAbility.INTEGER_FORMATTER.format(count), MKAbility.INTEGER_FORMATTER.format(this.count.value()));
    }

    private IFormattableTextComponent getProgressMessage(LivingEntity entity, int count){
        return new TranslationTextComponent("mknpc.objective.quest_loot_npc.progress", itemDescription, entity.getName(),
                MKAbility.INTEGER_FORMATTER.format(count), MKAbility.INTEGER_FORMATTER.format(this.count.value()));
    }

    @Override
    public boolean onPlayerKillNpcDefEntity(PlayerEntity player, PlayerQuestObjectiveData objectiveData, NpcDefinition def,
                                         LivingDeathEvent event, QuestData quest, PlayerQuestChainInstance playerChain) {
        if (!isComplete(objectiveData)){
            UUIDInstanceData objData = getInstanceData(quest);
            boolean applies = event.getEntityLiving().getCapability(NpcCapabilities.ENTITY_NPC_DATA_CAPABILITY).map(
                    x -> x.getStructureId().map(structId -> structId.equals(objData.getUuid())).orElse(false)).orElse(false)
                    && def.getDefinitionName().equals(npcDefinition.getValue());
            if (applies && player.getRNG().nextDouble() <= chanceToFind.value()){
                int currentCount = objectiveData.getInt("lootCount");
                currentCount++;
                objectiveData.putInt("lootCount", currentCount);
                objectiveData.setDescription(getDescriptionWithCount(currentCount));
                player.sendMessage(getProgressMessage(event.getEntityLiving(), currentCount)
                        .mergeStyle(TextFormatting.GOLD), Util.DUMMY_UUID);
                if (currentCount == count.value()){
                    signalCompleted(objectiveData);
                }
                playerChain.notifyDirty();
                return true;
            }
        }
        return false;
    }

    @Override
    public UUIDInstanceData generateInstanceData(Map<ResourceLocation, List<MKStructureEntry>> questStructures) {
        MKStructureEntry entry = questStructures.get(getStructureName()).get(structureIndex.value());
        return new UUIDInstanceData(entry.getStructureId());
    }

    @Override
    public UUIDInstanceData instanceDataFactory() {
        return new UUIDInstanceData();
    }


    @Override
    public PlayerQuestObjectiveData playerDataFactory() {
        return new PlayerQuestObjectiveData(getObjectiveName(), getDescription());
    }

    @Override
    public boolean isStructureRelevant(MKStructureEntry entry) {
        return structureName.getValue().equals(entry.getStructureName()) && entry.hasNotableOfType(npcDefinition.getValue());
    }

    @Override
    public PlayerQuestObjectiveData generatePlayerData(IWorldNpcData worldData, QuestData questData) {
        PlayerQuestObjectiveData newObj = playerDataFactory();
        newObj.putInt("lootCount", 0);
        return newObj;
    }
}
