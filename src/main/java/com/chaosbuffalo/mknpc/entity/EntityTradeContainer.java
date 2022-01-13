package com.chaosbuffalo.mknpc.entity;

import com.chaosbuffalo.mknpc.inventories.QuestGiverInventoryContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class EntityTradeContainer implements INamedContainerProvider {
    private final MKEntity entity;

    public EntityTradeContainer(MKEntity entity){
        this.entity = entity;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("mknpc.quest.trade_container", entity.getName());
    }

    @Nullable
    @Override
    public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity player) {
        return QuestGiverInventoryContainer.createGeneric9X1(menuId, playerInventory, entity);
    }
}
