package com.chaosbuffalo.mknpc.inventories;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;

public class PsuedoChestContainer extends ChestContainer {
    private final IInventory psuedoChest;

    public static PsuedoChestContainer createGeneric9X3(int id, PlayerInventory player, IInventory inventory, IInventory realblockEntity) {
        return new PsuedoChestContainer(ContainerType.GENERIC_9X3, id, player, inventory, 3, realblockEntity);
    }

    public PsuedoChestContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn,
                                IInventory p_i50092_4_, int rows, IInventory otherChest) {
        super(type, id, playerInventoryIn, p_i50092_4_, rows);
        psuedoChest = otherChest;
    }

    @Override
    public IInventory getLowerChestInventory() {
        return psuedoChest;
    }
}
