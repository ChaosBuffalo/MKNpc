package com.chaosbuffalo.mknpc.event;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.capabilities.WorldStructureManager;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.MKJigsawStructure;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerStartedEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid= MKNpc.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class WorldStructureHandler {

    public static List<MKJigsawStructure> MK_STRUCTURE_CACHE;
    public static final Map<ResourceLocation, MKJigsawStructure> MK_STRUCTURE_INDEX = new HashMap<>();


    @SubscribeEvent
    public static void serverStarted(final FMLServerStartedEvent event) {
        WorldStructureHandler.cacheStructures();
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent ev) {
        if (ev.phase == TickEvent.Phase.END && ev.world instanceof ServerLevel) {
            ServerLevel sWorld = (ServerLevel) ev.world;
            Level overworld = sWorld.getServer().getLevel(Level.OVERWORLD);
            if (overworld == null){
                return;
            }
            Optional<IWorldNpcData> overOpt = overworld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY).resolve();
            if (overOpt.isPresent()) {
                StructureFeatureManager manager = sWorld.structureFeatureManager();
                IWorldNpcData over = overOpt.get();
                WorldStructureManager activeStructures = over.getStructureManager();
                for (ServerPlayer player : sWorld.players()) {
                    List<MKJigsawStructure.Start> starts = WorldStructureHandler.MK_STRUCTURE_CACHE.stream().map(
                            x -> manager.getStructureAt(player.blockPosition(), false, x))
                            .filter(x -> x != StructureStart.INVALID_START)
                            .map(x -> (MKJigsawStructure.Start) x)
                            .collect(Collectors.toList());
                    for (MKJigsawStructure.Start start : starts) {
                        over.setupStructureDataIfAbsent(start, ev.world);
                        activeStructures.visitStructure(start.getInstanceId(), player);
                    }
                }
                if (ev.world.dimension() == Level.OVERWORLD) {
                    over.update();
                }
            }
        }
    }

    public static void cacheStructures() {
        MK_STRUCTURE_CACHE = ForgeRegistries.STRUCTURE_FEATURES.getValues().stream()
                .filter(x -> x instanceof MKJigsawStructure).map(x -> (MKJigsawStructure) x)
                .collect(Collectors.toList());
        MK_STRUCTURE_INDEX.clear();
        MK_STRUCTURE_CACHE.forEach(x -> {
            MKNpc.LOGGER.info("Caching MK Structure {}", x.getFeatureName());
            MK_STRUCTURE_INDEX.put(x.getRegistryName(), x);
        });
    }
}
