package com.chaosbuffalo.mknpc.capabilities.structure_tracking;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;

public class StructureData implements INBTSerializable<CompoundNBT> {

    private int chunkX;
    private int chunkZ;
    private MutableBoundingBox boundingBox;
    private final List<StructureComponentData> components;
    private RegistryKey<World> worldKey;

    public StructureData(RegistryKey<World> worldKey, int chunkX, int chunkZ, MutableBoundingBox bounds, List<StructureComponentData> data){
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.boundingBox = bounds;
        this.components = new ArrayList<>();
        this.worldKey = worldKey;
        components.addAll(data);
    }

    public StructureData(){
        this.components = new ArrayList<>();
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public MutableBoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("chunkX", chunkX);
        tag.putInt("chunkY", chunkZ);
        int[] boundsArr = {boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ};
        tag.putIntArray("bounds", boundsArr);
        ListNBT comps = new ListNBT();
        for (StructureComponentData dat : components){
            comps.add(dat.serializeNBT());
        }
        tag.put("components", comps);
        tag.putString("world", worldKey.getLocation().toString());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        chunkX = nbt.getInt("chunkX");
        chunkZ = nbt.getInt("chunkY");
        worldKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString("world")));
        int[] boundsArr = nbt.getIntArray("bounds");
        boundingBox = new MutableBoundingBox(boundsArr);
        ListNBT comps = nbt.getList("components", Constants.NBT.TAG_COMPOUND);
        List<StructureComponentData> newComps = new ArrayList<>();
        for (INBT comp : comps){
            StructureComponentData data = new StructureComponentData();
            data.deserializeNBT((CompoundNBT) comp);
            newComps.add(data);
        }
        components.clear();
        components.addAll(newComps);
    }
}
