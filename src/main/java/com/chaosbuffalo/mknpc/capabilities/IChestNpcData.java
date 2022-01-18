package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.world.gen.IStructurePlaced;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public interface IChestNpcData extends INBTSerializable<CompoundNBT>, IStructurePlaced, INamedContainerProvider {

    Inventory getQuestInventoryForPlayer(PlayerEntity player);

    boolean hasQuestInventoryForPlayer(PlayerEntity player);

    @Nullable
    UUID getChestId();

    @Nullable
    String getChestLabel();

    ChestTileEntity getTileEntity();

    void generateChestId(String chestLabel);

    void tick();
}
