package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class WorldNpcDataProvider extends NpcCapabilities.Provider<World, IWorldNpcData> {


    public WorldNpcDataProvider(World world) {
        super(world);
    }

    @Override
    IWorldNpcData makeData(World attached) {
        return new WorldNpcDataHandler(attached);
    }

    @Override
    Capability<IWorldNpcData> getCapability() {
        return NpcCapabilities.WORLD_NPC_DATA_CAPABILITY;
    }
}

