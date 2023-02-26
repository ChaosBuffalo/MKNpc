package com.chaosbuffalo.mknpc.capabilities;


import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.event.WorldStructureHandler;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.MKJigsawStructure;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.*;
import java.util.function.BiConsumer;

public class WorldStructureManager {

    public class ActivePlayerEntry {
        public int ticksSinceSeen;
        public final ServerPlayerEntity player;

        public ActivePlayerEntry(ServerPlayerEntity player) {
            ticksSinceSeen = 0;
            this.player = player;
        }
    }

    public class ActiveStructure {
        private int ticksEmpty = 0;
        private final UUID structureId;
        private final Map<UUID, ActivePlayerEntry> activePlayers;
        private final int PLAYER_TIMEOUT = 20 * 5;
        private final int EMPTY_TIMEOUT = 20 * 60;
        private final BiConsumer<ServerPlayerEntity, ActiveStructure> playerRemoveCallback;

        public ActiveStructure(UUID structureId, BiConsumer<ServerPlayerEntity, ActiveStructure> removalCallback){
            this.activePlayers = new HashMap<>();
            this.structureId = structureId;
            this.playerRemoveCallback = removalCallback;
        }

        public Map<UUID, ActivePlayerEntry> getActivePlayers() {
            return activePlayers;
        }

        public UUID getStructureId() {
            return structureId;
        }

        private void addPlayer(ServerPlayerEntity player) {
            activePlayers.put(player.getUniqueID(), new ActivePlayerEntry(player));
        }

        private void removePlayer(UUID uuid) {
            ActivePlayerEntry entry = activePlayers.get(uuid);
            if (entry != null) {
                playerRemoveCallback.accept(entry.player, this);
            }
            activePlayers.remove(uuid);
        }

        // returns true if we are not already in structure
        public boolean visit(ServerPlayerEntity player) {
            if (activePlayers.containsKey(player.getUniqueID())) {
                ActivePlayerEntry active = activePlayers.get(player.getUniqueID());
                active.ticksSinceSeen = 0;
                return false;
            } else {
                addPlayer(player);
                return true;
            }
        }

        public boolean tick() {
            Set<UUID> toRemove = new HashSet<>();
            for (Map.Entry<UUID, ActivePlayerEntry> entry : activePlayers.entrySet()) {
                entry.getValue().ticksSinceSeen++;
                if (entry.getValue().ticksSinceSeen > PLAYER_TIMEOUT || !entry.getValue().player.isAlive()) {
                    toRemove.add(entry.getKey());
                }
            }
            for (UUID rem : toRemove) {
                removePlayer(rem);
            }
            if (activePlayers.isEmpty()) {
                ticksEmpty++;
            } else {
                ticksEmpty = 0;
            }
            return ticksEmpty > PLAYER_TIMEOUT;
        }


    }

    Map<UUID, ActiveStructure> activeStructures;
    private final WorldNpcDataHandler handler;

    public WorldStructureManager(WorldNpcDataHandler handler) {
        activeStructures = new HashMap<>();
        this.handler = handler;
    }

    public void visitStructure(UUID structureId, ServerPlayerEntity player) {
        ActiveStructure struct = activeStructures.computeIfAbsent(structureId, (id) -> {
            ActiveStructure activeStructure = new ActiveStructure(id, this::removePlayer);
            MKStructureEntry structureEntry = handler.getStructureData(id);
            if (structureEntry != null) {
                MKJigsawStructure mkStruct = WorldStructureHandler.MK_STRUCTURE_INDEX.get(structureEntry.getStructureName());
                if (mkStruct != null) {
                    mkStruct.onStructureActivate(structureEntry, activeStructure, handler.getWorld());
                }
            }
            return activeStructure;
        });
        if (struct.visit(player)) {
            MKStructureEntry entry = handler.getStructureData(structureId);
            if (entry != null) {
                MKNpc.LOGGER.debug("Player {} entering structure {} (ID: {})", player, entry.getStructureName(), structureId);
                MKJigsawStructure mkStruct = WorldStructureHandler.MK_STRUCTURE_INDEX.get(entry.getStructureName());
                if (mkStruct != null) {
                    mkStruct.onPlayerEnter(player, entry, struct);
                }
            }
        }
    }

    public void removePlayer(ServerPlayerEntity player, ActiveStructure activeStructure) {
        MKStructureEntry entry = handler.getStructureData(activeStructure.getStructureId());
        if (player != null && entry != null) {
            MKNpc.LOGGER.debug("Player {} exiting structure {} (ID: {})", player, entry.getStructureName(), activeStructure.getStructureId());
            MKJigsawStructure mkStruct = WorldStructureHandler.MK_STRUCTURE_INDEX.get(entry.getStructureName());
            if (mkStruct != null) {
                mkStruct.onPlayerExit(player, entry, activeStructure);
            }
        }

    }

    public void onNpcDeath(IEntityNpcData npcData) {
        npcData.getStructureId().ifPresent(structureId -> {
            if (activeStructures.containsKey(structureId)) {
                MKStructureEntry entry = handler.getStructureData(structureId);
                ActiveStructure activeStruct = activeStructures.get(structureId);
                if (entry != null && activeStruct != null) {
                    MKJigsawStructure mkStruct = WorldStructureHandler.MK_STRUCTURE_INDEX.get(entry.getStructureName());
                    if (mkStruct != null) {
                        mkStruct.onNpcDeath(entry, activeStruct, npcData);
                    }
                }
            }
        });

    }

    public void tick(){
        if (activeStructures.isEmpty()) {
            return;
        }
        Set<UUID> toRemove = new HashSet<>();
        for (Map.Entry<UUID, ActiveStructure> entry : activeStructures.entrySet()) {
            if (entry.getValue().tick()) {
                toRemove.add(entry.getKey());
            }
            MKStructureEntry structureEntry = handler.getStructureData(entry.getValue().structureId);
            if (structureEntry != null) {
                MKJigsawStructure mkStruct = WorldStructureHandler.MK_STRUCTURE_INDEX.get(structureEntry.getStructureName());
                if (mkStruct != null) {
                    mkStruct.onActiveTick(structureEntry, entry.getValue(), handler.getWorld());
                }
            }
        }
        for (UUID structId : toRemove) {
            MKStructureEntry structureEntry = handler.getStructureData(structId);
            if (structureEntry != null) {
                MKJigsawStructure mkStruct = WorldStructureHandler.MK_STRUCTURE_INDEX.get(structureEntry.getStructureName());
                if (mkStruct != null) {
                    mkStruct.onStructureDeactivate(structureEntry, activeStructures.get(structId), handler.getWorld());
                }
            }
            activeStructures.remove(structId);
        }
    }
}
