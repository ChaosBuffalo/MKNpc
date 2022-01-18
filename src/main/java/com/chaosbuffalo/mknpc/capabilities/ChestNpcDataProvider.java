package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.tileentity.ChestTileEntity;
import net.minecraftforge.common.capabilities.Capability;

public class ChestNpcDataProvider extends NpcCapabilities.Provider<ChestTileEntity, IChestNpcData> {

    public ChestNpcDataProvider(ChestTileEntity entity) {
        super(entity);
    }

    @Override
    IChestNpcData makeData(ChestTileEntity attached) {
        return new ChestNpcDataHandler(attached);
    }

    @Override
    Capability<IChestNpcData> getCapability() {
        return NpcCapabilities.CHEST_NPC_DATA_CAPABILITY;
    }
}
