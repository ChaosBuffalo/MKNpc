package com.chaosbuffalo.mknpc.spawn;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class SpawnOption implements INBTSerializable<CompoundNBT> {
    private double weight;
    private NpcDefinition definition;

    public SpawnOption(){
        this.weight = 1.0;
    }

    public SpawnOption(double weight, NpcDefinition definition) {
        this.weight = weight;
        this.definition = definition;
    }

    public void setDefinition(NpcDefinition definition) {
        this.definition = definition;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public NpcDefinition getDefinition() {
        return definition;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("definition", getDefinition().getDefinitionName().toString());
        tag.putDouble("weight", getWeight());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ResourceLocation definitionName = new ResourceLocation(nbt.getString("definition"));
        NpcDefinition def = NpcDefinitionManager.getDefinition(definitionName);
        setDefinition(def);
        setWeight(nbt.getDouble("weight"));
    }
}
