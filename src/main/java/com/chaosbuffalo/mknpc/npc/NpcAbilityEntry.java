package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.utils.SerializationUtils;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class NpcAbilityEntry implements INBTSerializable<CompoundNBT> {
    private ResourceLocation abilityName;
    private int priority;
    private double chance;

    public NpcAbilityEntry(){
        priority = 1;
        chance = 1.0;
    }

    public NpcAbilityEntry(ResourceLocation abilityName, int priority, double chance){
        this.priority = priority;
        this.abilityName = abilityName;
        this.chance = chance;
    }


    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public double getChance() {
        return chance;
    }

    public void setAbilityName(ResourceLocation abilityName) {
        this.abilityName = abilityName;
    }

    public ResourceLocation getAbilityName() {
        return abilityName;
    }

    public <D> void deserialize(Dynamic<D> dynamic) {
        abilityName = dynamic.get("abilityName").asString().result().map(ResourceLocation::new)
                .orElse(MKCoreRegistry.INVALID_ABILITY);
        chance = dynamic.get("chance").asDouble(1.0);
        priority = dynamic.get("priority").asInt(1);
    }

    public <D> D serialize(DynamicOps<D> ops) {
        return ops.createMap(ImmutableMap.of(
                ops.createString("priority"), ops.createInt(getPriority()),
                ops.createString("chance"), ops.createDouble(getChance()),
                ops.createString("abilityName"), ops.createString(getAbilityName().toString())
        ));
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("abilityName", getAbilityName().toString());
        tag.putInt("priority", getPriority());
        tag.putDouble("chance", getChance());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        abilityName = new ResourceLocation(nbt.getString("abilityName"));
        priority = nbt.getInt("priority");
        chance = nbt.getDouble("chance");
    }
}
