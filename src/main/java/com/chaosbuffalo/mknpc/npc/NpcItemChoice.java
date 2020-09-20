package com.chaosbuffalo.mknpc.npc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class NpcItemChoice implements INBTSerializable<CompoundNBT> {
    public ItemStack item;
    public double weight;
    public float dropChance;

    public NpcItemChoice(ItemStack item, double weight, float dropChance) {
        this.item = item;
        this.weight = weight;
        this.dropChance = dropChance;
    }

    public NpcItemChoice(){
        item = ItemStack.EMPTY;
        dropChance = 0.0f;
        weight = 1.0;
    }

    public NpcItemChoice(ItemStack item, double weight, int minLevel) {
        this(item, weight, .00f);
    }

    public static void livingEquipmentAssign(LivingEntity entity, EquipmentSlotType slot, NpcItemChoice choice) {
        entity.setItemStackToSlot(slot, choice.item);
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

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT itemTag = nbt.getCompound("item");
        item = ItemStack.read(itemTag);
        weight = nbt.getDouble("weight");
        dropChance = nbt.getFloat("dropChance");
    }
}
