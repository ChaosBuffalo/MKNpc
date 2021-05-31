package com.chaosbuffalo.mknpc;

import com.chaosbuffalo.mknpc.capabilities.IEntityNpcData;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.command.NpcCommands;
import com.chaosbuffalo.mknpc.dialogue.NPCDialogueExtension;
import com.chaosbuffalo.mknpc.init.MKNpcBlocks;
import com.chaosbuffalo.mknpc.init.MKNpcTileEntityTypes;
import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.chaosbuffalo.mknpc.network.PacketHandler;
import com.chaosbuffalo.mknpc.npc.INpcOptionExtension;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.TestJigsawStructurePools;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(MKNpc.MODID)
public class MKNpc
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mknpc";
    public static final String REGISTER_NPC_OPTIONS_EXTENSION = "register_npc_options_extension";
    private NpcDefinitionManager npcDefinitionManager;

    public MKNpc() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        MKNpcBlocks.register();
        MKNpcTileEntityTypes.register();
        npcDefinitionManager = new NpcDefinitionManager();
        MKNpcWorldGen.registerStructurePieces();
        TestJigsawStructurePools.registerPatterns();
        MinecraftForge.EVENT_BUS.addListener(MKNpcWorldGen::biomeSetup);
        MinecraftForge.EVENT_BUS.addListener(MKNpcWorldGen::worldSetup);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        NPCDialogueExtension.sendExtension();
    }

    private void processIMC(final InterModProcessEvent event)
    {
        LOGGER.info("MKNpc.processIMC");
        event.getIMCStream().forEach(m -> {
            if (m.getMethod().equals(REGISTER_NPC_OPTIONS_EXTENSION)) {
                LOGGER.info("IMC register npc option extension from mod {} {}", m.getSenderModId(),
                        m.getMethod());
                INpcOptionExtension ext = (INpcOptionExtension) m.getMessageSupplier().get();
                ext.registerNpcOptionExtension();
            }
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event){
        NpcCommands.register(event.getDispatcher());
    }


    private void setup(final FMLCommonSetupEvent event){
        NpcCapabilities.registerCapabilities();
        PacketHandler.setupHandler();
        NpcDefinitionManager.setupDeserializers();
        NpcCommands.registerArguments();
    }

    public static LazyOptional<? extends IEntityNpcData> getNpcData(Entity entity){
        return entity.getCapability(NpcCapabilities.ENTITY_NPC_DATA_CAPABILITY);
    }

    public static LazyOptional<? extends IWorldNpcData> getWorldNpcData(World world){
        return world.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY);
    }
}
