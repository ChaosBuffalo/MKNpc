package com.chaosbuffalo.mknpc.dialogue.effects;

import com.chaosbuffalo.mkchat.dialogue.DialogueNode;
import com.chaosbuffalo.mkchat.dialogue.effects.DialogueEffect;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.abilities.training.IAbilityTrainingEntity;
import com.chaosbuffalo.mkcore.network.OpenLearnAbilitiesGuiPacket;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class OpenLearnAbilitiesEffect extends DialogueEffect {
    public static String effectTypeName = "open_learn_abilities";

    public OpenLearnAbilitiesEffect() {
        super(effectTypeName);
    }

    @Override
    public void applyEffect(ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity, DialogueNode dialogueNode) {
        if (livingEntity instanceof IAbilityTrainingEntity){
            MKCore.getPlayer(serverPlayerEntity).ifPresent(playerData -> {
                PacketHandler.sendMessage(new OpenLearnAbilitiesGuiPacket(playerData,
                        ((IAbilityTrainingEntity) livingEntity).getAbilityTrainer()), serverPlayerEntity);
            });
        }
    }
}
