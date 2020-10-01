package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.WorldPermanentSpawnConfiguration;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.chaosbuffalo.mknpc.npc.options.WorldPermanentOption;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class WorldNpcDataHandler implements IWorldNpcData{

    private final HashMap<UUID, WorldPermanentSpawnConfiguration> worldPermanentSpawnConfigurations;

    private World world;

    public WorldNpcDataHandler(){
        worldPermanentSpawnConfigurations = new HashMap<>();
    }

    @Override
    public void attach(World world) {
        this.world = world;
    }

    @Override
    public boolean hasEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, Entity entity) {
        UUID spawnId = getSpawnIdForEntity(entity);
        return worldPermanentSpawnConfigurations.containsKey(spawnId) &&
                worldPermanentSpawnConfigurations.get(spawnId).hasAttributeEntry(
                        definition.getDefinitionName(), attribute.getName());
    }

    private static UUID getSpawnIdForEntity(Entity entity){
        return MKNpc.getNpcData(entity).map(IMKNpcData::getSpawnID).orElse(entity.getUniqueID());
    }

    @Override
    public INpcOptionEntry getEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, Entity entity) {
        UUID spawnId = getSpawnIdForEntity(entity);
        return worldPermanentSpawnConfigurations.get(spawnId).getOptionEntry(definition, attribute);
    }

    @Override
    public void addEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute,
                                     Entity entity, INpcOptionEntry entry) {
        UUID spawnId = getSpawnIdForEntity(entity);
        if (!worldPermanentSpawnConfigurations.containsKey(spawnId)){
            worldPermanentSpawnConfigurations.put(spawnId, new WorldPermanentSpawnConfiguration());
        }
        worldPermanentSpawnConfigurations.get(spawnId).addAttributeEntry(definition, attribute, entry);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        for (UUID entityId : worldPermanentSpawnConfigurations.keySet()){
            WorldPermanentSpawnConfiguration config = worldPermanentSpawnConfigurations.get(entityId);
            tag.put(entityId.toString(), config.serializeNBT());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        worldPermanentSpawnConfigurations.clear();
        for (String idKey : nbt.keySet()){
            UUID entityId = UUID.fromString(idKey);
            WorldPermanentSpawnConfiguration config = new WorldPermanentSpawnConfiguration();
            config.deserializeNBT(nbt.getCompound(idKey));
            worldPermanentSpawnConfigurations.put(entityId, config);
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
