package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.WorldPermanentSpawnConfiguration;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.chaosbuffalo.mknpc.npc.options.WorldPermanentOption;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class WorldNpcDataHandler implements IWorldNpcData{

    private final HashMap<UUID, WorldPermanentSpawnConfiguration> worldPermanentSpawnConfigurations;
    private final HashMap<UUID, MKStructureEntry> structureIndex;

    private World world;

    public WorldNpcDataHandler(){
        worldPermanentSpawnConfigurations = new HashMap<>();
        structureIndex = new HashMap<>();
    }

    @Override
    public void attach(World world) {
        this.world = world;
    }

    @Override
    public boolean hasEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, Entity entity) {
        UUID spawnId = getSpawnIdForEntity(entity);
        return hasEntityOptionEntry(definition, attribute, spawnId);
    }

    @Override
    public boolean hasEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, UUID spawnId) {
        return worldPermanentSpawnConfigurations.containsKey(spawnId) &&
                worldPermanentSpawnConfigurations.get(spawnId).hasAttributeEntry(
                        definition.getDefinitionName(), attribute.getName());
    }

    public static UUID getSpawnIdForEntity(Entity entity){
        return MKNpc.getNpcData(entity).map(IEntityNpcData::getSpawnID).orElse(entity.getUniqueID());
    }

    @Override
    public INpcOptionEntry getEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, Entity entity) {
        UUID spawnId = getSpawnIdForEntity(entity);
        return getEntityOptionEntry(definition, attribute, spawnId);
    }

    @Override
    public INpcOptionEntry getEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, UUID spawnId) {
        return worldPermanentSpawnConfigurations.get(spawnId).getOptionEntry(definition, attribute);
    }

    @Override
    public void addEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute,
                                     UUID spawnId, INpcOptionEntry entry) {
        if (!worldPermanentSpawnConfigurations.containsKey(spawnId)){
            worldPermanentSpawnConfigurations.put(spawnId, new WorldPermanentSpawnConfiguration());
        }
        worldPermanentSpawnConfigurations.get(spawnId).addAttributeEntry(definition, attribute, entry);
    }

    @Override
    public void addSpawner(MKSpawnerTileEntity spawner) {
        MKStructureEntry structure = structureIndex.computeIfAbsent(spawner.getStructureId(),
                key -> new MKStructureEntry(spawner.getStructureName(), spawner.getStructureId()));
        structure.addSpawner(spawner);
    }

    protected boolean hasStructure(UUID structureId){
        return structureIndex.containsKey(structureId);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        CompoundNBT spawnConfig = new CompoundNBT();
        for (UUID entityId : worldPermanentSpawnConfigurations.keySet()){
            WorldPermanentSpawnConfiguration config = worldPermanentSpawnConfigurations.get(entityId);
            spawnConfig.put(entityId.toString(), config.serializeNBT());
        }
        tag.put("spawnConfigs", spawnConfig);
        ListNBT structuresNbt = new ListNBT();
        for (MKStructureEntry structure : structureIndex.values()){
            structuresNbt.add(structure.serializeNBT());
        }
        tag.put("structures", structuresNbt);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT spawnConfigNbt = nbt.getCompound("spawnConfigs");
        for (String idKey : spawnConfigNbt.keySet()){
            UUID entityId = UUID.fromString(idKey);
            WorldPermanentSpawnConfiguration config = new WorldPermanentSpawnConfiguration();
            config.deserializeNBT(spawnConfigNbt.getCompound(idKey));
            worldPermanentSpawnConfigurations.put(entityId, config);
        }
        ListNBT structuresNbt = nbt.getList("structures", Constants.NBT.TAG_COMPOUND);
        for (INBT structureNbt : structuresNbt){
            MKStructureEntry newStructure = new MKStructureEntry();
            newStructure.deserializeNBT((CompoundNBT) structureNbt);
            structureIndex.put(newStructure.getStructureId(), newStructure);
        }
    }

    public static class Storage implements Capability.IStorage<IWorldNpcData> {


        @Nullable
        @Override
        public INBT writeNBT(Capability<IWorldNpcData> capability, IWorldNpcData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IWorldNpcData> capability, IWorldNpcData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
