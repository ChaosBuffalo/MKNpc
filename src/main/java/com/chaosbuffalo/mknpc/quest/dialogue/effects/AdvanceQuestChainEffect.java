package com.chaosbuffalo.mknpc.quest.dialogue.effects;

import com.chaosbuffalo.mkchat.dialogue.DialogueNode;
import com.chaosbuffalo.mkchat.dialogue.effects.DialogueEffect;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IPlayerQuestingData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.UUID;

public class AdvanceQuestChainEffect extends DialogueEffect implements IReceivesChainId {
    public static ResourceLocation effectTypeName = new ResourceLocation(MKNpc.MODID, "advance_quest_chain");
    private UUID chainId;

    public AdvanceQuestChainEffect(UUID chainId){
        this();
        this.chainId = chainId;
    }

    public AdvanceQuestChainEffect() {
        super(effectTypeName);
        chainId = UUID.randomUUID();
    }

    @Override
    public void setChainId(UUID chainId) {
        this.chainId = chainId;
    }

    @Override
    public void applyEffect(ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity, DialogueNode dialogueNode) {
        MinecraftServer server = serverPlayerEntity.getServer();
        if (server == null){
            return;
        }
        World overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null){
            return;
        }
        IPlayerQuestingData questingData = MKNpc.getPlayerQuestData(serverPlayerEntity).resolve().orElse(null);
        if (questingData == null){
            return;
        }
        overworld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY).ifPresent(x ->{
            QuestChainInstance questChain = x.getQuest(chainId);
            if (questChain == null){
                return;
            }
            questingData.getQuestChain(chainId).ifPresent(playerChain -> {
                for (String questName : playerChain.getCurrentQuests()){
                    Quest currentQuest = questChain.getDefinition().getQuest(questName);
                    if (currentQuest == null){
                        continue;
                    }
                    questChain.signalQuestProgress(x, questingData, currentQuest, playerChain, true);
                }

            });
        });
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        chainId = dynamic.get("chainId").asString().result().map(UUID::fromString).orElse(UUID.randomUUID());
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        if (!(ops instanceof JsonOps)){
            builder.put(ops.createString("chainId"), ops.createString(chainId.toString()));
        }
    }
}
