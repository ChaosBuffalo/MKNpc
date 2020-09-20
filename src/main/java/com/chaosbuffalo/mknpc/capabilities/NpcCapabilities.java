package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class NpcCapabilities {
    public static ResourceLocation MK_NPC_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "npc_data");
    public static ResourceLocation MK_WORLD_NPC_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "world_npc_data");

    @CapabilityInject(IMKNpcData.class)
    public static final Capability<IMKNpcData> NPC_DATA_CAPABILITY;

    @CapabilityInject(IWorldNpcData.class)
    public static final Capability<IWorldNpcData> WORLD_NPC_DATA_CAPABILITY;

    static {
        NPC_DATA_CAPABILITY = null;
        WORLD_NPC_DATA_CAPABILITY = null;
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IMKNpcData.class, new NpcDataHandler.Storage(), NpcDataHandler::new);
        CapabilityManager.INSTANCE.register(IWorldNpcData.class, new WorldNpcDataHandler.Storage(),
                WorldNpcDataHandler::new);
    }
}