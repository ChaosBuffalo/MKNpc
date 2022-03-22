package com.chaosbuffalo.mknpc.event;

import com.chaosbuffalo.mkchat.event.PlayerNpcDialogueTreeGatherEvent;
import com.chaosbuffalo.mkcore.core.damage.MKDamageSource;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import com.chaosbuffalo.mknpc.capabilities.IEntityNpcData;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mknpc.quest.objectives.IContainerObjectiveHandler;
import com.chaosbuffalo.mknpc.quest.objectives.IKillObjectiveHandler;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.IControlNaturalSpawns;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityInteract(PlayerInteractEvent.RightClickBlock event){
        if (event.isCanceled()){
            return;
        }
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
            TileEntity te = world.getTileEntity(pos);
            if (te == null){
                return;
            }
            World overWorld = server.getWorld(World.OVERWORLD);
            if (overWorld != null) {
                te.getCapability(NpcCapabilities.CHEST_NPC_DATA_CAPABILITY).ifPresent(
                        chestCap -> {
                            overWorld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY).ifPresent(
                                    worldData -> processLootChestEvents(event.getPlayer(), chestCap, worldData));
                            if (chestCap.hasQuestInventoryForPlayer(event.getPlayer()) && !event.getPlayer().isSneaking()){
                                event.getPlayer().openContainer(chestCap);
                                event.setCanceled(true);
                            }
                        });
            }
        }
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event){
        int bonusXp = MKNpc.getNpcData(event.getEntityLiving()).map(IEntityNpcData::getBonusXp).orElse(0);
        event.setDroppedExperience(event.getDroppedExperience() + bonusXp);
    }

    private static void processLootChestEvents(PlayerEntity player, IChestNpcData chestCap, IWorldNpcData worldData) {
        MKNpc.getPlayerQuestData(player).ifPresent(x -> x.getQuestChains().forEach(
                pQuestChain -> {
                    QuestChainInstance questChain = worldData.getQuest(pQuestChain.getQuestId());
                    if (questChain == null) {
                        return;
                    }
                    for (String questName : pQuestChain.getCurrentQuests()){
                        Quest currentQuest = questChain.getDefinition().getQuest(questName);
                        if (currentQuest != null) {
                            for (QuestObjective<?> obj : currentQuest.getObjectives()) {
                                if (obj instanceof IContainerObjectiveHandler) {
                                    IContainerObjectiveHandler iObj = (IContainerObjectiveHandler) obj;
                                    PlayerQuestData pQuest = pQuestChain.getQuestData(currentQuest.getQuestName());
                                    PlayerQuestObjectiveData pObj = pQuest.getObjective(obj.getObjectiveName());
                                    QuestData qData = questChain.getQuestChainData().getQuestData(currentQuest);
                                    if (iObj.onLootChest(player, pObj, qData, chestCap)) {
                                        questChain.signalQuestProgress(worldData, x, currentQuest, pQuestChain, false);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }));
    }

    @SubscribeEvent
    public static void onSetupDialogue(PlayerNpcDialogueTreeGatherEvent event){
        if (event.getPlayer().world.isRemote) {
            return;
        }
        MinecraftServer server = event.getPlayer().getServer();
        if (server == null) {
            return;
        }
        World overWorld = server.getWorld(World.OVERWORLD);
        MKNpc.LOGGER.debug("Setting up dialogue between {} and {}", event.getSpeaker(), event.getPlayer());
        if (overWorld != null) {
           overWorld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY).ifPresent(
                    worldData -> MKNpc.getPlayerQuestData(event.getPlayer()).ifPresent(x -> x.getQuestChains().forEach(
                            pQuestChain -> {
                                QuestChainInstance questChainInstance = worldData.getQuest(pQuestChain.getQuestId());
                                if (questChainInstance != null){
                                    MKNpc.LOGGER.debug("Adding quest chain dialogue for {}", questChainInstance.getDefinition().getName());
                                    questChainInstance.getTreeForEntity(event.getSpeaker()).ifPresent(event::addTree);
                                }
                            })));
        }

    }

    private static void handleKillEntityForPlayer(PlayerEntity player, LivingDeathEvent event, IWorldNpcData worldData){
        MKNpc.getNpcData(event.getEntityLiving()).ifPresent(x -> {
            if (x.getDefinition() != null){
                NpcDefinition def = x.getDefinition();
                MKNpc.getPlayerQuestData(player).ifPresent(pData -> pData.getQuestChains().forEach(
                        pQuestChain -> {
                            QuestChainInstance questChain = worldData.getQuest(pQuestChain.getQuestId());
                            if (questChain == null) {
                                return;
                            }
                            for (String questName : pQuestChain.getCurrentQuests()){
                                Quest currentQuest = questChain.getDefinition().getQuest(questName);
                                if (currentQuest != null) {
                                    for (QuestObjective<?> obj : currentQuest.getObjectives()) {
                                        if (obj instanceof IKillObjectiveHandler) {
                                            PlayerQuestData pQuest = pQuestChain.getQuestData(currentQuest.getQuestName());
                                            PlayerQuestObjectiveData pObj = pQuest.getObjective(obj.getObjectiveName());
                                            QuestData qData = questChain.getQuestChainData().getQuestData(currentQuest);
                                            if (((IKillObjectiveHandler) obj).onPlayerKillNpcDefEntity(player, pObj, def, event, qData, pQuestChain)){
                                                questChain.signalQuestProgress(worldData, pData, currentQuest, pQuestChain, false);
                                            }
                                        }
                                    }
                                }
                            }
                        }));
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDeathEvent(LivingDeathEvent event){
        if (event.isCanceled() || event.getEntityLiving().world.isRemote){
            return;
        }
        if (event.getSource().getTrueSource() instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
            MinecraftServer server = player.getServer();
            if (server == null){
                return;
            }
            World overWorld = server.getWorld(World.OVERWORLD);
            if (overWorld == null){
                return;
            }
            overWorld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY).ifPresent(worldNpcData -> {
                handleKillEntityForPlayer(player, event, worldNpcData);
                Team team = player.getTeam();
                if (team != null) {
                    for (String s : team.getMembershipCollection()) {
                        ServerPlayerEntity serverplayerentity = server.getPlayerList().getPlayerByUsername(s);
                        if (serverplayerentity != null && !serverplayerentity.equals(player)){
                            handleKillEntityForPlayer(serverplayerentity, event, worldNpcData);
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event){
        MKNpc.getNpcData(event.getEntityLiving()).ifPresent(IEntityNpcData::tick);
    }

    @SubscribeEvent
    public static void onLootDrop(LivingDropsEvent event) {
        if (event.isRecentlyHit()) {
            MKNpc.getNpcData(event.getEntityLiving()).ifPresent(x -> x.handleExtraLoot(
                    event.getLootingLevel(), event.getDrops(), event.getSource()));
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
