package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mknpc.MKNpc;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

public class NpcDefinitionManager extends JsonReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final Map<ResourceLocation, NpcDefinition> DEFINITIONS = new HashMap<>();

    public NpcDefinitionManager() {
        super(GSON, "mknpcs");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> objectIn,
                         IResourceManager resourceManagerIn,
                         IProfiler profilerIn) { ;
        DEFINITIONS.clear();
        for(Map.Entry<ResourceLocation, JsonObject> entry : objectIn.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            MKNpc.LOGGER.info("Found Npc Definition file: {}", resourcelocation);
            if (resourcelocation.getPath().startsWith("_")) continue; //Forge: filter anything beginning with "_" as it's used for metadata.
            NpcDefinition def = NpcDefinition.deserializeJson(GSON, entry.getKey(), entry.getValue());
            DEFINITIONS.put(def.getDefinitionName(), def);
        }
    }

    public static NpcDefinition getDefinition(ResourceLocation name){
        return DEFINITIONS.get(name);
    }
}
