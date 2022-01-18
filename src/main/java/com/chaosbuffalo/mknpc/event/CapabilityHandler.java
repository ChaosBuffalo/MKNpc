package com.chaosbuffalo.mknpc.event;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid= MKNpc.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof LivingEntity) {
            if (e.getObject() instanceof PlayerEntity) {
                e.addCapability(NpcCapabilities.MK_QUEST_CAP_ID, new PlayerQuestingDataProvider((PlayerEntity) e.getObject()));
            } else {
                EntityNpcDataProvider provider = new EntityNpcDataProvider((LivingEntity) e.getObject());
                attachCap(NpcCapabilities.MK_NPC_CAP_ID, provider, e);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void attachWorldCapability(AttachCapabilitiesEvent<World> e) {
        WorldNpcDataProvider provider = new WorldNpcDataProvider(e.getObject());
        attachCap(NpcCapabilities.MK_WORLD_NPC_CAP_ID, provider, e);
    }

    @SubscribeEvent
    public static void attachChunkCapability(AttachCapabilitiesEvent<Chunk> e) {
        ChunkNpcDataProvider provider = new ChunkNpcDataProvider(e.getObject());
        attachCap(NpcCapabilities.MK_CHUNK_NPC_CAP_ID, provider, e);
    }

    @SubscribeEvent
    public static void attachChestCapability(AttachCapabilitiesEvent<TileEntity> e) {
        if (e.getObject() instanceof ChestTileEntity) {
            ChestNpcDataProvider provider = new ChestNpcDataProvider((ChestTileEntity) e.getObject());
            attachCap(NpcCapabilities.MK_CHEST_CAP_ID, provider, e);
        }
    }

    private static void attachCap(ResourceLocation capId, NpcCapabilities.Provider<?, ?> provider, AttachCapabilitiesEvent<?> event) {
        event.addCapability(capId, provider);
        event.addListener(provider::invalidate);
    }
}
