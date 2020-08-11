package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public interface IMKNpcData extends INBTSerializable<CompoundNBT> {

    void attach(LivingEntity entity);

    LivingEntity getEntity();

    @Nullable
    NpcDefinition getDefinition();

    void setDefinition(NpcDefinition definition);

    int getBonusXp();

    boolean wasMKSpawned();

    void setMKSpawned(boolean value);
}
