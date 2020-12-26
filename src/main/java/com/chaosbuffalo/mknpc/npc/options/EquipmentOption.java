package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.NpcItemChoice;
import com.chaosbuffalo.mknpc.npc.option_entries.EquipmentOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.chaosbuffalo.mknpc.utils.RandomCollection;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class EquipmentOption extends WorldPermanentOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "equipment");
    private final Map<EquipmentSlotType, List<NpcItemChoice>> itemChoices;

    public EquipmentOption(){
        super(NAME);
        itemChoices = new HashMap<>();
    }

    @Override
    protected INpcOptionEntry makeOptionEntry(NpcDefinition definition, Random random) {
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

    public EquipmentOption addItemChoice(EquipmentSlotType slot, NpcItemChoice choice){
        if (!itemChoices.containsKey(slot)){
            itemChoices.put(slot, new ArrayList<>());
        }
        itemChoices.get(slot).add(choice);
        return this;
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        Map<EquipmentSlotType, List<NpcItemChoice>> newSlots = dynamic.get("options")
                .asMap(keyD -> EquipmentSlotType.fromString(keyD.asString("error")),
                valueD -> valueD.asList(valD -> {
                    NpcItemChoice newChoice = new NpcItemChoice();
                    newChoice.deserialize(valD);
                    return newChoice;
                }));
        itemChoices.clear();
        itemChoices.putAll(newSlots);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup,
                ops.createString("options"),
                ops.createMap(itemChoices.entrySet().stream().map(entry -> Pair.of(
                        ops.createString(entry.getKey().getName()),
                        ops.createList(entry.getValue().stream()
                                .map(itemChoice -> itemChoice.serialize(ops)))))
                        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))
        ).result().orElse(sup);
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
