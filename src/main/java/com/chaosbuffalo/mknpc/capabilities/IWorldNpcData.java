package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.npc.NotableChestEntry;
import com.chaosbuffalo.mknpc.npc.NotableNpcEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.chaosbuffalo.mknpc.npc.options.WorldPermanentOption;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.QuestDefinition;
import com.chaosbuffalo.mknpc.quest.generation.QuestChainBuildResult;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public interface IWorldNpcData extends INBTSerializable<CompoundNBT> {

    boolean hasEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, Entity entity);

    boolean hasEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, UUID spawnId);

    INpcOptionEntry getEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute,
                                         Entity entity);

    INpcOptionEntry getEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute,
                                         UUID entityId);

    void addEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute,
                              UUID entityId, INpcOptionEntry entry);

    void addSpawner(MKSpawnerTileEntity spawner);

    void addChest(IChestNpcData chestData);

    @Nullable
    QuestChainInstance getQuest(UUID questId);

    Optional<QuestChainBuildResult> buildQuest(QuestDefinition definition, BlockPos pos);

    NotableChestEntry getNotableChest(UUID id);

    NotableNpcEntry getNotableNpc(UUID id);

    World getWorld();
}
