package com.chaosbuffalo.mknpc.capabilities;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.UUID;

public class ChestNpcDataHandler implements IChestNpcData{
    @Nullable
    private UUID structureId;
    @Nullable
    private UUID chestId;
    private boolean needsUploadToWorld;
    private boolean placedByStructure;
    private ChestTileEntity entity;
    @Nullable
    private String chestLabel;
    @Nullable
    private ResourceLocation structureName;

    public ChestNpcDataHandler(){
        structureId = null;
        chestId = null;
        needsUploadToWorld = false;
        placedByStructure = false;
        chestLabel = null;
        structureName = null;
    }


    @Nullable
    @Override
    public UUID getChestId() {
        return chestId;
    }

    @Nullable
    @Override
    public String getChestLabel() {
        return chestLabel;
    }

    @Override
    public boolean isInsideStructure() {
        return structureName != null && structureId != null;
    }

    @Nullable
    @Override
    public UUID getStructureId() {
        return structureId;
    }

    @Nullable
    @Override
    public ResourceLocation getStructureName() {
        return structureName;
    }

    @Override
    public void setStructureName(ResourceLocation name) {
        structureName = name;
    }

    @Override
    public void setStructureId(UUID id) {
        structureId = id;
    }

    @Override
    public BlockPos getBlockPos() {
        return entity.getPos();
    }

    @Nullable
    @Override
    public World getStructureWorld() {
        return getTileEntity().getWorld();
    }

    @Override
    public void attach(ChestTileEntity entity) {
        this.entity = entity;
    }

    @Override
    public ChestTileEntity getTileEntity() {
        return entity;
    }

    @Override
    public void generateChestId(String chestLabel) {
        needsUploadToWorld = true;
        placedByStructure = true;
        chestId = UUID.randomUUID();
        this.chestLabel = chestLabel;
    }

    @Override
    public void tick() {
        if (needsUploadToWorld){
            World world = getTileEntity().getWorld();
            if (world != null && !world.isRemote()) {
                MinecraftServer server = world.getServer();
                if (server != null){
                    World overworld = server.getWorld(World.OVERWORLD);
                    if (overworld != null){
                        overworld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY)
                                .ifPresent(cap -> cap.addChest(this));
                    }
                    needsUploadToWorld = false;
                }
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putBoolean("placedByStructure", placedByStructure);
        tag.putBoolean("needsUploadToWorld", needsUploadToWorld);
        if (chestId != null){
            tag.putUniqueId("chestId", chestId);
        }
        if (structureId != null){
            tag.putUniqueId("structureId", structureId);
        }
        if (chestLabel != null){
            tag.putString("chestLabel", chestLabel);
        }
        if (structureName != null){
            tag.putString("structureName", structureName.toString());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        placedByStructure = nbt.getBoolean("placedByStructure");
        needsUploadToWorld = nbt.getBoolean("needsUploadToWorld");
        if (nbt.contains("chestId")){
            chestId = nbt.getUniqueId("chestId");
        }
        if (nbt.contains("structureId")){
            structureId = nbt.getUniqueId("structureId");
        }
        if (nbt.contains("chestLabel")){
            chestLabel = nbt.getString("chestLabel");
        }
        if (nbt.contains("structureName")){
            structureName = new ResourceLocation(nbt.getString("structureName"));
        }

    }

    public static class Storage implements Capability.IStorage<IChestNpcData> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<IChestNpcData> capability, IChestNpcData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IChestNpcData> capability, IChestNpcData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
