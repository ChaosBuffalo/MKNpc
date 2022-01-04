package com.chaosbuffalo.mknpc.dialogue;

import com.chaosbuffalo.mkchat.MKChat;
import com.chaosbuffalo.mkchat.dialogue.DialogueManager;
import com.chaosbuffalo.mkchat.dialogue.IDialogueExtension;
import com.chaosbuffalo.mkchat.json.SerializationUtils;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.dialogue.effects.OpenLearnAbilitiesEffect;
import com.chaosbuffalo.mknpc.quest.dialogue.conditions.HasGeneratedQuestsCondition;
import com.chaosbuffalo.mknpc.quest.dialogue.conditions.OnQuestChainCondition;
import com.chaosbuffalo.mknpc.quest.dialogue.conditions.OnQuestCondition;
import com.chaosbuffalo.mknpc.quest.dialogue.conditions.PendingGenerationCondition;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.AdvanceQuestChainEffect;
import com.chaosbuffalo.mknpc.quest.dialogue.effects.StartQuestChainEffect;
import net.minecraftforge.fml.InterModComms;


public class NPCDialogueExtension implements IDialogueExtension {

    public static void sendExtension() {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo(MKChat.MODID, MKChat.REGISTER_DIALOGUE_EXTENSION, NPCDialogueExtension::new);
    }

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
    }
}
