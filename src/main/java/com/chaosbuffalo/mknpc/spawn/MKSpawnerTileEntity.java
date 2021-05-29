package com.chaosbuffalo.mknpc.spawn;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.init.MKNpcTileEntityTypes;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.utils.RandomCollection;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class MKSpawnerTileEntity extends TileEntity implements ITickableTileEntity {
    private final SpawnList spawnList;
    private UUID spawnUUID;
    private Entity entity;
    private int respawnTime;
    private int ticksSinceDeath;
    private int ticksSincePlayer;
    private boolean wasAlive;
    private final static double SPAWN_RANGE = 75.0;
    private static final int IDLE_TIME = GameConstants.TICKS_PER_SECOND * 10;
    private final RandomCollection<NpcDefinition> randomSpawns;
    private MKEntity.NonCombatMoveType moveType;
    private ResourceLocation structureName;
    private UUID structureId;
    private boolean needsUploadToWorld;

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
        this.respawnTime = GameConstants.TICKS_PER_SECOND * 25;
        this.ticksSinceDeath = 0;
        this.ticksSincePlayer = 0;
        this.entity = null;
        this.wasAlive = false;
        this.moveType = MKEntity.NonCombatMoveType.STATIONARY;
        this.randomSpawns = new RandomCollection<>();
    }

    public boolean isInsideStructure(){
        return structureName != null && structureId != null;
    }

    public void setStructureName(ResourceLocation structureName) {
        this.structureName = structureName;
    }

    public ResourceLocation getStructureName() {
        return structureName;
    }

    public UUID getStructureId() {
        return structureId;
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

    public void setStructureId(UUID structureId) {
        this.structureId = structureId;
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
        if (isInsideStructure()){
            compound.putString("structureName", structureName.toString());
            compound.putUniqueId("structureId", structureId);
        }
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

    public static double getSpawnRange() {
        return SPAWN_RANGE;
    }

    public void regenerateSpawnID(){
        this.spawnUUID = UUID.randomUUID();
        this.needsUploadToWorld = true;
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
        ticksSinceDeath = compound.getInt("ticksSinceDeath");
        spawnUUID = compound.getUniqueId("spawnId");
    }

    public void spawnEntity(){
        if (getWorld() != null){
            NpcDefinition definition = randomSpawns.next();
            Vector3d spawnPos = Vector3d.copy(getPos()).add(0.5, 0.0630, 0.5);
            Entity entity = definition.createEntity(getWorld(), spawnPos, spawnUUID);
            this.entity = entity;
            if (entity != null){
                getWorld().addEntity(entity);
                MKNpc.getNpcData(entity).ifPresent((cap) -> {
                    cap.setMKSpawned(true);
                    cap.setSpawnPos(new BlockPos(spawnPos).up());
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

    public void clearSpawn(){
        if (entity != null){
            entity.remove();
            entity = null;
        }
        wasAlive = false;
        ticksSinceDeath = 0;
    }

    @Override
    public void tick() {
        if (getWorld() != null && !getWorld().isRemote() && randomSpawns.size() >0){
            if (needsUploadToWorld){
                getWorld().getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY)
                        .ifPresent(cap -> cap.addSpawner(this));
                needsUploadToWorld = false;
            }
            if (!getWorld().getBlockState(getPos().up()).getBlock().equals(Blocks.AIR)){
                return;
            }
            boolean isAlive = isSpawnAlive();
            if (ticksSinceDeath > 0){
                ticksSinceDeath--;
            }

            if (isPlayerInRange()){
                if (!isAlive){
                    if (wasAlive){
                        ticksSinceDeath = getRespawnTime();
                    }
                    if (ticksSinceDeath <= 0){
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
                        wasAlive = false;
                    }
                }
            }

        }
    }
}
