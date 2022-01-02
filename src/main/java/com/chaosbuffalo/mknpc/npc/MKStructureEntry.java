package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import com.chaosbuffalo.mknpc.capabilities.WorldNpcDataHandler;
import com.chaosbuffalo.mknpc.capabilities.structure_tracking.StructureData;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.spawn.SpawnOption;
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
    private final Set<ResourceLocation> mobs;
    private final Set<ResourceLocation> factions;
    @Nullable
    private StructureData structureData;
    private final WorldNpcDataHandler worldData;

    public MKStructureEntry(WorldNpcDataHandler worldData, ResourceLocation structureName, UUID structureId, @Nullable StructureData structureData){
        this(worldData);
        this.structureName = structureName;
        this.structureId = structureId;
        this.structureData = structureData;
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
        structureData = null;
    }

    public boolean hasChestWithTag(String tag){
        return notableChests.stream().anyMatch(x -> x.getLabel() != null && x.getLabel().equals(tag));
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
            } else {
                mobs.add(def.getDefinitionName());
            }
            factions.add(def.getFactionName());
        }
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
    }
}
