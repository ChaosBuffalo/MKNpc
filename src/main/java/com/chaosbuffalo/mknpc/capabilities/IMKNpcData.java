package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public interface IMKNpcData extends INBTSerializable<CompoundNBT> {

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

    void setMKSpawned(boolean value);
}
