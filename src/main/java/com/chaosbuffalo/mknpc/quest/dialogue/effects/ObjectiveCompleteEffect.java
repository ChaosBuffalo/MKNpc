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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ObjectiveCompleteEffect extends DialogueEffect implements IReceivesChainId{
    public static ResourceLocation effectTypeName = new ResourceLocation(MKNpc.MODID, "objective_completion");
    private UUID chainId;
    private String objectiveName;
    private String questName;

    public ObjectiveCompleteEffect(UUID chainId, String objectiveName, String questName){
        this();
        this.chainId = chainId;
        this.objectiveName = objectiveName;
        this.questName = questName;
    }

    public ObjectiveCompleteEffect(String objectiveName, String questName){
        this(UUID.randomUUID(), objectiveName, questName);
    }

    public ObjectiveCompleteEffect() {
        super(effectTypeName);
        chainId = UUID.randomUUID();
        objectiveName = "invalid";
        questName = "default";
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
                Quest currentQuest = questChain.getDefinition().getQuest(questName);
                if (currentQuest == null){
                    return;
                }
                questChain.signalObjectiveComplete(objectiveName, x, questingData, currentQuest, playerChain);
            });
        });
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        chainId = dynamic.get("chainId").asString().result().map(UUID::fromString).orElse(UUID.randomUUID());
        objectiveName = dynamic.get("objectiveName").asString("invalid");
        questName = dynamic.get("questName").asString("defualt");
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup, ImmutableMap.of(
                ops.createString("chainId"), ops.createString(chainId.toString()),
                ops.createString("objectiveName"), ops.createString(objectiveName),
                ops.createString("questName"), ops.createString(questName)
        )).result().orElse(sup);
    }
}
