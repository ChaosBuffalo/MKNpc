package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.network.NpcDefinitionClientUpdatePacket;
import com.chaosbuffalo.mknpc.network.PacketHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;

import java.util.HashMap;
import java.util.Map;

public class NpcDefinitionManager extends JsonReloadListener {
    private final MinecraftServer server;

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final Map<ResourceLocation, NpcDefinition> DEFINITIONS = new HashMap<>();
    public static final Map<ResourceLocation, NpcDefinitionClient> CLIENT_DEFINITIONS = new HashMap<>();

    public NpcDefinitionManager(MinecraftServer server) {
        super(GSON, "mknpcs");
        MinecraftForge.EVENT_BUS.register(this);
        this.server = server;
    }

    public NpcDefinitionManager(){
        this(null);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> objectIn,
                         IResourceManager resourceManagerIn,
                         IProfiler profilerIn) { ;
        DEFINITIONS.clear();
        boolean wasChanged = false;
        for(Map.Entry<ResourceLocation, JsonObject> entry : objectIn.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            MKNpc.LOGGER.info("Found Npc Definition file: {}", resourcelocation);
            if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.
            NpcDefinition def = NpcDefinition.deserializeJson(GSON, entry.getKey(), entry.getValue());
            DEFINITIONS.put(def.getDefinitionName(), def);
            CLIENT_DEFINITIONS.put(def.getDefinitionName(), new NpcDefinitionClient(def));
            wasChanged = true;
        }
        if (wasChanged){
            syncToPlayers();
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        if (event.getPlayer() instanceof ServerPlayerEntity){
            NpcDefinitionClientUpdatePacket updatePacket = new NpcDefinitionClientUpdatePacket(
                    CLIENT_DEFINITIONS.values());
            MKNpc.LOGGER.info("Sending {} update packet", event.getPlayer());
            ((ServerPlayerEntity) event.getPlayer()).connection.sendPacket(
                    PacketHandler.getNetworkChannel().toVanillaPacket(
                            updatePacket, NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    public void syncToPlayers(){
        NpcDefinitionClientUpdatePacket updatePacket = new NpcDefinitionClientUpdatePacket(CLIENT_DEFINITIONS.values());
        if (server != null){
            server.getPlayerList().sendPacketToAllPlayers(PacketHandler.getNetworkChannel().toVanillaPacket(
                    updatePacket, NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    public static NpcDefinition getDefinition(ResourceLocation name){
        return DEFINITIONS.get(name);
    }
}
