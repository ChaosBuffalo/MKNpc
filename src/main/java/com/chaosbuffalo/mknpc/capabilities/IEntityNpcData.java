package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.option_entries.LootOptionEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface IEntityNpcData extends INBTSerializable<CompoundNBT> {

    void attach(LivingEntity entity);

    LivingEntity getEntity();

    @Nullable
    NpcDefinition getDefinition();

    void setDefinition(NpcDefinition definition);

    int getBonusXp();

    void setBonusXp(int value);

    boolean wasMKSpawned();

    void setSpawnPos(BlockPos pos);

    BlockPos getSpawnPos();

    void setSpawnID(UUID id);

    @Nonnull
    UUID getSpawnID();

    void setMKSpawned(boolean value);

    boolean isNotable();

    void setNotable(boolean value);

    boolean needsDefinitionApplied();

    void applyDefinition();

    void addLootOption(LootOptionEntry option);

    void setChanceNoLoot(double chance);

    void setDropChances(int count);

    void setNoLootChanceIncrease(double chance);

    void handleExtraLoot(int lootingLevel, Collection<ItemEntity> drops, DamageSource source);
}
