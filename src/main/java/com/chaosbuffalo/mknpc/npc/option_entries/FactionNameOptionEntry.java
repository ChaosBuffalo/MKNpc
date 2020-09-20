package com.chaosbuffalo.mknpc.npc.option_entries;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;


public class FactionNameOptionEntry implements INpcOptionEntry{
    private String name;

    public FactionNameOptionEntry(){
        this.name = "";
    }

    public FactionNameOptionEntry(String name){
        this.name = name;
    }


    @Override
    public void applyToEntity(Entity entity) {
        if (!name.equals("") && entity instanceof LivingEntity){
            entity.setCustomName(new StringTextComponent(name));
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("name", name);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.name = nbt.getString("name");
    }
}
