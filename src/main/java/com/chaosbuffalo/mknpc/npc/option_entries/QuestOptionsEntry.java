package com.chaosbuffalo.mknpc.npc.option_entries;

import com.chaosbuffalo.mkchat.capabilities.ChatCapabilities;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.npc.entries.QuestOfferingEntry;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.QuestDefinition;
import com.chaosbuffalo.mknpc.quest.QuestDefinitionManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class QuestOptionsEntry implements INpcOptionEntry{

    private Map<ResourceLocation, QuestOfferingEntry> questOfferings = new HashMap<>();

    public QuestOptionsEntry(List<ResourceLocation> locs){
        for (ResourceLocation loc : locs){
            questOfferings.put(loc, new QuestOfferingEntry(loc));
        }
    }

    public QuestOptionsEntry(){

    }

    @Override
    public void applyToEntity(Entity entity) {
//        BlockPos pos = new BlockPos(entity.getPositionVec());
//        for (QuestOfferingEntry entry : questOfferings.values()){
//            if (entry.getQuestId() == null){
//                QuestDefinition definition = QuestDefinitionManager.getDefinition(entry.getQuestDef());
//                if (definition != null) {
//                    MinecraftServer server = entity.getServer();
//                    if (server != null) {
//                        World world = server.getWorld(World.OVERWORLD);
//                        if (world != null) {
//                            Optional<QuestChainInstance> quest = world.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY)
//                                    .map(x -> x.buildQuest(definition, pos)).orElse(Optional.empty());
//                            if (quest.isPresent()) {
//                                QuestChainInstance newQuest = quest.get();
//                                MKNpc.getNpcData(entity).ifPresent(x -> newQuest.setQuestSourceNpc(x.getSpawnID()));
//                                entry.setQuestId(newQuest.getQuestId());
//                            }
//                        }
//                    }
//                }
//            }
//        }
        MKNpc.getNpcData(entity).ifPresent(x -> {
            x.putShouldHaveQuest(true);
            for (QuestOfferingEntry entry : questOfferings.values()){
                x.requestQuest(entry);
            }
        });
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT offeringNbt = new ListNBT();
        for (QuestOfferingEntry entry : questOfferings.values()){
            offeringNbt.add(entry.serializeNBT());
        }
        nbt.put("offerings", offeringNbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT offeringNbt = nbt.getList("offerings", Constants.NBT.TAG_COMPOUND);
        for (INBT offering : offeringNbt){
            QuestOfferingEntry newEntry = new QuestOfferingEntry((CompoundNBT) offering);
            questOfferings.put(newEntry.getQuestDef(), newEntry);
        }
    }
}
