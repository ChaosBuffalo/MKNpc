package com.chaosbuffalo.mknpc.npc.option_entries;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface INpcOptionEntry extends INBTSerializable<CompoundNBT> {

    void applyToEntity(Entity entity);
}
