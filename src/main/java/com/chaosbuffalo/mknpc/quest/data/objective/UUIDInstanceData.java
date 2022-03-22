package com.chaosbuffalo.mknpc.quest.data.objective;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;

import java.util.UUID;

public class UUIDInstanceData extends ObjectiveInstanceData {

    private UUID uuid;
    private boolean isValid;

    public UUIDInstanceData() {
        uuid = Util.DUMMY_UUID;
        isValid = false;
    }

    public UUIDInstanceData(UUID uuid) {
        this.uuid = uuid;
        isValid = true;
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putUniqueId("id", uuid);
        tag.putBoolean("isValid", isValid);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        uuid = nbt.getUniqueId("id");
        isValid = nbt.getBoolean("isValid");
    }
}
