package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public class NotableNpcEntry implements INBTSerializable<CompoundNBT> {

    private BlockPos location;
    private StringTextComponent name;
    private ResourceLocation definition;
    private UUID structureId;
    private UUID spawnerId;

    public NotableNpcEntry(NpcDefinition definition, MKSpawnerTileEntity spawner){
        this.location = spawner.getPos();
        this.name = definition.getNameForEntity(spawner.getWorld(), spawner.getSpawnUUID());
        this.definition = definition.getDefinitionName();
        this.structureId = spawner.getStructureId();
        this.spawnerId = spawner.getSpawnUUID();
    }

    public NotableNpcEntry(){

    }

    @Nullable
    public NpcDefinition getDefinition(){
        return NpcDefinitionManager.getDefinition(definition);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("location", NBTUtil.writeBlockPos(location));
        tag.putUniqueId("spawnerId", spawnerId);
        tag.putUniqueId("structureId", structureId);
        tag.putString("definition", definition.toString());
        tag.putString("name", name.getUnformattedComponentText());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        location = NBTUtil.readBlockPos(nbt.getCompound("location"));
        spawnerId = nbt.getUniqueId("spawnerId");
        structureId = nbt.getUniqueId("structureId");
        definition = new ResourceLocation(nbt.getString("definition"));
        name = new StringTextComponent(nbt.getString("name"));
    }
}
