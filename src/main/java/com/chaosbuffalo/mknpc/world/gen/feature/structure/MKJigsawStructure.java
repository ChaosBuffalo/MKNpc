package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IEntityNpcData;
import com.chaosbuffalo.mknpc.capabilities.WorldStructureManager;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.events.StructureEvent;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;

public class MKJigsawStructure extends JigsawFeature implements IControlNaturalSpawns {

    private final boolean allowSpawns;
    @Nullable
    private Component enterMessage;
    @Nullable
    private Component exitMessage;
    private final Map<String, StructureEvent> events = new HashMap<>();


    public MKJigsawStructure(Codec<JigsawConfiguration> codec, int groundLevel, boolean offsetVertical,
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

    @Override
    public StructureFeature.StructureStartFactory<JigsawConfiguration> getStartFactory() {
        return (p_159909_, p_159910_, p_159911_, p_159912_) -> {
            return new Start(this, p_159910_, p_159911_, p_159912_);
        };
    }


    public MKJigsawStructure setEnterMessage(Component msg) {
        this.enterMessage = msg;
        return this;
    }

    public MKJigsawStructure setExitMessage(Component msg) {
        this.exitMessage = msg;
        return this;
    }

    public void onStructureActivate(MKStructureEntry entry, WorldStructureManager.ActiveStructure activeStructure, Level world) {
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

    public void onStructureDeactivate(MKStructureEntry entry, WorldStructureManager.ActiveStructure activeStructure, Level world) {
        MKNpc.LOGGER.debug("Deactivating structure {} (ID: {})", entry.getStructureName(), entry.getStructureId());
        for (String key : entry.getActiveEvents()) {
            StructureEvent ev = events.get(key);
            if (ev != null && ev.canTrigger(StructureEvent.EventTrigger.ON_DEACTIVATE)) {
                checkAndExecuteEvent(ev, entry, activeStructure, world);
            }
        }
        entry.clearActiveEvents();
    }

    public void onActiveTick(MKStructureEntry entry, WorldStructureManager.ActiveStructure activeStructure, Level world) {
        for (String key : entry.getActiveEvents()) {
            StructureEvent ev = events.get(key);
            if (ev != null && ev.canTrigger(StructureEvent.EventTrigger.ON_TICK)) {
                checkAndExecuteEvent(ev, entry, activeStructure, world);
            }
        }

    }

    protected void checkAndExecuteEvent(StructureEvent ev, MKStructureEntry entry,
                                        WorldStructureManager.ActiveStructure activeStructure, Level world) {
        if (!entry.getCooldownTracker().hasTimer(ev.getTimerName()) && ev.meetsConditions(entry, activeStructure, world)) {
            ev.execute(entry, activeStructure, world);
            entry.getCooldownTracker().setTimer(ev.getTimerName(), ev.getCooldown());
        }
    }

    public void onNpcDeath(MKStructureEntry entry, WorldStructureManager.ActiveStructure activeStructure, IEntityNpcData npcData) {
        for (String key : entry.getActiveEvents()) {
            StructureEvent ev = events.get(key);
            if (ev != null && ev.canTrigger(StructureEvent.EventTrigger.ON_DEATH)) {
                checkAndExecuteEvent(ev, entry, activeStructure, npcData.getEntity().getCommandSenderWorld());
            }
        }
    }

    public void onPlayerEnter(ServerPlayer player, MKStructureEntry structureEntry,
                              WorldStructureManager.ActiveStructure activeStructure) {
        if (getEnterMessage() != null) {
            player.sendMessage(getEnterMessage(), Util.NIL_UUID);
        }
    }

    public void onPlayerExit(ServerPlayer player, MKStructureEntry structureEntry,
                             WorldStructureManager.ActiveStructure activeStructure) {
        if (getExitMessage() != null) {
            player.sendMessage(getExitMessage(), Util.NIL_UUID);
        }
    }

    @Nullable
    public Component getEnterMessage() {
        return enterMessage;
    }

    @Nullable
    public Component getExitMessage() {
        return exitMessage;
    }

    public static class Start extends JigsawFeature.FeatureStart implements IAdditionalStartData {
        private final MKJigsawStructure structure;
        private UUID instanceId;

        public Start(MKJigsawStructure p_159914_, ChunkPos p_159915_, int p_159916_, long p_159917_MK) {
            super(p_159914_, p_159915_, p_159916_, p_159917_MK);
            this.structure = p_159914_;
            instanceId = UUID.randomUUID();
        }

        public UUID getInstanceId() {
            return instanceId;
        }

        @Override
        public CompoundTag createTag(ServerLevel p_163607_, ChunkPos p_163608_) {
            CompoundTag tag = super.createTag(p_163607_, p_163608_);
            if (isValid()){
                tag.putUUID("instanceId", instanceId);
            }
            return tag;
        }

        public void generatePieces(RegistryAccess p_159927_, ChunkGenerator p_159928_,
                                   StructureManager p_159929_, ChunkPos p_159930_,
                                   Biome p_159931_, JigsawConfiguration p_159932_, LevelHeightAccessor p_159933_) {
            BlockPos blockpos = new BlockPos(p_159930_.getMinBlockX(), this.structure.startY, p_159930_.getMinBlockZ());
            JigsawPlacement.addPieces(p_159927_, p_159932_,
                    (tempManager, piece, pos, groundLevelDelta, rotation, boundingBox) ->
                            new MKAbstractJigsawPiece(tempManager, piece, pos, groundLevelDelta, rotation, boundingBox,
                                    getFeature().getRegistryName(), getInstanceId()),
                    p_159928_, p_159929_, blockpos, this, this.random, this.structure.doExpansionHack, this.structure.projectStartToHeightmap, p_159933_);
        }


        @Override
        public void readAdditional(CompoundTag tag) {
            if (tag.contains("instanceId")){
                instanceId = tag.getUUID("instanceId");
            }
        }
    }
}
