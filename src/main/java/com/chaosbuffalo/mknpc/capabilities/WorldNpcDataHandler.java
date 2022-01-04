package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.structure_tracking.StructureComponentData;
import com.chaosbuffalo.mknpc.capabilities.structure_tracking.StructureData;
import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import com.chaosbuffalo.mknpc.npc.*;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.chaosbuffalo.mknpc.npc.options.WorldPermanentOption;
import com.chaosbuffalo.mknpc.quest.QuestDefinition;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.world.gen.IStructurePlaced;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.MKSingleJigsawPiece;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class WorldNpcDataHandler implements IWorldNpcData{

    private final HashMap<UUID, WorldPermanentSpawnConfiguration> worldPermanentSpawnConfigurations;
    private final HashMap<UUID, MKStructureEntry> structureIndex;
    private final HashMap<ResourceLocation, List<UUID>> structureToInstanceIndex;
    private final HashMap<UUID, QuestChainInstance> quests;
    private final HashMap<UUID, NotableChestEntry> notableChests;
    private final HashMap<UUID, NotableNpcEntry> notableNpcs;

    private World world;

    public WorldNpcDataHandler(){
        worldPermanentSpawnConfigurations = new HashMap<>();
        structureIndex = new HashMap<>();
        structureToInstanceIndex = new HashMap<>();
        notableChests = new HashMap<>();
        notableNpcs = new HashMap<>();
        quests = new HashMap<>();
    }

    @Override
    public QuestChainInstance getQuest(UUID questId){
        return quests.get(questId);
    }

    @Override
    public void attach(World world) {
        this.world = world;
    }

    @Override
    public boolean hasEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, Entity entity) {
        UUID spawnId = getSpawnIdForEntity(entity);
        return hasEntityOptionEntry(definition, attribute, spawnId);
    }

    public void putNotableChest(NotableChestEntry notableChestEntry){
        notableChests.put(notableChestEntry.getChestId(), notableChestEntry);
    }

    @Override
    public NotableChestEntry getNotableChest(UUID id){
        return notableChests.get(id);
    }

    @Override
    public NotableNpcEntry getNotableNpc(UUID id){
        return notableNpcs.get(id);
    }

    public void putNotableNpc(NotableNpcEntry notableNpcEntry){
        notableNpcs.put(notableNpcEntry.getSpawnerId(), notableNpcEntry);
    }

    @Override
    public boolean hasEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, UUID spawnId) {
        return worldPermanentSpawnConfigurations.containsKey(spawnId) &&
                worldPermanentSpawnConfigurations.get(spawnId).hasAttributeEntry(
                        definition.getDefinitionName(), attribute.getName());
    }

    public static UUID getSpawnIdForEntity(Entity entity){
        return MKNpc.getNpcData(entity).map(IEntityNpcData::getSpawnID).orElse(entity.getUniqueID());
    }

    @Override
    public INpcOptionEntry getEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, Entity entity) {
        UUID spawnId = getSpawnIdForEntity(entity);
        return getEntityOptionEntry(definition, attribute, spawnId);
    }

    @Override
    public INpcOptionEntry getEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute, UUID spawnId) {
        return worldPermanentSpawnConfigurations.get(spawnId).getOptionEntry(definition, attribute);
    }

    @Override
    public void addEntityOptionEntry(NpcDefinition definition, WorldPermanentOption attribute,
                                     UUID spawnId, INpcOptionEntry entry) {
        if (!worldPermanentSpawnConfigurations.containsKey(spawnId)){
            worldPermanentSpawnConfigurations.put(spawnId, new WorldPermanentSpawnConfiguration());
        }
        worldPermanentSpawnConfigurations.get(spawnId).addAttributeEntry(definition, attribute, entry);
    }

    @Override
    public Optional<QuestChainInstance> buildQuest(QuestDefinition definition, BlockPos pos){
        Map<ResourceLocation, Integer> structuresNeeded = definition.getStructuresNeeded();
        if (hasStructureInstances(structuresNeeded.keySet())){
            Map<ResourceLocation, List<MKStructureEntry>> possibilities = structuresNeeded.keySet().stream()
                    .map(x -> new Pair<>(x, structureToInstanceIndex.get(x)))
                    .map(x -> x.mapSecond(ids -> ids.stream().map(structureIndex::get)
                            .filter(definition::doesStructureMeetRequirements)))
                    .collect(Collectors.toMap(Pair::getFirst, pair -> pair.getSecond().collect(Collectors.toList())));
            if (possibilities.entrySet().stream().allMatch(x -> x.getValue().size() >= structuresNeeded.get(x.getKey()))) {
                Map<ResourceLocation, List<MKStructureEntry>> questStructures = new HashMap<>();
                for (Map.Entry<ResourceLocation, Integer> needed : structuresNeeded.entrySet()){
                    int toFind = needed.getValue();
                    List<MKStructureEntry> byDistance = possibilities.get(needed.getKey()).stream().sorted(Comparator.comparingInt(
                            x -> new ChunkPos(pos).getChessboardDistance(x.getChunkPos())))
                            .collect(Collectors.toList());
                    List<MKStructureEntry> finals = new ArrayList<>();
                    for (int i = 0; i < toFind; i++){
                        finals.add(byDistance.get(i));
                    }
                    questStructures.put(needed.getKey(), finals);
                }

                QuestChainInstance instance = definition.generate(questStructures);
                instance.generateDialogue(questStructures);
                quests.put(instance.getQuestId(), instance);
                return Optional.of(instance);
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public boolean hasStructureInstances(Set<ResourceLocation> structureNames){
        return structureNames.stream().allMatch(this::isStructureIndexed);
    }

    private MKStructureEntry computeStructureEntry(IStructurePlaced structurePlaced){
        StructureData structureData = null;
        World structureWorld = structurePlaced.getStructureWorld();
        if (structureWorld instanceof ServerWorld){
            ServerWorld world = (ServerWorld) structureWorld;
            Structure<?> struct = ForgeRegistries.STRUCTURE_FEATURES.getValue(structurePlaced.getStructureName());
            if (struct != null){
                StructureStart<?> start = world.getStructureManager().getStructureStart(structurePlaced.getBlockPos(), false, struct);
                structureData = new StructureData(structurePlaced.getStructureWorld().getDimensionKey(),
                        start.getChunkPosX(), start.getChunkPosZ(), start.getBoundingBox(), start.getComponents().stream().map(
                        this::getComponentDataFromPiece).collect(Collectors.toList()));
            }

        }
        MKStructureEntry structureEntry = new MKStructureEntry(this, structurePlaced.getStructureName(), structurePlaced.getStructureId(), structureData);
        indexStructureEntry(structureEntry);
        return structureEntry;
    }

    private void indexStructureEntry(MKStructureEntry structureEntry){
        structureToInstanceIndex.computeIfAbsent(structureEntry.getStructureName(), key -> new ArrayList<>())
                .add(structureEntry.getStructureId());
    }

    private StructureComponentData getComponentDataFromPiece(StructurePiece piece){
        ResourceLocation pieceName = MKNpcWorldGen.UNKNOWN_PIECE;
        if (piece instanceof AbstractVillagePiece){
            if (((AbstractVillagePiece) piece).getJigsawPiece() instanceof MKSingleJigsawPiece){
                MKSingleJigsawPiece mkPiece = ((MKSingleJigsawPiece) ((AbstractVillagePiece) piece).getJigsawPiece());
                pieceName = mkPiece.getPieceEither().left().orElse(MKNpcWorldGen.UNKNOWN_PIECE);
            }
        }
        return new StructureComponentData(pieceName, piece.getBoundingBox());
    }

    @Override
    public void addSpawner(MKSpawnerTileEntity spawner) {
        MKStructureEntry structure = structureIndex.computeIfAbsent(spawner.getStructureId(),
                key -> computeStructureEntry(spawner));
        structure.addSpawner(spawner);
    }

    @Override
    public void addChest(IChestNpcData chestData){
        MKStructureEntry structure = structureIndex.computeIfAbsent(chestData.getStructureId(),
                key -> computeStructureEntry(chestData));
        structure.addChest(chestData);

    }

    protected boolean hasStructureInstance(UUID structureId){
        return structureIndex.containsKey(structureId);
    }

    protected boolean isStructureIndexed(ResourceLocation structureName){
        return structureToInstanceIndex.containsKey(structureName) && structureToInstanceIndex.get(structureName).size() > 0;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        CompoundNBT spawnConfig = new CompoundNBT();
        for (UUID entityId : worldPermanentSpawnConfigurations.keySet()){
            WorldPermanentSpawnConfiguration config = worldPermanentSpawnConfigurations.get(entityId);
            spawnConfig.put(entityId.toString(), config.serializeNBT());
        }
        tag.put("spawnConfigs", spawnConfig);
        ListNBT structuresNbt = new ListNBT();
        for (MKStructureEntry structure : structureIndex.values()){
            structuresNbt.add(structure.serializeNBT());
        }
        tag.put("structures", structuresNbt);
        ListNBT questNbt = new ListNBT();
        for (QuestChainInstance inst : quests.values()){
            questNbt.add(inst.serializeNBT());
        }
        tag.put("quests", questNbt);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT spawnConfigNbt = nbt.getCompound("spawnConfigs");
        for (String idKey : spawnConfigNbt.keySet()){
            UUID entityId = UUID.fromString(idKey);
            WorldPermanentSpawnConfiguration config = new WorldPermanentSpawnConfiguration();
            config.deserializeNBT(spawnConfigNbt.getCompound(idKey));
            worldPermanentSpawnConfigurations.put(entityId, config);
        }
        ListNBT structuresNbt = nbt.getList("structures", Constants.NBT.TAG_COMPOUND);
        for (INBT structureNbt : structuresNbt){
            MKStructureEntry newStructure = new MKStructureEntry(this);
            newStructure.deserializeNBT((CompoundNBT) structureNbt);
            structureIndex.put(newStructure.getStructureId(), newStructure);
            indexStructureEntry(newStructure);
        }
        ListNBT questsNbt = nbt.getList("quests", Constants.NBT.TAG_COMPOUND);
        for (INBT questNbt : questsNbt){
            QuestChainInstance inst = new QuestChainInstance((CompoundNBT) questNbt);
            quests.put(inst.getQuestId(), inst);
        }
    }

    public static class Storage implements Capability.IStorage<IWorldNpcData> {


        @Nullable
        @Override
        public INBT writeNBT(Capability<IWorldNpcData> capability, IWorldNpcData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IWorldNpcData> capability, IWorldNpcData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
