package com.chaosbuffalo.mknpc.npc;

import net.minecraft.util.ResourceLocation;

public class NpcAbilityEntry {
    private ResourceLocation abilityName;
    private int priority;

    public NpcAbilityEntry(){
        priority = 1;
    }

    public NpcAbilityEntry(ResourceLocation abilityName, int priority){
        this.priority = priority;
        this.abilityName = abilityName;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setAbilityName(ResourceLocation abilityName) {
        this.abilityName = abilityName;
    }

    public ResourceLocation getAbilityName() {
        return abilityName;
    }
}
