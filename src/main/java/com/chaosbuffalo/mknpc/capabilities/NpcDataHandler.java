package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class NpcDataHandler implements IMKNpcData {
    private LivingEntity entity;
    private NpcDefinition definition;
    private boolean mkSpawned;

    public NpcDataHandler(){
        mkSpawned = false;
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
        return definition != null ? definition.getExperiencePoints() : 0;
    }

    @Override
    public boolean wasMKSpawned() {
        return mkSpawned;
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
