package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class EntityNpcDataHandler implements IEntityNpcData {
    private LivingEntity entity;
    private NpcDefinition definition;
    private boolean mkSpawned;
    private int bonusXp;
    private UUID spawnID;
    private BlockPos blockPos;
    private boolean notable;
    private boolean needsDefinitionApplied;

    public EntityNpcDataHandler(){
        mkSpawned = false;
        bonusXp = 0;
        notable = false;
        spawnID = UUID.randomUUID();
        needsDefinitionApplied = false;
    }

    public boolean needsDefinitionApplied() {
        return needsDefinitionApplied;
    }

    public void applyDefinition(){
        if (definition != null){
            definition.applyDefinition(getEntity());
            needsDefinitionApplied = false;
        }
    }

    @Override
    public void attach(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public NpcDefinition getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(NpcDefinition definition) {
        this.definition = definition;
    }

    @Override
    public int getBonusXp() {
        return bonusXp;
    }

    @Override
    public void setBonusXp(int value) {
        this.bonusXp = value;
    }

    @Override
    public boolean wasMKSpawned() {
        return mkSpawned;
    }

    @Override
    public void setSpawnPos(BlockPos pos) {
        this.blockPos = pos;
    }

    @Override
    public BlockPos getSpawnPos() {
        return blockPos;
    }

    @Override
    public void setSpawnID(UUID id) {
        spawnID = id;
    }

    @Nonnull
    @Override
    public UUID getSpawnID() {
        return spawnID;
    }

    @Override
    public void setMKSpawned(boolean value) {
        this.mkSpawned = value;
    }

    @Override
    public boolean isNotable() {
        return notable;
    }

    @Override
    public void setNotable(boolean value) {
        notable = value;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        if (definition != null){
            tag.putString("npc_definition", definition.getDefinitionName().toString());
        }
        tag.putUniqueId("spawn_id", spawnID);
        tag.putBoolean("mk_spawned", mkSpawned);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("mk_spawned")){
            mkSpawned = nbt.getBoolean("mk_spawned");
        }
        if (nbt.contains("spawn_id")){
            spawnID = nbt.getUniqueId("spawn_id");
        }
        if (nbt.contains("npc_definition")){
            ResourceLocation defName = new ResourceLocation(nbt.getString("npc_definition"));
            NpcDefinition def = NpcDefinitionManager.getDefinition(defName);
            this.definition = def;
            needsDefinitionApplied = true;
        }
    }

    public static class Storage implements Capability.IStorage<IEntityNpcData> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<IEntityNpcData> capability, IEntityNpcData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IEntityNpcData> capability, IEntityNpcData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
