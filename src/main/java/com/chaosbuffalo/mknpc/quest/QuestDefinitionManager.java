package com.chaosbuffalo.mknpc.quest;

import com.chaosbuffalo.mknpc.quest.objectives.LootChestObjective;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class QuestDefinitionManager extends JsonReloadListener {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final String DEFINITION_FOLDER = "mkquests";
    private MinecraftServer server;
    private boolean serverStarted = false;

    public static final Map<ResourceLocation, QuestDefinition> DEFINITIONS = new HashMap<>();

    public static final Map<ResourceLocation, Supplier<QuestObjective>> OBJECTIVE_DESERIALIZERS = new HashMap<>();

    public QuestDefinitionManager(){
        super(GSON, DEFINITION_FOLDER);
        MinecraftForge.EVENT_BUS.register(this);
    }


    public static void putObjectiveDeserializer(ResourceLocation name, Supplier<QuestObjective> deserializer){
        OBJECTIVE_DESERIALIZERS.put(name, deserializer);
    }

    public static void setupDeserializers(){
        putObjectiveDeserializer(LootChestObjective.NAME, LootChestObjective::new);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {

    }

    public static QuestDefinition getDefinition(ResourceLocation questName){
        return DEFINITIONS.get(questName);
    }

    @SubscribeEvent
    public void serverStop(FMLServerStoppingEvent event) {
        serverStarted = false;
        server = null;
    }

    @SubscribeEvent
    public void serverStart(FMLServerAboutToStartEvent event) {
        server = event.getServer();
        serverStarted = true;
    }

    @SubscribeEvent
    public void onDataPackSync(OnDatapackSyncEvent event) {

    }
}
