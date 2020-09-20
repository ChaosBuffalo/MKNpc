package com.chaosbuffalo.mknpc.npc.option_entries;

import com.chaosbuffalo.mknpc.npc.NpcItemChoice;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;

import java.util.HashMap;
import java.util.Map;

public class EquipmentOptionsEntry implements INpcOptionEntry {
    private final Map<EquipmentSlotType, NpcItemChoice> itemChoices;

    public EquipmentOptionsEntry(){
        this.itemChoices = new HashMap<>();
    }

    public void setSlotChoice(EquipmentSlotType slot, NpcItemChoice choice){
        itemChoices.put(slot, choice);
    }

    @Override
    public void applyToEntity(Entity entity) {
        if (entity instanceof LivingEntity){
            applyItemChoices((LivingEntity) entity);
        }
    }

    public void applyItemChoices(LivingEntity entity){
        for (Map.Entry<EquipmentSlotType, NpcItemChoice> entry : itemChoices.entrySet()){
            NpcItemChoice.livingEquipmentAssign(entity, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        for (Map.Entry<EquipmentSlotType, NpcItemChoice> entry : itemChoices.entrySet()){
            tag.put(entry.getKey().getName(), entry.getValue().serializeNBT());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        for (String key : nbt.keySet()){
            EquipmentSlotType type = EquipmentSlotType.fromString(key);
            NpcItemChoice newChoice = new NpcItemChoice();
            newChoice.deserializeNBT(nbt.getCompound(key));
            setSlotChoice(type, newChoice);
        }
    }
}
