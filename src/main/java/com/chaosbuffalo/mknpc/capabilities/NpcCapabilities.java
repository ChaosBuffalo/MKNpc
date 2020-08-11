package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class NpcCapabilities {
    public static ResourceLocation MK_NPC_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "npc_data");

    @CapabilityInject(IMKNpcData.class)
    public static final Capability<IMKNpcData> NPC_DATA_CAPABILITY;

    static {
        NPC_DATA_CAPABILITY = null;
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IMKNpcData.class, new NpcDataHandler.Storage(), NpcDataHandler::new);
    }
}