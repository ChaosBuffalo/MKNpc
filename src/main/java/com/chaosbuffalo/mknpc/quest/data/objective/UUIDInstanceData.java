package com.chaosbuffalo.mknpc.quest.data.objective;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;

import java.util.UUID;

public class UUIDInstanceData extends ObjectiveInstanceData {

    private UUID uuid;

    public UUIDInstanceData() {
        uuid = Util.DUMMY_UUID;
    }

    public UUIDInstanceData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putUniqueId("id", uuid);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        uuid = nbt.getUniqueId("id");
    }
}
