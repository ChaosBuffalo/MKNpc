package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.tile_entities.MKPoiTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public class PointOfInterestEntry implements INBTSerializable<CompoundNBT> {

    private GlobalPos location;
    private String label;
    private UUID structureId;
    private UUID pointId;

    public PointOfInterestEntry(MKPoiTileEntity entity) {
        this.location = entity.getBlockPos();
        this.label = entity.getPoiTag();
        this.structureId = entity.getStructureId();
        this.pointId = entity.getPoiID();
    }

    public PointOfInterestEntry() {

    }

    public GlobalPos getLocation() {
        return location;
    }

    public UUID getPointId() {
        return pointId;
    }

    @Nullable
    public String getLabel() {
        return label;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("location", GlobalPos.CODEC.encodeStart(NBTDynamicOps.INSTANCE, getLocation())
                .getOrThrow(false, MKNpc.LOGGER::error));
        tag.putUniqueId("pointId", pointId);
        tag.putUniqueId("structureId", structureId);
        if (label != null) {
            tag.putString("label", label);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        location = GlobalPos.CODEC.parse(NBTDynamicOps.INSTANCE, nbt.getCompound("location"))
                .result().orElse(GlobalPos.getPosition(World.OVERWORLD, NBTUtil.readBlockPos(nbt.getCompound("location"))));
        pointId = nbt.getUniqueId("pointId");
        structureId = nbt.getUniqueId("structureId");
        if (nbt.contains("label")){
            label = nbt.getString("label");
        }
    }
}
