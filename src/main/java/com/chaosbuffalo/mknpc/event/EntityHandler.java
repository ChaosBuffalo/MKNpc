package com.chaosbuffalo.mknpc.event;

import com.chaosbuffalo.mkcore.core.damage.MKDamageSource;
import com.chaosbuffalo.mkcore.effects.SpellTriggers;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mknpc.quest.objectives.IContainerObjectiveHandler;
import com.chaosbuffalo.mknpc.quest.objectives.LootChestObjective;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.IControlNaturalSpawns;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.MKJigsawStructure;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.MKStructureStart;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid= MKNpc.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EntityHandler {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        MKNpc.getNpcData(event.getEntity()).ifPresent((cap) -> {
            if (cap.wasMKSpawned()) {
                event.setCanceled(true);
            } else {
                if (cap.needsDefinitionApplied()) {
                    cap.applyDefinition();
                }
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityDamage(LivingDamageEvent event) {
        if (event.getSource() instanceof MKDamageSource) {
            if (event.getEntityLiving() instanceof PlayerEntity &&
                    !(event.getSource().getTrueSource() instanceof PlayerEntity)) {
                event.setAmount((float) (event.getAmount() * MKNpc.getDifficultyScale(event.getEntityLiving())));
            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event){
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END){
            event.world.tickableTileEntities.forEach(
                    x -> x.getCapability(NpcCapabilities.CHEST_NPC_DATA_CAPABILITY).ifPresent(IChestNpcData::tick));
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.RightClickBlock event){
        if (event.getPlayer().world.isRemote) {
            return;
        }
        MinecraftServer server = event.getPlayer().getServer();
        if (server == null) {
            return;
        }
        World world = event.getWorld();
        BlockPos pos = event.getHitVec().getPos();
        if (world.getBlockState(pos).getBlock() instanceof ChestBlock){
            if (ChestTileEntity.getPlayersUsing(world, pos) == 0){
                TileEntity te = world.getTileEntity(pos);
                if (te == null){
                    return;
                }
                LazyOptional<IChestNpcData> chestCapO = te.getCapability(NpcCapabilities.CHEST_NPC_DATA_CAPABILITY);
                if (chestCapO.resolve().isPresent()){
                    IChestNpcData chestCap = chestCapO.resolve().get();
                    World overWorld = server.getWorld(World.OVERWORLD);
                    if (overWorld != null){
                        overWorld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY).ifPresent(worldData -> {
                            MKNpc.getPlayerQuestData(event.getPlayer()).ifPresent(x -> {
                                x.getQuestChains().forEach(pQuestChain -> {
                                    QuestChainInstance questChain = worldData.getQuest(pQuestChain.getQuestId());
                                    Quest currentQuest = questChain.getDefinition().getQuest(pQuestChain.getCurrentQuest());
                                    if (currentQuest != null) {
                                        for (QuestObjective<?> obj : currentQuest.getObjectives()){
                                            if (obj instanceof IContainerObjectiveHandler){
                                                IContainerObjectiveHandler iObj = (IContainerObjectiveHandler) obj;
                                                PlayerQuestObjectiveData pObj = pQuestChain.getQuestData(
                                                        currentQuest.getQuestName()).getObjective(obj.getObjectiveName());
                                                QuestData qData = questChain.getQuestChainData().getQuestData(currentQuest.getQuestName());
                                                if (iObj.onLootChest(pObj, qData, chestCap.getChestId())){
                                                    iObj.populateChest(chestCap, qData);
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                });
                            });
                        });
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLootDrop(LivingDropsEvent event) {
        if (event.isRecentlyHit()) {
            MKNpc.getNpcData(event.getEntityLiving()).ifPresent(x -> {
                x.handleExtraLoot(event.getLootingLevel(), event.getDrops(), event.getSource());
            });
        }
    }

    @SubscribeEvent
    public static void onLivingSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getSpawnReason() == SpawnReason.NATURAL && stopSpawningForClassification(event.getEntityLiving())) {
            BlockPos spawnPos = new BlockPos(event.getX(), event.getY(), event.getZ());
            if (event.getWorld() instanceof ServerWorld){
                StructureManager manager = ((ServerWorld) event.getWorld()).getStructureManager();
                for (Structure<?> structure : ForgeRegistries.STRUCTURE_FEATURES){
                    if (structure instanceof IControlNaturalSpawns){
                        if (!((IControlNaturalSpawns) structure).doesAllowSpawns()){
                            StructureStart<?> start = manager.getStructureStart(spawnPos, false, structure);
                            if (start != StructureStart.DUMMY){
                                event.setResult(Event.Result.DENY);
                            }
                        }
                    }
                }
            }
        }

    }

    private static boolean stopSpawningForClassification(LivingEntity entity){
        return (entity.getClassification(false) == EntityClassification.MONSTER);
    }
}
