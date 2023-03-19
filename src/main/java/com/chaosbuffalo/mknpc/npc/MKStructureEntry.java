package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mkcore.core.AbilityTracker;
import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import com.chaosbuffalo.mknpc.capabilities.PointOfInterestEntry;
import com.chaosbuffalo.mknpc.capabilities.WorldNpcDataHandler;
import com.chaosbuffalo.mknpc.capabilities.structure_tracking.StructureData;
import com.chaosbuffalo.mknpc.tile_entities.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.spawn.SpawnOption;
import com.chaosbuffalo.mknpc.tile_entities.MKPoiTileEntity;
import com.chaosbuffalo.mknpc.utils.NBTSerializableMappedData;
import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class MKStructureEntry implements INBTSerializable<CompoundNBT> {
    private ResourceLocation structureName;
    private UUID structureId;
    private final List<NotableChestEntry> notableChests;
    private final List<NotableNpcEntry> notables;
    private final Map<String, List<PointOfInterestEntry>> pois;
    private final Set<ResourceLocation> mobs;
    private final Set<ResourceLocation> factions;
    @Nullable
    private StructureData structureData;
    private final WorldNpcDataHandler worldData;
    private final NBTSerializableMappedData customStructureData;
    private final AbilityTracker cooldownTracker;
    private final Set<String> activeEvents = new HashSet<>();

    public MKStructureEntry(WorldNpcDataHandler worldData, ResourceLocation structureName, UUID structureId, @Nullable StructureData structureData){
        this(worldData);
        this.structureName = structureName;
        this.structureId = structureId;
        this.structureData = structureData;


    }

    public AbilityTracker getCooldownTracker() {
        return cooldownTracker;
    }

    public void addActiveEvent(String name) {
        activeEvents.add(name);
    }

    public Set<String> getActiveEvents() {
        return activeEvents;
    }

    public void clearActiveEvents() {
        activeEvents.clear();
    }

    public ChunkPos getChunkPos(){
        if (structureData != null){
            return new ChunkPos(structureData.getChunkX(), structureData.getChunkZ());
        } else {
            return new ChunkPos(0, 0);
        }

    }

    public MKStructureEntry(WorldNpcDataHandler worldData){
        this.worldData = worldData;
        notables = new ArrayList<>();
        mobs = new HashSet<>();
        factions = new HashSet<>();
        notableChests = new ArrayList<>();
        pois = new HashMap<>();
        structureData = null;
        customStructureData = new NBTSerializableMappedData();
        cooldownTracker = new AbilityTracker();
    }

    public Map<String, List<PointOfInterestEntry>> getPointsOfInterest() {
        return pois;
    }

    public List<PointOfInterestEntry> getPoisWithTag(String tag) {
        return pois.get(tag);
    }

    public Optional<PointOfInterestEntry> getFirstPoiWithTag(String tag) {
        return pois.containsKey(tag) ? pois.get(tag).stream().findFirst() : Optional.empty();
    }

    public boolean hasChestWithTag(String tag){
        return notableChests.stream().anyMatch(x -> x.getLabel() != null && x.getLabel().equals(tag));
    }

    public boolean hasNotableOfType(ResourceLocation npcDef){
        return notables.stream().anyMatch(x -> x.getDefinition() != null && x.getDefinition().getDefinitionName().equals(npcDef));
    }

    public Optional<NotableNpcEntry> getFirstNotableOfType(ResourceLocation npcDef){
        return notables.stream().filter(x -> x.getDefinition() != null && x.getDefinition().getDefinitionName().equals(npcDef)).findFirst();
    }

    public List<NotableNpcEntry> getAllNotablesOfType(ResourceLocation npcDef) {
        return notables.stream().filter(x -> x. getDefinition() != null && x.getDefinition().getDefinitionName().equals(npcDef)).collect(Collectors.toList());
    }

    public Optional<NotableChestEntry> getFirstChestWithTag(String tag){
        return notableChests.stream().filter(x -> x.getLabel() != null && x.getLabel().equals(tag)).findFirst();
    }

    public List<NotableChestEntry> getChestsWithTag(String tag){
        return notableChests.stream().filter(x -> x.getLabel() != null && x.getLabel().equals(tag)).collect(Collectors.toList());
    }

    public boolean hasStructureData(){
        return structureData != null;
    }

    public UUID getStructureId() {
        return structureId;
    }

    public ResourceLocation getStructureName() {
        return structureName;
    }

    public void addSpawner(MKSpawnerTileEntity spawner){
        for (SpawnOption spawnOption : spawner.getSpawnList().getOptions()){
            NpcDefinition def = spawnOption.getDefinition();
            if (def.isNotable()){
                NotableNpcEntry entry = new NotableNpcEntry(def, spawner);
                worldData.putNotableNpc(entry);
                notables.add(entry);
                spawner.putNotableId(def.getDefinitionName(), entry.getNotableId());
            } else {
                mobs.add(def.getDefinitionName());
            }
            factions.add(def.getFactionName());
        }
    }

    public boolean hasPoi(String name) {
        return pois.containsKey(name) && !pois.get(name).isEmpty();
    }

    private void putPoi(PointOfInterestEntry entry) {
        List<PointOfInterestEntry> entries = pois.computeIfAbsent(entry.getLabel(), (key) -> new ArrayList<>());
        entries.add(entry);
        worldData.putNotablePOI(entry);
    }

    public NBTSerializableMappedData getCustomData() {
        return customStructureData;
    }

    public void addPOI(MKPoiTileEntity poi) {
        PointOfInterestEntry entry = new PointOfInterestEntry(poi);
        putPoi(entry);
    }

    public void addChest(IChestNpcData chestData){
        NotableChestEntry entry = new NotableChestEntry(chestData);
        worldData.putNotableChest(entry);
        notableChests.add(entry);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("structureName", structureName.toString());
        tag.putUniqueId("structureId", structureId);
        ListNBT notablesNbt = new ListNBT();
        for (NotableNpcEntry notableEntry : notables){
            notablesNbt.add(notableEntry.serializeNBT());
        }
        tag.put("notables", notablesNbt);
        ListNBT mobNbt = new ListNBT();
        for (ResourceLocation mob : mobs){
            mobNbt.add(StringNBT.valueOf(mob.toString()));
        }
        tag.put("mobs", mobNbt);
        ListNBT factionNbt = new ListNBT();
        for (ResourceLocation faction : factions){
            factionNbt.add(StringNBT.valueOf(faction.toString()));
        }
        tag.put("factions", factionNbt);
        if (structureData != null){
            tag.put("structureData", structureData.serializeNBT());
        }
        ListNBT chestNbt = new ListNBT();
        for (NotableChestEntry chest : notableChests){
            chestNbt.add(chest.serializeNBT());
        }
        tag.put("chests", chestNbt);
        CompoundNBT poiTag = new CompoundNBT();
        for (String key : pois.keySet()) {
            ListNBT poiList = new ListNBT();
            for (PointOfInterestEntry entry : pois.getOrDefault(key, new ArrayList<>())) {
                poiList.add(entry.serializeNBT());
            }
            poiTag.put(key, poiList);
        }
        tag.put("pois", poiTag);
        if (!customStructureData.isEmpty()) {
            tag.put("customData", customStructureData.serializeNBT());
        }
        tag.put("cooldowns", cooldownTracker.serialize());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        structureName = new ResourceLocation(nbt.getString("structureName"));
        structureId = nbt.getUniqueId("structureId");
        ListNBT notablesNbt = nbt.getList("notables", Constants.NBT.TAG_COMPOUND);
        for (INBT notTag : notablesNbt){
            NotableNpcEntry newEntry = new NotableNpcEntry();
            newEntry.deserializeNBT((CompoundNBT) notTag);
            worldData.putNotableNpc(newEntry);
            notables.add(newEntry);
        }
        ListNBT mobNbt = nbt.getList("mobs", Constants.NBT.TAG_STRING);
        for (INBT mobName : mobNbt){
            ResourceLocation mobLoc = new ResourceLocation(mobName.getString());
            mobs.add(mobLoc);
        }
        ListNBT factionNbt = nbt.getList("factions", Constants.NBT.TAG_STRING);
        for (INBT factionName : factionNbt){
            ResourceLocation factionLoc = new ResourceLocation(factionName.getString());
            factions.add(factionLoc);
        }
        if (nbt.contains("structureData")){
            structureData = new StructureData();
            structureData.deserializeNBT(nbt.getCompound("structureData"));
        }
        ListNBT chestNbt = nbt.getList("chests", Constants.NBT.TAG_COMPOUND);
        for (INBT chest : chestNbt){
            NotableChestEntry chestEntry = new NotableChestEntry();
            chestEntry.deserializeNBT((CompoundNBT) chest);
            worldData.putNotableChest(chestEntry);
            notableChests.add(chestEntry);
        }
        pois.clear();
        CompoundNBT poiNbt = nbt.getCompound("pois");
        for (String key : poiNbt.keySet()) {
            ListNBT poiLNbt = poiNbt.getList(key, Constants.NBT.TAG_COMPOUND);
            for (INBT poi : poiLNbt) {
                PointOfInterestEntry entry  = new PointOfInterestEntry();
                entry.deserializeNBT((CompoundNBT) poi);
                putPoi(entry);
            }
        }
        if (nbt.contains("customData")) {
            customStructureData.deserializeNBT(nbt.getCompound("customData"));
        }
        if (nbt.contains("cooldowns")) {
            cooldownTracker.deserialize(nbt.getCompound("cooldowns"));
        }

    }
}
