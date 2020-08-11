package com.chaosbuffalo.mknpc;

import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.command.NpcCommands;
import com.chaosbuffalo.mknpc.dialogue.NPCDialogueExtension;
import com.chaosbuffalo.mknpc.entity.MKEntityTypes;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(MKNpc.MODID)
public class MKNpc
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mknpc";
    private NpcDefinitionManager npcDefinitionManager;

    public MKNpc() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        MKEntityTypes.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void aboutToStart(FMLServerAboutToStartEvent event){
        npcDefinitionManager = new NpcDefinitionManager();
        event.getServer().getResourceManager().addReloadListener(npcDefinitionManager);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        NPCDialogueExtension.sendExtension();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        NpcCommands.register(event.getCommandDispatcher());
    }

    private void setup(final FMLCommonSetupEvent event){
        NpcCapabilities.registerCapabilities();
    }
}
