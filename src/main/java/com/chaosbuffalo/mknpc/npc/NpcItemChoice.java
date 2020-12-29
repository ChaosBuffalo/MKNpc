package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mkcore.utils.SerializationUtils;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class NpcItemChoice implements INBTSerializable<CompoundNBT> {
    public ItemStack item;
    public double weight;
    public float dropChance;

    public NpcItemChoice(ItemStack item, double weight, float dropChance) {
        this.item = item.isEmpty() ? item : item.copy();
        this.weight = weight;
        this.dropChance = dropChance;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setDropChance(float dropChance) {
        this.dropChance = dropChance;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public NpcItemChoice(){
        this(ItemStack.EMPTY, 1.0, 0.0f);
    }

    public static void livingEquipmentAssign(LivingEntity entity, EquipmentSlotType slot, NpcItemChoice choice) {
        entity.setItemStackToSlot(slot, choice.item.copy());
        if (entity instanceof MobEntity){
            MobEntity mobEntity = (MobEntity) entity;
            mobEntity.setDropChance(slot, choice.dropChance);
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        CompoundNBT itemTag = new CompoundNBT();
        item.write(itemTag);
        tag.put("item", itemTag);
        tag.putDouble("weight", weight);
        tag.putFloat("dropChance", dropChance);
        return tag;
    }

    public <D> void deserialize(Dynamic<D> dynamic) {
        weight = dynamic.get("weight").asDouble(1.0);
        dropChance = dynamic.get("dropChance").asFloat(0.0f);
        item = dynamic.get("item").result().map(SerializationUtils::deserializeItemStack).orElse(ItemStack.EMPTY);
    }

    public <D> D serialize(DynamicOps<D> ops) {
        return ops.createMap(ImmutableMap.of(
                ops.createString("weight"), ops.createDouble(weight),
                ops.createString("dropChance"), ops.createFloat(dropChance),
                ops.createString("item"), SerializationUtils.serializeItemStack(ops, item)
        ));
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT itemTag = nbt.getCompound("item");
        item = ItemStack.read(itemTag);
        weight = nbt.getDouble("weight");
        dropChance = nbt.getFloat("dropChance");
    }
}
