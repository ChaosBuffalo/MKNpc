package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.NpcItemChoice;
import com.chaosbuffalo.mknpc.npc.option_entries.EquipmentOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.chaosbuffalo.mknpc.utils.RandomCollection;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;

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
        Map<EquipmentSlotType, List<NpcItemChoice>> newSlots = dynamic.get("slotOptions")
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
                ops.createString("slotOptions"),
                ops.createMap(itemChoices.entrySet().stream().map(entry -> Pair.of(
                        ops.createString(entry.getKey().getName()),
                        ops.createList(entry.getValue().stream()
                                .map(itemChoice -> itemChoice.serialize(ops)))))
                        .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))
        ).result().orElse(sup);
    }

}
