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

public class NpcDataHandler implements IMKNpcData {
    private LivingEntity entity;
    private NpcDefinition definition;
    private boolean mkSpawned;
    private int bonusXp;
    private UUID spawnID;
    private BlockPos blockPos;

    public NpcDataHandler(){
        mkSpawned = false;
        bonusXp = 0;
        spawnID = UUID.randomUUID();
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
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        if (definition != null){
            tag.putString("npc_definition", definition.getDefinitionName().toString());
        }
        tag.putBoolean("mk_spawned", mkSpawned);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("npc_definition")){
            ResourceLocation defName = new ResourceLocation(nbt.getString("npc_definition"));
            NpcDefinition def = NpcDefinitionManager.getDefinition(defName);
            this.definition = def;
            if (def != null){
                def.applyDefinition(getEntity());
            }
        }
        if (nbt.contains("mk_spawned")){
            mkSpawned = nbt.getBoolean("mk_spawned");
        }
    }

    public static class Storage implements Capability.IStorage<IMKNpcData> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<IMKNpcData> capability, IMKNpcData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IMKNpcData> capability, IMKNpcData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
