package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class NpcCapabilities {
    public static ResourceLocation MK_NPC_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "npc_data");
    public static ResourceLocation MK_WORLD_NPC_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "world_npc_data");
    public static ResourceLocation MK_CHUNK_NPC_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "chunk_npc_data");
    public static ResourceLocation MK_CHEST_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "chest_npc_data");

    public static ResourceLocation MK_QUEST_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "player_quest_data");

    @CapabilityInject(IEntityNpcData.class)
    public static final Capability<IEntityNpcData> ENTITY_NPC_DATA_CAPABILITY;

    @CapabilityInject(IWorldNpcData.class)
    public static final Capability<IWorldNpcData> WORLD_NPC_DATA_CAPABILITY;

    @CapabilityInject(IChunkNpcData.class)
    public static final Capability<IChunkNpcData> CHUNK_NPC_DATA_CAPABILITY;

    @CapabilityInject(IChestNpcData.class)
    public static final Capability<IChestNpcData> CHEST_NPC_DATA_CAPABILITY;

    @CapabilityInject(IPlayerQuestData.class)
    public static final Capability<IPlayerQuestData> PLAYER_QUEST_DATA_CAPABILITY;



    static {
        ENTITY_NPC_DATA_CAPABILITY = null;
        WORLD_NPC_DATA_CAPABILITY = null;
        CHUNK_NPC_DATA_CAPABILITY = null;
        CHEST_NPC_DATA_CAPABILITY = null;
        PLAYER_QUEST_DATA_CAPABILITY = null;
    }

    public static void registerCapabilities() {
        CoreCapabilities.registerLivingEntity(e -> e instanceof MKEntity);
        CapabilityManager.INSTANCE.register(IEntityNpcData.class, new EntityNpcDataHandler.Storage(), EntityNpcDataHandler::new);
        CapabilityManager.INSTANCE.register(IWorldNpcData.class, new WorldNpcDataHandler.Storage(),
                WorldNpcDataHandler::new);
        CapabilityManager.INSTANCE.register(IChunkNpcData.class, new ChunkNpcDataHandler.Storage(),
                ChunkNpcDataHandler::new);
        CapabilityManager.INSTANCE.register(IChestNpcData.class, new ChestNpcDataHandler.Storage(), ChestNpcDataHandler::new);
    }


}