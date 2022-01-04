package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mkcore.serialization.attributes.StringAttribute;
import com.chaosbuffalo.mkcore.utils.SerializationUtils;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.npc.NotableChestEntry;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.UUIDInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.*;

public class LootChestObjective extends StructureInstanceObjective<UUIDInstanceData> implements IContainerObjectiveHandler{
    protected final StringAttribute chestTag = new StringAttribute("chestTag", "invalid");
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "objective.loot_chest");
    private final List<ItemStack> itemsToAdd = new ArrayList<>();

    public LootChestObjective(String name, ResourceLocation structure, String chestTag, ITextComponent description) {
        this(name, structure, 0, chestTag, description);
    }

    public LootChestObjective(String name, ResourceLocation structure, int index, String chestTag, ITextComponent description){
        super(NAME, name, structure, index, description);
        addAttribute(this.chestTag);
        this.chestTag.setValue(chestTag);
    }

    public LootChestObjective(){
        super(NAME, "invalid", new StringTextComponent("placeholder"));
        addAttribute(this.chestTag);
    }

    public void addItemStack(ItemStack stack){
        itemsToAdd.add(stack);
    }

    @Override
    public <D> void putAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.putAdditionalData(ops, builder);
        builder.put(ops.createString("items"),
                ops.createList(itemsToAdd.stream().map(x -> SerializationUtils.serializeItemStack(ops, x)))
        );
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        List<ItemStack> dStacks = dynamic.get("items").asList(SerializationUtils::deserializeItemStack);
        itemsToAdd.clear();
        itemsToAdd.addAll(dStacks);
    }

    @Override
    public boolean isStructureRelevant(MKStructureEntry entry) {
        return structureName.getValue().equals(entry.getStructureName()) && entry.hasChestWithTag(chestTag.getValue());
    }

    @Override
    public UUIDInstanceData generateInstanceData(Map<ResourceLocation, List<MKStructureEntry>> questStructures) {
        MKStructureEntry entry = questStructures.get(getStructureName()).get(structureIndex.getValue());
        Optional<NotableChestEntry> chest = entry.getFirstChestWithTag(chestTag.getValue());
        return chest.map(x -> new UUIDInstanceData(x.getChestId())).orElse(new UUIDInstanceData());
    }

    @Override
    public UUIDInstanceData instanceDataFactory() {
        return new UUIDInstanceData();
    }

    @Override
    public PlayerQuestObjectiveData generatePlayerData(IWorldNpcData worldData, QuestData questData) {
        UUIDInstanceData objData = getInstanceData(questData);
        PlayerQuestObjectiveData newObj = playerDataFactory();
        newObj.putBlockPos("chestPos", worldData.getNotableChest(objData.getUuid()).getLocation());
        newObj.putBool("hasLooted", false);
        return newObj;
    }


    @Override
    public PlayerQuestObjectiveData playerDataFactory() {
        return new PlayerQuestObjectiveData(getObjectiveName(), getDescription());
    }

    @Override
    public boolean isComplete(PlayerQuestObjectiveData playerData) {
        return playerData.getBool("hasLooted");
    }

    @Override
    public boolean onLootChest(PlayerQuestObjectiveData objectiveData, QuestData questData, IChestNpcData chestData) {
        UUIDInstanceData objData = getInstanceData(questData);
        if (objectiveData.getBool("hasLooted")){
            return false;
        }
        if (chestData.getChestId() != null && chestData.getChestId().equals(objData.getUuid())){
            objectiveData.putBool("hasLooted", true);
            objectiveData.removeBlockPos("chestPos");
            populateChest(chestData);
            return true;
        }
        return false;
    }

    protected void populateChest(IChestNpcData chestData) {
        int index = 0;
        ChestTileEntity chest = chestData.getTileEntity();
        for (ItemStack item : itemsToAdd){
            chest.setInventorySlotContents(index, item.copy());
            index++;
        }
    }
}
