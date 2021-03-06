package com.chaosbuffalo.mknpc.event;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.ChunkNpcDataProvider;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.capabilities.EntityNpcDataProvider;
import com.chaosbuffalo.mknpc.capabilities.WorldNpcDataProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        if (!(e.getObject() instanceof PlayerEntity) && e.getObject() instanceof LivingEntity) {
            e.addCapability(NpcCapabilities.MK_NPC_CAP_ID, new EntityNpcDataProvider((LivingEntity) e.getObject()));
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void attachWorldCapability(AttachCapabilitiesEvent<World> e) {
        e.addCapability(NpcCapabilities.MK_WORLD_NPC_CAP_ID, new WorldNpcDataProvider(e.getObject()));
    }

    @SubscribeEvent
    public static void attachChunkCapability(AttachCapabilitiesEvent<Chunk> e) {
        e.addCapability(NpcCapabilities.MK_CHUNK_NPC_CAP_ID, new ChunkNpcDataProvider(e.getObject()));
    }

}