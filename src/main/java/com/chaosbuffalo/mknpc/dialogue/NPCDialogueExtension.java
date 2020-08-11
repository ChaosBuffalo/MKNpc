package com.chaosbuffalo.mknpc.dialogue;

import com.chaosbuffalo.mkchat.MKChat;
import com.chaosbuffalo.mkchat.dialogue.DialogueManager;
import com.chaosbuffalo.mkchat.dialogue.IDialogueExtension;
import com.chaosbuffalo.mkchat.json.SerializationUtils;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.dialogue.effects.OpenLearnAbilitiesEffect;
import net.minecraftforge.fml.InterModComms;


public class NPCDialogueExtension implements IDialogueExtension {

    public static void sendExtension() {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo(MKChat.MODID, MKChat.REGISTER_DIALOGUE_EXTENSION,
                NPCDialogueExtension::new);
    }

    @Override
    public void registerDialogueExtension() {
        MKNpc.LOGGER.info("Registering MKNpc Dialogue Extension");
        DialogueManager.putEffectDeserializer(OpenLearnAbilitiesEffect.effectTypeName,
                SerializationUtils.deserialize(OpenLearnAbilitiesEffect.class));
    }
}
