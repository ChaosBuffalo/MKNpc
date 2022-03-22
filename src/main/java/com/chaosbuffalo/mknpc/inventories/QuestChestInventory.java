package com.chaosbuffalo.mknpc.inventories;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.tileentity.ChestTileEntity;

import javax.annotation.Nonnull;

public class QuestChestInventory extends Inventory {
    private final ChestTileEntity replacementChest;

    public QuestChestInventory(ChestTileEntity replacementChest) {
        super(replacementChest.getSizeInventory());
        this.replacementChest = replacementChest;
    }

    @Override
    public void openInventory(@Nonnull PlayerEntity player) {
        replacementChest.openInventory(player);
    }

    @Override
    public void closeInventory(@Nonnull PlayerEntity player) {
        replacementChest.closeInventory(player);
    }
}
