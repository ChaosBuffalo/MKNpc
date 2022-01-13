package com.chaosbuffalo.mknpc.quest.data.player;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class PlayerQuestObjectiveData implements INBTSerializable<CompoundNBT> {

    private String objectiveName;
    private final Map<String, Integer> intData = new HashMap<>();
    private final Map<String, Double> doubleData = new HashMap<>();
    private final Map<String, BlockPos> blockPosData = new HashMap<>();
    private final Map<String, Float> floatData = new HashMap<>();
    private final Map<String, ResourceLocation> rlData = new HashMap<>();
    private final Map<String, ITextComponent> textData = new HashMap<>();
    private final Map<String, Boolean> boolData = new HashMap<>();
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

    public boolean getBool(String name){
        return boolData.get(name);
    }

    public void putBool(String name, boolean value){
        boolData.put(name, value);
    }

    public int getInt(String name){
        return intData.get(name);
    }

    public void putInt(String name, int value){
        intData.put(name, value);
    }

    public double getDouble(String name){
        return doubleData.get(name);
    }

    public void putDouble(String name, double value){
        doubleData.put(name, value);
    }

    public BlockPos getBlockPos(String name){
        return blockPosData.get(name);
    }

    public void putBlockPos(String name, BlockPos value){
        blockPosData.put(name, value);
    }

    public Map<String, BlockPos> getBlockPosData() {
        return blockPosData;
    }

    public void removeBlockPos(String name){
        blockPosData.remove(name);
    }

    public float getFloat(String name){
        return floatData.get(name);
    }

    public void putFloat(String name, float value){
        floatData.put(name, value);
    }

    public ResourceLocation getResourceLocation(String name){
        return rlData.get(name);
    }

    public void putResourceLocation(String name, ResourceLocation value){
        rlData.put(name, value);
    }

    public ITextComponent getTextComponent(String name){
        return textData.get(name);
    }

    public void putTextComponent(String name, ITextComponent component){
        textData.put(name, component);
    }

    public PlayerQuestObjectiveData(CompoundNBT nbt){
        deserializeNBT(nbt);
    }

    public boolean isComplete() {
        return getBool("isComplete");
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        CompoundNBT doubleNbt = new CompoundNBT();
        for (Map.Entry<String, Double> entry : doubleData.entrySet()){
            doubleNbt.putDouble(entry.getKey(), entry.getValue());
        }
        nbt.put("doubleData", doubleNbt);
        CompoundNBT intNbt = new CompoundNBT();
        for (Map.Entry<String, Integer> entry : intData.entrySet()){
            intNbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("intData", intNbt);
        CompoundNBT blockPosNbt = new CompoundNBT();
        for (Map.Entry<String, BlockPos> entry : blockPosData.entrySet()){
            blockPosNbt.putLong(entry.getKey(), entry.getValue().toLong());
        }
        nbt.put("blockPosData", blockPosNbt);
        CompoundNBT floatNbt = new CompoundNBT();
        for (Map.Entry<String, Float> entry : floatData.entrySet()){
            floatNbt.putFloat(entry.getKey(), entry.getValue());
        }
        nbt.put("floatData", floatNbt);
        CompoundNBT rlNbt = new CompoundNBT();
        for (Map.Entry<String, ResourceLocation> entry : rlData.entrySet()){
            rlNbt.putString(entry.getKey(), entry.getValue().toString());
        }
        nbt.put("rlData", rlNbt);
        CompoundNBT textNbt = new CompoundNBT();
        for (Map.Entry<String, ITextComponent> entry : textData.entrySet()){
            textNbt.putString(entry.getKey(), ITextComponent.Serializer.toJson(entry.getValue()));
        }
        nbt.put("textData", textNbt);
        CompoundNBT boolNbt = new CompoundNBT();
        for (Map.Entry<String, Boolean> entry : boolData.entrySet()){
            boolNbt.putBoolean(entry.getKey(), entry.getValue());
        }
        nbt.put("boolData", boolNbt);
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
        CompoundNBT doubleNbt = nbt.getCompound("doubleData");
        for (String key : doubleNbt.keySet()){
            doubleData.put(key, doubleNbt.getDouble(key));
        }
        CompoundNBT intNbt = nbt.getCompound("intData");
        for (String key : intNbt.keySet()){
            intData.put(key, intNbt.getInt(key));
        }
        CompoundNBT blockPosNbt = nbt.getCompound("blockPosData");
        for (String key : blockPosNbt.keySet()){
            blockPosData.put(key, BlockPos.fromLong(blockPosNbt.getLong(key)));
        }
        CompoundNBT floatNbt = nbt.getCompound("floatData");
        for (String key : floatNbt.keySet()){
            floatData.put(key, floatNbt.getFloat(key));
        }
        CompoundNBT rlNbt = nbt.getCompound("rlData");
        for (String key : rlNbt.keySet()){
            rlData.put(key, new ResourceLocation(rlNbt.getString(key)));
        }
        CompoundNBT textNbt = nbt.getCompound("textData");
        for (String key : textNbt.keySet()){
            textData.put(key, ITextComponent.Serializer.getComponentFromJson(textNbt.getString(key)));
        }
        CompoundNBT boolNbt = nbt.getCompound("boolData");
        for (String key : boolNbt.keySet()){
            boolData.put(key, boolNbt.getBoolean(key));
        }
        objectiveName = nbt.getString("name");
        ListNBT descriptions = nbt.getList("description", Constants.NBT.TAG_STRING);
        for (INBT desc : descriptions){
            description.add(ITextComponent.Serializer.getComponentFromJson(desc.getString()));
        }
    }
}
