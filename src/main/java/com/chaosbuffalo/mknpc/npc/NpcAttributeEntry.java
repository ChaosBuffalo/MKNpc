package com.chaosbuffalo.mknpc.npc;


import net.minecraft.entity.ai.attributes.IAttribute;

public class NpcAttributeEntry {
    private String attributeName;
    private double value;

    public NpcAttributeEntry(){

    }

    public NpcAttributeEntry(IAttribute attribute, double value){
        this.attributeName = attribute.getName();
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
