package com.chaosbuffalo.mknpc.quest;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.quest.objectives.*;
import com.chaosbuffalo.mknpc.quest.requirements.HasEntitlementRequirement;
import com.chaosbuffalo.mknpc.quest.requirements.QuestRequirement;
import com.chaosbuffalo.mknpc.quest.rewards.GrantEntitlementReward;
import com.chaosbuffalo.mknpc.quest.rewards.MKLootReward;
import com.chaosbuffalo.mknpc.quest.rewards.QuestReward;
import com.chaosbuffalo.mknpc.quest.rewards.XpReward;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuestDefinitionManager extends JsonReloadListener {
    public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final String DEFINITION_FOLDER = "mkquests";

    public static final ResourceLocation INVALID_QUEST = new ResourceLocation(MKNpc.MODID, "invalid_quest");

    public static final Map<ResourceLocation, QuestDefinition> DEFINITIONS = new HashMap<>();

    public static final Map<ResourceLocation, QuestObjective.Deserializer> OBJECTIVE_DESERIALIZERS = new HashMap<>();
    public static final Map<ResourceLocation, QuestReward.Deserializer> REWARD_DESERIALIZERS = new HashMap<>();
    public static final Map<ResourceLocation, QuestRequirement.Deserializer> REQUIREMENT_DESERIALIZERS = new HashMap<>();

    public QuestDefinitionManager() {
        super(GSON, DEFINITION_FOLDER);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void setObjectiveDeserializer(ResourceLocation name, QuestObjective.Deserializer deserializer) {
        OBJECTIVE_DESERIALIZERS.put(name, deserializer);
    }

    public static Optional<QuestObjective.Deserializer> getObjectiveDeserializer(ResourceLocation name) {
        return Optional.ofNullable(OBJECTIVE_DESERIALIZERS.get(name));
    }

    public static void setRequirementDeserializer(ResourceLocation name, QuestRequirement.Deserializer deserializer) {
        REQUIREMENT_DESERIALIZERS.put(name, deserializer);
    }

    public static Optional<QuestRequirement.Deserializer> getRequirementDeserializer(ResourceLocation name) {
        return Optional.ofNullable(REQUIREMENT_DESERIALIZERS.get(name));
    }

    public static void setRewardDeserializer(ResourceLocation name, QuestReward.Deserializer deserializer) {
        REWARD_DESERIALIZERS.put(name, deserializer);
    }

    public static Optional<QuestReward.Deserializer> getRewardDeserializer(ResourceLocation name) {
        return Optional.ofNullable(REWARD_DESERIALIZERS.get(name));
    }

    public static void setupDeserializers() {
        setObjectiveDeserializer(LootChestObjective.NAME, LootChestObjective::new);
        setObjectiveDeserializer(TalkToNpcObjective.NAME, TalkToNpcObjective::new);
        setObjectiveDeserializer(KillNpcDefObjective.NAME, KillNpcDefObjective::new);
        setObjectiveDeserializer(TradeItemsObjective.NAME, TradeItemsObjective::new);
        setObjectiveDeserializer(KillNotableNpcObjective.NAME, KillNotableNpcObjective::new);
        setObjectiveDeserializer(QuestLootNpcObjective.NAME, QuestLootNpcObjective::new);
        setObjectiveDeserializer(QuestLootNotableObjective.NAME, QuestLootNotableObjective::new);
        setObjectiveDeserializer(KillWithAbilityObjective.NAME, KillWithAbilityObjective::new);

        setRewardDeserializer(XpReward.TYPE_NAME, XpReward::new);
        setRewardDeserializer(MKLootReward.TYPE_NAME, MKLootReward::new);
        setRewardDeserializer(GrantEntitlementReward.TYPE_NAME, GrantEntitlementReward::new);

        setRequirementDeserializer(HasEntitlementRequirement.TYPE_NAME, HasEntitlementRequirement::new);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        DEFINITIONS.clear();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            MKNpc.LOGGER.info("Found Quest Definition file: {}", resourcelocation);
            if (resourcelocation.getPath().startsWith("_"))
                continue; //Forge: filter anything beginning with "_" as it's used for metadata.
            QuestDefinition def = new QuestDefinition(resourcelocation);
            def.deserialize(new Dynamic<>(JsonOps.INSTANCE, entry.getValue()));
            DEFINITIONS.put(def.getName(), def);
        }
    }

    public static QuestDefinition getDefinition(ResourceLocation questName) {
        return DEFINITIONS.get(questName);
    }

    @SubscribeEvent
    public void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(this);
    }

    @SubscribeEvent
    public void onDataPackSync(OnDatapackSyncEvent event) {

    }
}
