package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.entries.LootOptionEntry;
import com.chaosbuffalo.mknpc.npc.entries.QuestOfferingEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface IEntityNpcData extends INBTSerializable<CompoundNBT> {

    LivingEntity getEntity();

    @Nullable
    NpcDefinition getDefinition();

    void setDefinition(NpcDefinition definition);

    int getBonusXp();

    void setBonusXp(int value);

    boolean wasMKSpawned();

    void setSpawnPos(BlockPos pos);

    void addQuestOffering(ResourceLocation questName, UUID questId);

    boolean hasGeneratedQuest();

    boolean shouldHaveQuest();

    void putShouldHaveQuest(boolean value);

    Map<ResourceLocation, UUID> getQuestsOffered();

    BlockPos getSpawnPos();

    void setSpawnID(UUID id);

    @Nonnull
    UUID getSpawnID();

    void tick();

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

    void requestQuest(QuestOfferingEntry entry);
}
