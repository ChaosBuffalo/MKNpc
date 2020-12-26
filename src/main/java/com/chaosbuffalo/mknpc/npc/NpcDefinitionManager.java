package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.network.NpcDefinitionClientUpdatePacket;
import com.chaosbuffalo.mknpc.network.PacketHandler;
import com.chaosbuffalo.mknpc.npc.option_entries.AbilitiesOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.EquipmentOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.FactionNameOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.chaosbuffalo.mknpc.npc.options.*;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class NpcDefinitionManager extends JsonReloadListener {
    private final MinecraftServer server;

    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final Map<ResourceLocation, NpcDefinition> DEFINITIONS = new HashMap<>();
    public static final Map<ResourceLocation, NpcDefinitionClient> CLIENT_DEFINITIONS = new HashMap<>();
    public static final Map<ResourceLocation, Supplier<INpcOptionEntry>> ENTRY_DESERIALIZERS = new HashMap<>();
    public static final Map<ResourceLocation, Supplier<NpcDefinitionOption>> OPTION_DESERIALIZERS = new HashMap<>();

    public NpcDefinitionManager(MinecraftServer server) {
        super(GSON, "mknpcs");
        MinecraftForge.EVENT_BUS.register(this);
        this.server = server;
    }

    public NpcDefinitionManager(){
        this(null);
    }

    public static void setupDeserializers(){
        putOptionEntryDeserializer(AbilitiesOption.NAME, AbilitiesOptionEntry::new);
        putOptionEntryDeserializer(EquipmentOption.NAME, EquipmentOptionEntry::new);
        putOptionDeserializer(EquipmentOption.NAME, EquipmentOption::new);
        putOptionDeserializer(AbilitiesOption.NAME, AbilitiesOption::new);
        putOptionDeserializer(AttributesOption.NAME, AttributesOption::new);
        putOptionDeserializer(NameOption.NAME, NameOption::new);
        putOptionDeserializer(ExperienceOption.NAME, ExperienceOption::new);
        putOptionDeserializer(FactionOption.NAME, FactionOption::new);
        putOptionDeserializer(DialogueOption.NAME, DialogueOption::new);
        putOptionDeserializer(FactionNameOption.NAME, FactionNameOption::new);
        putOptionEntryDeserializer(FactionNameOption.NAME, FactionNameOptionEntry::new);
        putOptionDeserializer(NotableOption.NAME, NotableOption::new);
    }

    public static void putOptionDeserializer(ResourceLocation optionName,
                                             Supplier<NpcDefinitionOption> optionFunction){
        OPTION_DESERIALIZERS.put(optionName, optionFunction);
    }

    public static void putOptionEntryDeserializer(ResourceLocation entryName,
                                                  Supplier<INpcOptionEntry> entryFunction){
        ENTRY_DESERIALIZERS.put(entryName, entryFunction);
    }

    @Nullable
    public static INpcOptionEntry getNpcOptionEntry(ResourceLocation entryName){
        if (!ENTRY_DESERIALIZERS.containsKey(entryName)){
            MKNpc.LOGGER.error("Failed to deserialize option entry {}", entryName);
            return null;
        }
        return ENTRY_DESERIALIZERS.get(entryName).get();
    }

    @Nullable
    public static NpcDefinitionOption getNpcOption(ResourceLocation optionName){

        if (!OPTION_DESERIALIZERS.containsKey(optionName)){
            MKNpc.LOGGER.error("Failed to deserialize option {}", optionName);
            return null;
        }
        return OPTION_DESERIALIZERS.get(optionName).get();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> objectIn,
                         IResourceManager resourceManagerIn,
                         IProfiler profilerIn) { ;
        DEFINITIONS.clear();
        CLIENT_DEFINITIONS.clear();
        boolean wasChanged = false;
        for(Map.Entry<ResourceLocation, JsonObject> entry : objectIn.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            MKNpc.LOGGER.info("Found Npc Definition file: {}", resourcelocation);
            if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.
            NpcDefinition def = NpcDefinition.deserializeJson(GSON, entry.getKey(), entry.getValue());
            DEFINITIONS.put(def.getDefinitionName(), def);
            wasChanged = true;
        }
        if (wasChanged){
            resolveDefinitions();
            syncToPlayers();
        }
    }

    public static void resolveDefinitions(){
        List<ResourceLocation> toRemove = new ArrayList<>();
        for (NpcDefinition def : DEFINITIONS.values()){
            boolean resolved = def.resolveParents();
            if (!resolved){
                MKNpc.LOGGER.info("Failed to resolve parents for {}, removing definition",
                        def.getDefinitionName());
                toRemove.add(def.getDefinitionName());
            }
        }
        for (ResourceLocation loc : toRemove){
            DEFINITIONS.remove(loc);
        }
        for (NpcDefinition def : DEFINITIONS.values()){
            def.resolveEntityType();
            CLIENT_DEFINITIONS.put(def.getDefinitionName(), new NpcDefinitionClient(def));
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
