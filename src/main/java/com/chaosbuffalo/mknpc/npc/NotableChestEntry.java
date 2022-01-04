package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public class NotableChestEntry implements INBTSerializable<CompoundNBT> {

    private BlockPos location;
    @Nullable
    private String label;
    private UUID structureId;
    private UUID chestId;

    public NotableChestEntry(IChestNpcData data) {
        this.location = data.getBlockPos();
        this.label = data.getChestLabel();
        this.structureId = data.getStructureId();
        this.chestId = data.getChestId();
    }

    public NotableChestEntry() {

    }

    public BlockPos getLocation() {
        return location;
    }

    public UUID getChestId() {
        return chestId;
    }

    @Nullable
    public String getLabel() {
        return label;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("location", NBTUtil.writeBlockPos(location));
        tag.putUniqueId("chestId", chestId);
        tag.putUniqueId("structureId", structureId);
        if (label != null) {
            tag.putString("label", label);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        location = NBTUtil.readBlockPos(nbt.getCompound("location"));
        chestId = nbt.getUniqueId("chestId");
        structureId = nbt.getUniqueId("structureId");
        if (nbt.contains("label")){
            label = nbt.getString("label");
        }
    }
}
