package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mknpc.inventories.PsuedoChestContainer;
import com.chaosbuffalo.mknpc.inventories.QuestChestInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChestNpcDataHandler implements IChestNpcData{
    @Nullable
    private UUID structureId;
    @Nullable
    private UUID chestId;
    private boolean needsUploadToWorld;
    private boolean placedByStructure;
    private final ChestTileEntity entity;
    @Nullable
    private String chestLabel;
    @Nullable
    private ResourceLocation structureName;

    private final HashMap<UUID, QuestChestInventory> questInventories = new HashMap<>();

    public ChestNpcDataHandler(ChestTileEntity entity) {
        this.entity = entity;
        structureId = null;
        chestId = null;
        needsUploadToWorld = false;
        placedByStructure = false;
        chestLabel = null;
        structureName = null;
    }

    public QuestChestInventory createQuestInventoryForPlayer(UUID playerId){
        QuestChestInventory inventory = new QuestChestInventory(getTileEntity());
        return inventory;
    }

    @Override
    public QuestChestInventory getQuestInventoryForPlayer(PlayerEntity player){
        return questInventories.computeIfAbsent(player.getUniqueID(), this::createQuestInventoryForPlayer);
    }

    @Override
    public boolean hasQuestInventoryForPlayer(PlayerEntity player){
        return questInventories.containsKey(player.getUniqueID());
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
        CompoundNBT questInvNbt = new CompoundNBT();
        for (Map.Entry<UUID, QuestChestInventory> entry : questInventories.entrySet()){
            questInvNbt.put(entry.getKey().toString(), entry.getValue().write());
        }
        tag.put("questInventories", questInvNbt);
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
        if (nbt.contains("questInventories")){
            questInventories.clear();
            CompoundNBT questInvNbt = nbt.getCompound("questInventories");
            for (String key : questInvNbt.keySet()){
                QuestChestInventory newInventory = new QuestChestInventory(entity);
                newInventory.read(questInvNbt.getList(key, Constants.NBT.TAG_COMPOUND));
                questInventories.put(UUID.fromString(key), newInventory);
            }
        }

    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Quest Chest");
    }

    @Nullable
    @Override
    public Container createMenu(int guiWindow, PlayerInventory playerInventory, PlayerEntity player) {
        return PsuedoChestContainer.createGeneric9X3(guiWindow, playerInventory, getQuestInventoryForPlayer(player), entity);
    }
}
