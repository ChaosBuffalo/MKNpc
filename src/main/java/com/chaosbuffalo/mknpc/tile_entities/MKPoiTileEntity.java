package com.chaosbuffalo.mknpc.tile_entities;

import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.init.MKNpcTileEntityTypes;
import com.chaosbuffalo.mknpc.world.gen.IStructurePlaced;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class MKPoiTileEntity extends TileEntity implements ITickableTileEntity, IStructurePlaced {
    private ResourceLocation structureName;
    private UUID structureId;
    private UUID poiID;
    private boolean needsUploadToWorld;
    private boolean placedByStructure;
    private String tag;

    public MKPoiTileEntity(){
        this(MKNpcTileEntityTypes.MK_POI_TILE_ENTITY_TYPE.get());
    }

    public MKPoiTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.structureName = null;
        this.structureId = null;
        this.placedByStructure = false;
        this.needsUploadToWorld = false;
        this.poiID = UUID.randomUUID();
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

    public UUID getPoiID() {
        return poiID;
    }

    public void setPoiTag(String tag) {
        this.tag = tag;
    }

    public String getPoiTag() {
        return tag;
    }

    @Override
    @Nullable
    public UUID getStructureId() {
        return structureId;
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

    @Override
    public void tick() {
        World world = getWorld();
        if (world != null && !world.isRemote()) {
            if (needsUploadToWorld) {
                MinecraftServer server = world.getServer();
                if (server != null) {
                    World overworld = server.getWorld(World.OVERWORLD);
                    if (overworld != null) {
                        overworld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY)
                                .ifPresent(cap -> cap.addPointOfInterest(this));
                    }
                    world.setBlockState(getPos(), Blocks.AIR.getDefaultState(), 3);
                    needsUploadToWorld = false;
                }
            }
        }
    }

    public void regenerateId(){
        if (!placedByStructure){
            this.poiID = UUID.randomUUID();
            this.needsUploadToWorld = true;
            this.placedByStructure = true;
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putUniqueId("poiId", poiID);
        compound.putBoolean("hasUploadedToWorld", needsUploadToWorld);
        compound.putBoolean("placedByStructure", placedByStructure);
        if (isInsideStructure()){
            compound.putString("structureName", structureName.toString());
            compound.putUniqueId("structureId", structureId);
        }
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
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

        if (compound.contains("poiId")){
            poiID = compound.getUniqueId("poiId");
        } else {
            poiID = UUID.randomUUID();
        }
    }
}
