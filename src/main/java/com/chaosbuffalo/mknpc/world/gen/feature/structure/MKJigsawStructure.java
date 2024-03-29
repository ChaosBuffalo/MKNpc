package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IEntityNpcData;
import com.chaosbuffalo.mknpc.capabilities.WorldStructureManager;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.events.StructureEvent;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MKJigsawStructure extends JigsawStructure implements IControlNaturalSpawns {

    private final boolean allowSpawns;
    @Nullable
    private ITextComponent enterMessage;
    @Nullable
    private ITextComponent exitMessage;
    private final Map<String, StructureEvent> events = new HashMap<>();


    public MKJigsawStructure(Codec<VillageConfig> codec, int groundLevel, boolean offsetVertical,
                             boolean offsetFromWorldSurface, boolean allowSpawns) {
        super(codec, groundLevel, offsetVertical, offsetFromWorldSurface);
        this.allowSpawns = allowSpawns;
        enterMessage = null;
        exitMessage = null;
    }

    public MKJigsawStructure addEvent(String name, StructureEvent event) {
        event.setEventName(name);
        events.put(name, event);
        return this;
    }

    @Override
    public boolean doesAllowSpawns(){
        return allowSpawns;
    }

    public Structure.IStartFactory<VillageConfig> getStartFactory() {
        return (p_242778_1_, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_) ->
                new Start(this, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_);
    }

    public MKJigsawStructure setEnterMessage(ITextComponent msg) {
        this.enterMessage = msg;
        return this;
    }

    public MKJigsawStructure setExitMessage(ITextComponent msg) {
        this.exitMessage = msg;
        return this;
    }

    public void onStructureActivate(MKStructureEntry entry, WorldStructureManager.ActiveStructure activeStructure, World world) {
        MKNpc.LOGGER.debug("Activating structure {} (ID: {})", entry.getStructureName(), entry.getStructureId());
        for (Map.Entry<String, StructureEvent> ev : events.entrySet()) {
            if (ev.getValue().meetsRequirements(entry, activeStructure, world)) {
                entry.addActiveEvent(ev.getKey());
            }
        }
        for (String key : entry.getActiveEvents()) {
            StructureEvent ev = events.get(key);
            if (ev != null && ev.canTrigger(StructureEvent.EventTrigger.ON_ACTIVATE)) {
                checkAndExecuteEvent(ev, entry, activeStructure, world);
            }
        }
    }

    public void onStructureDeactivate(MKStructureEntry entry, WorldStructureManager.ActiveStructure activeStructure, World world) {
        MKNpc.LOGGER.debug("Deactivating structure {} (ID: {})", entry.getStructureName(), entry.getStructureId());
        for (String key : entry.getActiveEvents()) {
            StructureEvent ev = events.get(key);
            if (ev != null && ev.canTrigger(StructureEvent.EventTrigger.ON_DEACTIVATE)) {
                checkAndExecuteEvent(ev, entry, activeStructure, world);
            }
        }
        entry.clearActiveEvents();
    }

    public void onActiveTick(MKStructureEntry entry, WorldStructureManager.ActiveStructure activeStructure, World world) {
        for (String key : entry.getActiveEvents()) {
            StructureEvent ev = events.get(key);
            if (ev != null && ev.canTrigger(StructureEvent.EventTrigger.ON_TICK)) {
                checkAndExecuteEvent(ev, entry, activeStructure, world);
            }
        }

    }

    protected void checkAndExecuteEvent(StructureEvent ev, MKStructureEntry entry,
                                        WorldStructureManager.ActiveStructure activeStructure, World world) {
        if (!entry.getCooldownTracker().hasTimer(ev.getTimerName()) && ev.meetsConditions(entry, activeStructure, world)) {
            ev.execute(entry, activeStructure, world);
            entry.getCooldownTracker().setTimer(ev.getTimerName(), ev.getCooldown());
        }
    }

    public void onNpcDeath(MKStructureEntry entry, WorldStructureManager.ActiveStructure activeStructure, IEntityNpcData npcData) {
        for (String key : entry.getActiveEvents()) {
            StructureEvent ev = events.get(key);
            if (ev != null && ev.canTrigger(StructureEvent.EventTrigger.ON_DEATH)) {
                checkAndExecuteEvent(ev, entry, activeStructure, npcData.getEntity().getEntityWorld());
            }
        }
    }

    public void onPlayerEnter(ServerPlayerEntity player, MKStructureEntry structureEntry,
                              WorldStructureManager.ActiveStructure activeStructure) {
        if (getEnterMessage() != null) {
            player.sendMessage(getEnterMessage(), Util.DUMMY_UUID);
        }
    }

    public void onPlayerExit(ServerPlayerEntity player, MKStructureEntry structureEntry,
                             WorldStructureManager.ActiveStructure activeStructure) {
        if (getExitMessage() != null) {
            player.sendMessage(getExitMessage(), Util.DUMMY_UUID);
        }
    }

    @Nullable
    public ITextComponent getEnterMessage() {
        return enterMessage;
    }

    @Nullable
    public ITextComponent getExitMessage() {
        return exitMessage;
    }

    public static class Start extends MarginedStructureStart<VillageConfig> implements IAdditionalStartData {
        private final MKJigsawStructure structure;
        private UUID instanceId;

        public Start(MKJigsawStructure p_i241979_1_, int p_i241979_2_, int p_i241979_3_,
                     MutableBoundingBox p_i241979_4_, int p_i241979_5_, long seed) {
            super(p_i241979_1_, p_i241979_2_, p_i241979_3_, p_i241979_4_, p_i241979_5_, seed);
            this.structure = p_i241979_1_;
            instanceId = UUID.randomUUID();
        }

        public UUID getInstanceId() {
            return instanceId;
        }

        @Override
        public CompoundNBT write(int chunkX, int chunkZ) {
            CompoundNBT tag = super.write(chunkX, chunkZ);
            if (isValid()){
                tag.putUniqueId("instanceId", instanceId);
            }
            return tag;
        }

        public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator,
                                   TemplateManager templateManager, int chunkX, int chunkY, Biome biome,
                                   VillageConfig config) {
            BlockPos blockpos = new BlockPos(chunkX * 16, this.structure.field_242774_u, chunkY * 16);
            JigsawManager.func_242837_a(dynamicRegistries, config,
                    (tempManager, piece, pos, groundLevelDelta, rotation, boundingBox) ->
                            new MKAbstractJigsawPiece(tempManager, piece, pos, groundLevelDelta, rotation,
                                    boundingBox, getStructure().getRegistryName(), getInstanceId()),
                    chunkGenerator, templateManager, blockpos, this.components, this.rand,
                    this.structure.field_242775_v, this.structure.field_242776_w);
            this.recalculateStructureSize();
        }

        @Override
        public void readAdditional(CompoundNBT tag) {
            if (tag.contains("instanceId")){
                instanceId = tag.getUniqueId("instanceId");
            }
        }
    }
}
