package com.chaosbuffalo.mknpc.spawn;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.utils.RandomCollection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class MKSpawnerTileEntity extends TileEntity implements ITickableTileEntity {
    private final SpawnList spawnList;
    private UUID spawnUUID;
    private Entity entity;
    private int respawnTime;
    private int ticksSinceDeath;
    private int ticksSincePlayer;
    private final static double SPAWN_RANGE = 100.0;
    private static int IDLE_TIME = GameConstants.TICKS_PER_SECOND * 60;
    private final RandomCollection<NpcDefinition> randomSpawns;

    public MKSpawnerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.spawnList = new SpawnList();
        this.spawnUUID = UUID.randomUUID();
        this.respawnTime = GameConstants.TICKS_PER_SECOND * 30;
        this.ticksSinceDeath = 0;
        this.ticksSincePlayer = 0;
        this.entity = null;
        this.randomSpawns = new RandomCollection<>();
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
        return super.write(compound);
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

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        if (compound.contains("spawnList")){
            spawnList.deserializeNBT(compound.getCompound("spawnList"));
            populateRandomSpawns();
        }
        spawnUUID = compound.getUniqueId("spawnId");
    }

    public void spawnEntity(){
        if (getWorld() != null){
            NpcDefinition definition = randomSpawns.next();
            Entity entity = definition.createEntity(getWorld(), new Vec3d(getPos()));
            this.entity = entity;
            if (entity != null){
                entity.setUniqueId(spawnUUID);
                getWorld().addEntity(entity);
            }
        }
    }

    private boolean isPlayerInRange(){
        if (getWorld() == null){
            return false;
        }
        Vec3d loc = new Vec3d(getPos());
        for (PlayerEntity player : getWorld().getPlayers()){
            if (player.getDistanceSq(loc) < getSpawnRange() * getSpawnRange()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick() {
        if (isPlayerInRange()){
            if (!isSpawnAlive()){
                ticksSinceDeath++;
                if (ticksSinceDeath >= getRespawnTime()){
                    spawnEntity();
                    ticksSinceDeath = 0;
                }
            } else {
                ticksSincePlayer = 0;
            }
        } else {
            if (isSpawnAlive()){
                ticksSincePlayer++;
                if (ticksSincePlayer > IDLE_TIME){
                    entity.remove();
                    ticksSincePlayer = 0;
                }
            }
        }
    }
}
