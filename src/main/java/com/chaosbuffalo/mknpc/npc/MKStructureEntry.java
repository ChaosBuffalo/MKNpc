package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.spawn.SpawnOption;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.*;

public class MKStructureEntry implements INBTSerializable<CompoundNBT> {
    private ResourceLocation structureName;
    private UUID structureId;
    private final List<NotableNpcEntry> notables;
    private final Set<ResourceLocation> mobs;
    private final Set<ResourceLocation> factions;

    public MKStructureEntry(ResourceLocation structureName, UUID structureId){
        this();
        this.structureName = structureName;
        this.structureId = structureId;
    }

    public MKStructureEntry(){
        notables = new ArrayList<>();
        mobs = new HashSet<>();
        factions = new HashSet<>();
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
                notables.add(new NotableNpcEntry(def, spawner));
            } else {
                mobs.add(def.getDefinitionName());
            }
            factions.add(def.getFactionName());
        }
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
    }
}
