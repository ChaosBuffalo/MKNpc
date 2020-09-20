package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.NpcItemChoice;
import com.chaosbuffalo.mknpc.npc.option_entries.EquipmentOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.chaosbuffalo.mknpc.utils.RandomCollection;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentOption extends WorldPermanentOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "equipment");
    private final Map<EquipmentSlotType, List<NpcItemChoice>> itemChoices;

    public EquipmentOption(){
        super(NAME);
        itemChoices = new HashMap<>();
    }

    @Override
    protected INpcOptionEntry makeOptionEntry(NpcDefinition definition, Entity entity) {
        EquipmentOptionEntry equipmentEntry = new EquipmentOptionEntry();
        for (Map.Entry<EquipmentSlotType, List<NpcItemChoice>> entry : itemChoices.entrySet()){
            RandomCollection<NpcItemChoice> slotChoices = new RandomCollection<>();
            for (NpcItemChoice choice : entry.getValue()){
                slotChoices.add(choice.weight, choice);
            }
            equipmentEntry.setSlotChoice(entry.getKey(), slotChoices.next());
        }
        return equipmentEntry;
    }

    private void addItemChoice(EquipmentSlotType slot, NpcItemChoice choice){
        if (!itemChoices.containsKey(slot)){
            itemChoices.put(slot, new ArrayList<>());
        }
        itemChoices.get(slot).add(choice);
    }

    @Override
    public void fromJson(Gson gson, JsonObject object) {
        JsonObject equipmentObject = object.getAsJsonObject(NAME.toString());
        for (Map.Entry<String, JsonElement> entry : equipmentObject.entrySet()){
            EquipmentSlotType slot = EquipmentSlotType.fromString(entry.getKey());
            JsonArray itemChoiceArray = entry.getValue().getAsJsonArray();
            for (JsonElement itemChoiceEle : itemChoiceArray){
                JsonObject itemChoiceObj = itemChoiceEle.getAsJsonObject();
                float dropChance = .00f;
                if (itemChoiceObj.has("dropChance")) {
                    dropChance = itemChoiceObj.get("dropChance").getAsFloat();
                }
                String itemName = itemChoiceObj.get("item").getAsString();
                Item item = Items.AIR;
                if (!itemName.equals("EMPTY")) {
                    item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
                }
                if (item.equals(Items.AIR) && !itemName.equals("EMPTY")) {
                    MKNpc.LOGGER.info("Failed to load item for {}",
                            itemChoiceObj.get("item").getAsString());
                    continue;
                } else {
                    ItemStack itemStack;
                    if (item.equals(Items.AIR)) {
                        itemStack = ItemStack.EMPTY;
                    } else {
                        itemStack = new ItemStack(item, 1);
                    }
                    NpcItemChoice choice = new NpcItemChoice(itemStack,
                            itemChoiceObj.get("weight").getAsDouble(),
                            dropChance);
                    addItemChoice(slot, choice);
                }
            }
        }
    }
}
