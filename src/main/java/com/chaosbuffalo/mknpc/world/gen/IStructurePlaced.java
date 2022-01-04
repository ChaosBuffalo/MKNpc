package com.chaosbuffalo.mknpc.world.gen;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IStructurePlaced {

    boolean isInsideStructure();

    @Nullable
    UUID getStructureId();

    @Nullable
    ResourceLocation getStructureName();

    void setStructureName(ResourceLocation structureName);

    void setStructureId(UUID structureId);

    BlockPos getBlockPos();

    @Nullable
    World getStructureWorld();
}
