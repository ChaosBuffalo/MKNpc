package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mkcore.serialization.attributes.StringAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.WorldNpcDataHandler;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.npc.NotableChestEntry;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.UUIDInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LootChestObjective extends StructureInstanceObjective<UUIDInstanceData>{
    protected final StringAttribute chestTag = new StringAttribute("chestTag", "invalid");
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "objective.loot_chest");

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
    public PlayerQuestObjectiveData generatePlayerData(WorldNpcDataHandler worldData, QuestData questData) {
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
}
