package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.tile_entities.MKSpawnerTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.UUID;

public class NotableNpcEntry implements INBTSerializable<CompoundNBT> {

    private GlobalPos location;
    private StringTextComponent name;
    private ResourceLocation definition;
    private UUID structureId;
    private UUID spawnerId;
    private UUID notableId;

    public NotableNpcEntry(NpcDefinition definition, MKSpawnerTileEntity spawner){
        this.location = GlobalPos.getPosition(spawner.getWorld().getDimensionKey(), spawner.getPos());
        this.name = definition.getNameForEntity(spawner.getWorld(), spawner.getSpawnUUID());
        this.definition = definition.getDefinitionName();
        this.structureId = spawner.getStructureId();
        this.spawnerId = spawner.getSpawnUUID();
        this.notableId = UUID.randomUUID();
    }

    public NotableNpcEntry(){

    }

    public GlobalPos getLocation() {
        return location;
    }

    public UUID getSpawnerId() {
        return spawnerId;
    }

    public UUID getStructureId() {
        return structureId;
    }

    public UUID getNotableId() {
        return notableId;
    }

    public StringTextComponent getName() {
        return name;
    }

    @Nullable
    public NpcDefinition getDefinition(){
        return NpcDefinitionManager.getDefinition(definition);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("location", GlobalPos.CODEC.encodeStart(NBTDynamicOps.INSTANCE, getLocation())
                .getOrThrow(false, MKNpc.LOGGER::error));
        tag.putUniqueId("spawnerId", spawnerId);
        tag.putUniqueId("structureId", structureId);
        tag.putUniqueId("notableId", notableId);
        tag.putString("definition", definition.toString());
        tag.putString("name", name.getUnformattedComponentText());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        location = GlobalPos.CODEC.parse(NBTDynamicOps.INSTANCE, nbt.getCompound("location"))
                .result().orElse(GlobalPos.getPosition(World.OVERWORLD, NBTUtil.readBlockPos(nbt.getCompound("location"))));
        spawnerId = nbt.getUniqueId("spawnerId");
        structureId = nbt.getUniqueId("structureId");
        definition = new ResourceLocation(nbt.getString("definition"));
        name = new StringTextComponent(nbt.getString("name"));
        notableId = nbt.getUniqueId("notableId");
    }
}
