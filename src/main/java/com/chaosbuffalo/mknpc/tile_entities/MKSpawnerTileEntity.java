package com.chaosbuffalo.mknpc.tile_entities;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.utils.WorldUtils;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.blocks.MKSpawnerBlock;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.init.MKNpcTileEntityTypes;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.spawn.SpawnList;
import com.chaosbuffalo.mknpc.spawn.SpawnOption;
import com.chaosbuffalo.mknpc.utils.RandomCollection;
import com.chaosbuffalo.mknpc.world.gen.IStructurePlaced;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MKSpawnerTileEntity extends TileEntity implements ITickableTileEntity, IStructurePlaced {
    private final SpawnList spawnList;
    private UUID spawnUUID;
    private Entity entity;
    private int respawnTime;
    private int ticksSinceDeath;
    private int ticksSincePlayer;
    private boolean wasAlive;
    private final static double SPAWN_RANGE = 100.0;
    private final static double DESPAWN_RANGE = 150.0;
    private static final int IDLE_TIME = GameConstants.TICKS_PER_SECOND * 10;
    private final RandomCollection<NpcDefinition> randomSpawns;
    private MKEntity.NonCombatMoveType moveType;
    private ResourceLocation structureName;
    private UUID structureId;
    private boolean needsUploadToWorld;
    private boolean placedByStructure;
    private final Map<ResourceLocation, UUID> notableIds = new HashMap<>();

    public MKSpawnerTileEntity(){
        this(MKNpcTileEntityTypes.MK_SPAWNER_TILE_ENTITY_TYPE.get());
    }

    public MKSpawnerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.spawnList = new SpawnList();
        this.spawnUUID = UUID.randomUUID();
        this.structureName = null;
        this.structureId = null;
        this.needsUploadToWorld = false;
        this.respawnTime = GameConstants.TICKS_PER_SECOND * 300;
        this.ticksSinceDeath = 0;
        this.ticksSincePlayer = 0;
        this.entity = null;
        this.wasAlive = false;
        this.moveType = MKEntity.NonCombatMoveType.STATIONARY;
        this.randomSpawns = new RandomCollection<>();
        this.placedByStructure = false;
    }

    @Override
    public boolean isInsideStructure(){
        return structureName != null && structureId != null;
    }

    @Override
    public void setStructureName(ResourceLocation structureName) {
        this.structureName = structureName;
    }

    @Override
    @Nullable
    public ResourceLocation getStructureName() {
        return structureName;
    }

    @Override
    @Nullable
    public UUID getStructureId() {
        return structureId;
    }

    public void putNotableId(ResourceLocation loc, UUID notableId){
        this.notableIds.put(loc, notableId);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return super.getCapability(cap);
    }

    @Override
    public void setStructureId(UUID structureId) {
        this.structureId = structureId;
    }

    @Override
    public GlobalPos getBlockPos() {
        return GlobalPos.getPosition(getWorld().getDimensionKey(), getPos());
    }

    @Override
    @Nullable
    public World getStructureWorld() {
        return getWorld();
    }

    public void setMoveType(MKEntity.NonCombatMoveType moveType) {
        this.moveType = moveType;
    }

    public MKEntity.NonCombatMoveType getMoveType() {
        return moveType;
    }

    public SpawnList getSpawnList() {
        return spawnList;
    }

    public void setSpawnList(SpawnList list){
        spawnList.copyList(list);
        populateRandomSpawns();
        ticksSinceDeath = 0;
    }

    public void populateRandomSpawns(){
        randomSpawns.clear();
        for (SpawnOption option : spawnList.getOptions()){
            randomSpawns.add(option.getWeight(), option.getDefinition());
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("spawnList", spawnList.serializeNBT());
        compound.putUniqueId("spawnId", spawnUUID);
        compound.putInt("ticksSinceDeath", ticksSinceDeath);
        compound.putInt("moveType", moveType.ordinal());
        compound.putBoolean("hasUploadedToWorld", needsUploadToWorld);
        compound.putBoolean("placedByStructure", placedByStructure);
        compound.putInt("respawnTime", respawnTime);
        if (isInsideStructure()){
            compound.putString("structureName", structureName.toString());
            compound.putUniqueId("structureId", structureId);
        }
        CompoundNBT notableTag = new CompoundNBT();
        for (Map.Entry<ResourceLocation, UUID> entry : notableIds.entrySet()){
            notableTag.putUniqueId(entry.getKey().toString(), entry.getValue());
        }
        compound.put("notableIds", notableTag);
        return super.write(compound);
    }

    public UUID getSpawnUUID() {
        return spawnUUID;
    }

    public void setRespawnTime(int respawnTime) {
        this.respawnTime = respawnTime;
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public boolean isSpawnAlive(){
        return entity != null && entity.isAlive();
    }

    private boolean isSpawnDead(){
        return entity instanceof LivingEntity && ((LivingEntity) entity).getHealth() <= 0 && !entity.isAlive();
    }

    public static double getDespawnRange(){
        return DESPAWN_RANGE;
    }

    public static double getSpawnRange() {
        return SPAWN_RANGE;
    }

    public void regenerateSpawnID(){
        if (!placedByStructure){
            this.spawnUUID = UUID.randomUUID();
            this.needsUploadToWorld = true;
            this.placedByStructure = true;
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        if (compound.contains("spawnList")){
            spawnList.deserializeNBT(compound.getCompound("spawnList"));
            populateRandomSpawns();
        }
        if (compound.contains("moveType")){
            setMoveType(MKEntity.NonCombatMoveType.values()[compound.getInt("moveType")]);
        }
        if (compound.contains("structureName")){
            setStructureName(new ResourceLocation(compound.getString("structureName")));
        }
        if (compound.contains("structureId")){
            setStructureId(compound.getUniqueId("structureId"));
        }
        if (compound.contains("hasUploadedToWorld")){
            needsUploadToWorld = compound.getBoolean("hasUploadedToWorld");
        }
        if (compound.contains("placedByStructure")){
            placedByStructure = compound.getBoolean("placedByStructure");
        }
        ticksSinceDeath = compound.getInt("ticksSinceDeath");
        if (compound.contains("spawnId")){
            spawnUUID = compound.getUniqueId("spawnId");
        } else {
            spawnUUID = UUID.randomUUID();
        }
        if (compound.contains("respawnTime")){
            setRespawnTime(compound.getInt("respawnTime"));
        }
        if (compound.contains("notableIds")){
            CompoundNBT notableTag = compound.getCompound("notableIds");
            for (String key : notableTag.keySet()){
                UUID notId = notableTag.getUniqueId(key);
                notableIds.put(new ResourceLocation(key), notId);
            }
        }

    }

    public void spawnEntity(){
        if (getWorld() != null){
            NpcDefinition definition = randomSpawns.next();
            Vector3d spawnPos = Vector3d.copy(getPos()).add(0.5, 0.125, 0.5);
            double difficultyValue = WorldUtils.getDifficultyForGlobalPos(
                    GlobalPos.getPosition(getWorld().getDimensionKey(), getPos()));
            switch (getWorld().getDifficulty()) {
                case EASY:
                    difficultyValue *= .5;
                    break;
                case NORMAL:
                    difficultyValue *= .75;
                    break;
                case PEACEFUL:
                    difficultyValue = 0.0;
                    break;
                default:
                    break;
            }
            Entity entity = definition.createEntity(getWorld(), spawnPos, spawnUUID, difficultyValue);
            this.entity = entity;
            if (entity != null){
                float rot = getBlockState().get(MKSpawnerBlock.ORIENTATION).getAngleInDegrees();
                entity.setPositionAndRotation(
                    entity.getPosX(),
                    entity.getPosY(),
                    entity.getPosZ(),
                    rot,
                    0.0f);
                entity.setRotationYawHead(rot);
                getWorld().addEntity(entity);
                final double finDiff = difficultyValue;
                MKNpc.getNpcData(entity).ifPresent((cap) -> {
                    cap.setMKSpawned(true);
                    cap.setSpawnPos(new BlockPos(spawnPos).up());
                    if (notableIds.containsKey(definition.getDefinitionName())){
                        cap.setNotableUUID(notableIds.get(definition.getDefinitionName()));
                    }
                    cap.setStructureId(getStructureId());
                    cap.setDifficultyValue(finDiff);
                });
                if (entity instanceof MKEntity){
                    ((MKEntity) entity).setNonCombatMoveType(getMoveType());
                }
                if (entity instanceof MobEntity && getWorld() instanceof IServerWorld){
                    ((MobEntity) entity).onInitialSpawn((IServerWorld) getWorld(), getWorld().getDifficultyForLocation(
                            entity.getPosition()), SpawnReason.SPAWNER, null, null);
                }

            }
        }
    }

    private boolean isPlayerInRange(){
        if (getWorld() == null){
            return false;
        }
        Vector3d loc = Vector3d.copy(getPos());
        for (PlayerEntity player : getWorld().getPlayers()){
            if (player.getDistanceSq(loc) < getSpawnRange() * getSpawnRange()){
                return true;
            }
        }
        return false;
    }

    private boolean isPlayerInDespawnRange(){
        if (getWorld() == null){
            return false;
        }
        Vector3d loc = Vector3d.copy(getPos());
        for (PlayerEntity player : getWorld().getPlayers()){
            if (player.getDistanceSq(loc) < getDespawnRange() * getDespawnRange()){
                return true;
            }
        }
        return false;
    }

    public void clearSpawn(){
        if (entity != null){
            entity.remove();
            entity = null;
        }
        wasAlive = false;
        ticksSinceDeath = 0;
    }

    private boolean isAir(World world, BlockPos pos){
        BlockState blockState = world.getBlockState(pos);
        return blockState.getBlock().isAir(blockState, world, pos);
    }

    @Override
    public void tick() {
        World world = getWorld();
        if (world != null && !world.isRemote() && randomSpawns.size() >0){
            if (needsUploadToWorld){
                MinecraftServer server = world.getServer();
                if (server != null){
                    World overworld = server.getWorld(World.OVERWORLD);
                    if (overworld != null){
                        overworld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY)
                                .ifPresent(cap -> cap.addSpawner(this));
                    }
                    if (!isAir(world, getPos().up())){
                        world.setBlockState(getPos().up(), Blocks.AIR.getDefaultState(), 3);
                    }
                    needsUploadToWorld = false;

                }
            }
            if (!isAir(world, getPos().up())){
                if (placedByStructure){
                    world.setBlockState(getPos().up(), Blocks.AIR.getDefaultState(), 3);
                } else {
                    return;
                }
            }
            boolean isAlive = isSpawnAlive();
            if (ticksSinceDeath > 0){
                ticksSinceDeath--;
            }

            if (isPlayerInDespawnRange()){
                if (!isAlive){
                    if (wasAlive && isSpawnDead()){
                        ticksSinceDeath = getRespawnTime();
                    }
                    if (ticksSinceDeath <= 0 && isPlayerInRange()){
                        spawnEntity();
                        isAlive = true;
                    }
                }
                wasAlive = isAlive;
                ticksSincePlayer = 0;
            } else {
                if (isAlive){
                    ticksSincePlayer++;
                    if (ticksSincePlayer > IDLE_TIME){
                        entity.remove();
                        this.entity = null;
                        ticksSinceDeath = 0;
                        wasAlive = false;
                        ticksSincePlayer = 0;
                    }
                }
            }

        }
    }
}
