package com.chaosbuffalo.mknpc.dialogue;

import com.chaosbuffalo.mkchat.MKChat;
import com.chaosbuffalo.mkchat.dialogue.ContextAwareTextComponent;
import com.chaosbuffalo.mkchat.dialogue.DialogueManager;
import com.chaosbuffalo.mkchat.dialogue.DialogueTree;
import com.chaosbuffalo.mkchat.dialogue.IDialogueExtension;
import com.chaosbuffalo.mkchat.json.SerializationUtils;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.dialogue.effects.OpenLearnAbilitiesEffect;
import com.chaosbuffalo.mknpc.quest.dialogue.conditions.*;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.AdvanceQuestChainEffect;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.GrantEntitlementEffect;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.ObjectiveCompleteEffect;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.StartQuestChainEffect;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.UUID;
import java.util.function.BiFunction;


public class NPCDialogueExtension implements IDialogueExtension {

    public static void sendExtension() {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo(MKChat.MODID, MKChat.REGISTER_DIALOGUE_EXTENSION, NPCDialogueExtension::new);
    }



    private static final BiFunction<String, DialogueTree, ITextComponent> notableProvider =
            (name, tree) -> {
                 return new ContextAwareTextComponent("%s", (context) -> {
                     if (context.getPlayer().getServer() != null){
                         World overworld = context.getPlayer().getServer().getWorld(World.OVERWORLD);
                         if (overworld != null){
                             return Collections.singletonList(overworld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY).map(x ->
                                     x.getNotableNpc(UUID.fromString(name)).getName()).orElse(
                                     new StringTextComponent(String.format("notable:%s", name))));
                         }
                     }
                     return Collections.singletonList(new StringTextComponent(String.format("notable:%s", name)));
                 });
            };

    @Override
    public void registerDialogueExtension() {
        MKNpc.LOGGER.info("Registering MKNpc Dialogue Extension");
        DialogueManager.putEffectDeserializer(OpenLearnAbilitiesEffect.effectTypeName, OpenLearnAbilitiesEffect::new);
        DialogueManager.putConditionDeserializer(OnQuestCondition.conditionTypeName, OnQuestCondition::new);
        DialogueManager.putConditionDeserializer(OnQuestChainCondition.conditionTypeName, OnQuestChainCondition::new);
        DialogueManager.putConditionDeserializer(PendingGenerationCondition.conditionTypeName, PendingGenerationCondition::new);
        DialogueManager.putConditionDeserializer(HasGeneratedQuestsCondition.conditionTypeName, HasGeneratedQuestsCondition::new);
        DialogueManager.putEffectDeserializer(AdvanceQuestChainEffect.effectTypeName, AdvanceQuestChainEffect::new);
        DialogueManager.putEffectDeserializer(StartQuestChainEffect.effectTypeName, StartQuestChainEffect::new);
        DialogueManager.putEffectDeserializer(ObjectiveCompleteEffect.effectTypeName, ObjectiveCompleteEffect::new);
        DialogueManager.putConditionDeserializer(HasWeaponInHandCondition.conditionTypeName, HasWeaponInHandCondition::new);
        DialogueManager.putConditionDeserializer(HasSpentTalentPointsCondition.conditionTypeName, HasSpentTalentPointsCondition::new);
        DialogueManager.putEffectDeserializer(GrantEntitlementEffect.effectTypeName, GrantEntitlementEffect::new);
        DialogueManager.putConditionDeserializer(HasTrainedAbilitiesCondition.conditionTypeName, HasTrainedAbilitiesCondition::new);
        DialogueManager.putConditionDeserializer(ObjectivesCompleteCondition.conditionTypeName, ObjectivesCompleteCondition::new);
        DialogueManager.putConditionDeserializer(HasEntitlementCondition.conditionTypeName, HasEntitlementCondition::new);
        DialogueManager.putConditionDeserializer(CanStartQuestCondition.conditionTypeName, CanStartQuestCondition::new);
        DialogueManager.putTextComponentProvider("notable", notableProvider);
    }
}
