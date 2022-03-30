package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public class NotableChestEntry implements INBTSerializable<CompoundNBT> {

    private GlobalPos location;
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

    public GlobalPos getLocation() {
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
        tag.put("location", GlobalPos.CODEC.encodeStart(NBTDynamicOps.INSTANCE, getLocation())
                .getOrThrow(false, MKNpc.LOGGER::error));
        tag.putUniqueId("chestId", chestId);
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
        chestId = nbt.getUniqueId("chestId");
        structureId = nbt.getUniqueId("structureId");
        if (nbt.contains("label")){
            label = nbt.getString("label");
        }
    }
}
