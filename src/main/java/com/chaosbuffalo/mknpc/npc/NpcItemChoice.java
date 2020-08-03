package com.chaosbuffalo.mknpc.npc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class NpcItemChoice {
    public final ItemStack item;
    public final double weight;
    public final float dropChance;

    public NpcItemChoice(ItemStack item, double weight, float dropChance) {
        this.item = item;
        this.weight = weight;
        this.dropChance = dropChance;
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
}
