package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IEntityNpcData;
import com.chaosbuffalo.mknpc.capabilities.WorldStructureManager;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.events.StructureEvent;
import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class MKJigsawStructure extends JigsawFeature implements IControlNaturalSpawns {

    private final boolean allowSpawns;
    @Nullable
    private Component enterMessage;
    @Nullable
    private Component exitMessage;
    private final Map<String, StructureEvent> events = new HashMap<>();


    //Codec<JigsawConfiguration> p_197092_, int p_197093_, boolean p_197094_,
    // boolean p_197095_, Predicate<PieceGeneratorSupplier.Context<JigsawConfiguration>> p_197096_
    public MKJigsawStructure(Codec<JigsawConfiguration> codec, int groundLevel, boolean offsetVertical,
                             boolean offsetFromWorldSurface,
                             Predicate<PieceGeneratorSupplier.Context<JigsawConfiguration>> pieceSupplier,
                             boolean allowSpawns) {
        super(codec, groundLevel, offsetVertical, offsetFromWorldSurface, pieceSupplier);
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

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    @Nullable
    public Component getEnterMessage() {
        return enterMessage;
    }

    @Nullable
    public Component getExitMessage() {
        return exitMessage;
    }
}
