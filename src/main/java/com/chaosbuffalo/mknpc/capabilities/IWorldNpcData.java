package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.chaosbuffalo.mknpc.npc.options.WorldPermanentOption;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public interface IWorldNpcData extends INBTSerializable<CompoundNBT> {

    void attach(World world);

    boolean hasEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, Entity entity);

    boolean hasEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, UUID spawnId);

    INpcOptionEntry getEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute,
                                         Entity entity);

    INpcOptionEntry getEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute,
                                         UUID entityId);

    void addEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute,
                              UUID entityId, INpcOptionEntry entry);

    void addSpawner(MKSpawnerTileEntity spawner);

    World getWorld();
}
