package com.chaosbuffalo.mknpc.npc.entries;

import com.chaosbuffalo.mkweapons.items.randomization.LootTierManager;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlotManager;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;

public class LootOptionEntry {

    public ResourceLocation lootSlotName;
    public ResourceLocation lootTierName;
    public ResourceLocation templateName;
    public double weight;

    public LootOptionEntry(ResourceLocation lootSlotName, ResourceLocation lootTierName, ResourceLocation templateName, double weight){
        this.lootSlotName = lootSlotName;
        this.lootTierName = lootTierName;
        this.templateName = templateName;
        this.weight = weight;
    }

    public LootOptionEntry(){
        this(LootSlotManager.INVALID_LOOT_SLOT, LootTierManager.INVALID_LOOT_TIER,
                LootTierManager.INVALID_RANDOMIZATION_TEMPLATE, 1.0);
    }

    public <D> void deserialize(Dynamic<D> dynamic){
        lootSlotName = dynamic.get("lootSlotName").asString().result().map(ResourceLocation::new)
                .orElse(LootSlotManager.INVALID_LOOT_SLOT);
        lootTierName = dynamic.get("lootSlotTier").asString().result().map(ResourceLocation::new)
                .orElse(LootTierManager.INVALID_LOOT_TIER);
        templateName = dynamic.get("templateName").asString().result().map(ResourceLocation::new)
                .orElse(LootTierManager.INVALID_RANDOMIZATION_TEMPLATE);
        weight = dynamic.get("weight").asDouble(1.0);
    }

    public boolean isValidConfiguration(){
        return !lootSlotName.equals(LootSlotManager.INVALID_LOOT_SLOT) &&
                !lootTierName.equals(LootTierManager.INVALID_LOOT_TIER);
    }

    public boolean hasTemplate(){
        return !templateName.equals(LootTierManager.INVALID_RANDOMIZATION_TEMPLATE);
    }

    public <D> D serialize(DynamicOps<D> ops) {
        ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
        builder.put(ops.createString("lootSlotName"), ops.createString(lootSlotName.toString()));
        builder.put(ops.createString("lootSlotTier"), ops.createString(lootTierName.toString()));
        builder.put(ops.createString("templateName"), ops.createString(templateName.toString()));
        builder.put(ops.createString("weight"), ops.createDouble(weight));
        return ops.createMap(builder.build());
    }

}
