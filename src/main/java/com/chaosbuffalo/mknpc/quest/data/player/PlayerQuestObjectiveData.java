package com.chaosbuffalo.mknpc.quest.data.player;

import com.chaosbuffalo.mknpc.utils.NBTSerializableMappedData;
import net.minecraft.nbt.*;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class PlayerQuestObjectiveData extends NBTSerializableMappedData {

    private String objectiveName;
    private List<IFormattableTextComponent> description = new ArrayList<>();

    public PlayerQuestObjectiveData(String objectiveName, IFormattableTextComponent... description){
        this(objectiveName, Arrays.asList(description));
    }

    public PlayerQuestObjectiveData(String objectiveName, List<IFormattableTextComponent> description){
        this.objectiveName = objectiveName;
        this.description.addAll(description);
    }

    public void setDescription(IFormattableTextComponent... description) {
        this.description.clear();
        this.description.addAll(Arrays.asList(description));
    }

    public List<IFormattableTextComponent> getDescription() {
        return description;
    }

    public String getObjectiveName() {
        return objectiveName;
    }

    public PlayerQuestObjectiveData(CompoundNBT nbt){
        deserializeNBT(nbt);
    }

    public boolean isComplete() {
        return getBool("isComplete");
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = super.serializeNBT();
        nbt.putString("name", objectiveName);
        ListNBT descriptions = new ListNBT();
        for (IFormattableTextComponent comp : this.description){
            descriptions.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(comp)));
        }
        nbt.put("description", descriptions);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        objectiveName = nbt.getString("name");
        ListNBT descriptions = nbt.getList("description", Constants.NBT.TAG_STRING);
        for (INBT desc : descriptions){
            description.add(ITextComponent.Serializer.getComponentFromJson(desc.getString()));
        }
    }
}
