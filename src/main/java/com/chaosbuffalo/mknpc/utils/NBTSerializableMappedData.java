package com.chaosbuffalo.mknpc.utils;

import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NBTSerializableMappedData implements INBTSerializable<CompoundNBT> {
    protected final Map<String, Integer> intData = new HashMap<>();
    protected final Map<String, Double> doubleData = new HashMap<>();
    protected final Map<String, GlobalPos> blockPosData = new HashMap<>();
    protected final Map<String, Float> floatData = new HashMap<>();
    protected final Map<String, ResourceLocation> rlData = new HashMap<>();
    protected final Map<String, Boolean> boolData = new HashMap<>();
    protected final Map<String, ITextComponent> textData = new HashMap<>();
    protected final Map<String, String> stringData = new HashMap<>();
    protected final Map<String, UUID> uuidData = new HashMap<>();

    public UUID getUUID(String name) {
        return uuidData.get(name);
    }

    public UUID computeUUID(String name) {
        return uuidData.computeIfAbsent(name, k -> UUID.randomUUID());
    }

    public void putUUID(String name, UUID value) {
        uuidData.put(name, value);
    }

    public void removeUUID(String name) {
        uuidData.remove(name);
    }

    public String getString(String name) {
        return stringData.get(name);
    }

    public void putString(String name, String value) {
        stringData.put(name, value);
    }

    public void removeString(String name) {
        stringData.remove(name);
    }

    public boolean getBool(String name){
        return boolData.get(name);
    }

    public void putBool(String name, boolean value){
        boolData.put(name, value);
    }

    public void removeBool(String name) {
        boolData.remove(name);
    }

    public int getInt(String name){
        return intData.get(name);
    }

    public void putInt(String name, int value){
        intData.put(name, value);
    }

    public void removeInt(String name) {
        intData.remove(name);
    }

    public double getDouble(String name){
        return doubleData.get(name);
    }

    public void putDouble(String name, double value){
        doubleData.put(name, value);
    }

    public void removeDouble(String name) {
        doubleData.remove(name);
    }

    public GlobalPos getBlockPos(String name){
        return blockPosData.get(name);
    }

    public void putBlockPos(String name, GlobalPos value){
        blockPosData.put(name, value);
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

    public void removeFloat(String name) {
        floatData.remove(name);
    }

    public ResourceLocation getResourceLocation(String name){
        return rlData.get(name);
    }

    public void putResourceLocation(String name, ResourceLocation value){
        rlData.put(name, value);
    }

    public void removeResourceLocation(String name) {
        rlData.remove(name);
    }

    public ITextComponent getTextComponent(String name){
        return textData.get(name);
    }

    public void putTextComponent(String name, ITextComponent component){
        textData.put(name, component);
    }

    public void removeTextComponent(String name) {
        textData.remove(name);
    }

    public Map<String, GlobalPos> getBlockPosData() {
        return blockPosData;
    }

    public Map<String, Boolean> getBoolData() {
        return boolData;
    }

    public Map<String, Double> getDoubleData() {
        return doubleData;
    }

    public Map<String, Float> getFloatData() {
        return floatData;
    }

    public Map<String, Integer> getIntData() {
        return intData;
    }

    public Map<String, ITextComponent> getTextData() {
        return textData;
    }

    public Map<String, ResourceLocation> getRlData() {
        return rlData;
    }

    public void clearData() {
        floatData.clear();
        rlData.clear();
        intData.clear();
        textData.clear();
        doubleData.clear();
        boolData.clear();
        blockPosData.clear();
        stringData.clear();
        uuidData.clear();
    }

    public boolean isEmpty() {
        return floatData.isEmpty() && rlData.isEmpty() && intData.isEmpty() && textData.isEmpty()
                && doubleData.isEmpty() && boolData.isEmpty() && blockPosData.isEmpty() && stringData.isEmpty()
                && uuidData.isEmpty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        if (!doubleData.isEmpty()) {
            CompoundNBT doubleNbt = new CompoundNBT();
            for (Map.Entry<String, Double> entry : doubleData.entrySet()){
                doubleNbt.putDouble(entry.getKey(), entry.getValue());
            }
            nbt.put("doubleData", doubleNbt);
        }
        if (!intData.isEmpty()) {
            CompoundNBT intNbt = new CompoundNBT();
            for (Map.Entry<String, Integer> entry : intData.entrySet()){
                intNbt.putInt(entry.getKey(), entry.getValue());
            }
            nbt.put("intData", intNbt);
        }
        if (!blockPosData.isEmpty()) {
            CompoundNBT blockPosNbt = new CompoundNBT();
            for (Map.Entry<String, GlobalPos> entry : blockPosData.entrySet()){
                blockPosNbt.put(entry.getKey(), GlobalPos.CODEC.encodeStart(NBTDynamicOps.INSTANCE, entry.getValue())
                        .getOrThrow(false, MKNpc.LOGGER::error));
            }
            nbt.put("blockPosData", blockPosNbt);
        }
        if (!floatData.isEmpty()) {
            CompoundNBT floatNbt = new CompoundNBT();
            for (Map.Entry<String, Float> entry : floatData.entrySet()){
                floatNbt.putFloat(entry.getKey(), entry.getValue());
            }
            nbt.put("floatData", floatNbt);
        }
        if (!rlData.isEmpty()) {
            CompoundNBT rlNbt = new CompoundNBT();
            for (Map.Entry<String, ResourceLocation> entry : rlData.entrySet()){
                rlNbt.putString(entry.getKey(), entry.getValue().toString());
            }
            nbt.put("rlData", rlNbt);
        }
        if (!boolData.isEmpty()) {
            CompoundNBT boolNbt = new CompoundNBT();
            for (Map.Entry<String, Boolean> entry : boolData.entrySet()){
                boolNbt.putBoolean(entry.getKey(), entry.getValue());
            }
            nbt.put("boolData", boolNbt);
        }
        if (!textData.isEmpty()) {
            CompoundNBT textNbt = new CompoundNBT();
            for (Map.Entry<String, ITextComponent> entry : textData.entrySet()){
                textNbt.putString(entry.getKey(), ITextComponent.Serializer.toJson(entry.getValue()));
            }
            nbt.put("textData", textNbt);
        }
        if (!uuidData.isEmpty()) {
            CompoundNBT uuidNbt = new CompoundNBT();
            for (Map.Entry<String, UUID> entry : uuidData.entrySet()) {
                uuidNbt.putUniqueId(entry.getKey(), entry.getValue());
            }
            nbt.put("uuidData", uuidNbt);
        }
        if (!stringData.isEmpty()) {
            CompoundNBT stringNbt = new CompoundNBT();
            for (Map.Entry<String, String> entry : stringData.entrySet()) {
                stringNbt.putString(entry.getKey(), entry.getValue());
            }
            nbt.put("stringData", stringNbt);
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        clearData();
        if (nbt.contains("doubleData")) {
            CompoundNBT doubleNbt = nbt.getCompound("doubleData");
            for (String key : doubleNbt.keySet()){
                putDouble(key, doubleNbt.getDouble(key));
            }
        }
        if (nbt.contains("intData")) {
            CompoundNBT intNbt = nbt.getCompound("intData");
            for (String key : intNbt.keySet()){
                putInt(key, intNbt.getInt(key));
            }
        }
        if (nbt.contains("blockPosData")) {
            CompoundNBT blockPosNbt = nbt.getCompound("blockPosData");
            for (String key : blockPosNbt.keySet()){
                putBlockPos(key, GlobalPos.CODEC.parse(NBTDynamicOps.INSTANCE, blockPosNbt.getCompound(key)).result()
                        .orElse(GlobalPos.getPosition(World.OVERWORLD, BlockPos.fromLong(blockPosNbt.getLong(key)))));
            }
        }
        if (nbt.contains("floatData")) {
            CompoundNBT floatNbt = nbt.getCompound("floatData");
            for (String key : floatNbt.keySet()){
                putFloat(key, floatNbt.getFloat(key));
            }
        }
        if (nbt.contains("rlData")) {
            CompoundNBT rlNbt = nbt.getCompound("rlData");
            for (String key : rlNbt.keySet()){
                putResourceLocation(key, new ResourceLocation(rlNbt.getString(key)));
            }
        }
        if (nbt.contains("boolData")) {
            CompoundNBT boolNbt = nbt.getCompound("boolData");
            for (String key : boolNbt.keySet()){
                putBool(key, boolNbt.getBoolean(key));
            }
        }
        if (nbt.contains("textData")) {
            CompoundNBT textNbt = nbt.getCompound("textData");
            for (String key : textNbt.keySet()){
                putTextComponent(key, ITextComponent.Serializer.getComponentFromJson(textNbt.getString(key)));
            }
        }
        if (nbt.contains("uuidData")) {
            CompoundNBT uuidNbt = nbt.getCompound("uuidData");
            for (String key : uuidNbt.keySet()) {
                putUUID(key, uuidNbt.getUniqueId(key));
            }
        }
        if (nbt.contains("stringData")) {
            CompoundNBT stringNbt = nbt.getCompound("stringData");
            for (String key : stringNbt.keySet()) {
                putString(key, stringNbt.getString(key));
            }
        }
    }
}
