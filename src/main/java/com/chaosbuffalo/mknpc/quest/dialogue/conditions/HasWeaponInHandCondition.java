package com.chaosbuffalo.mknpc.quest.dialogue.conditions;

import com.chaosbuffalo.mkchat.dialogue.conditions.DialogueCondition;
import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

public class HasWeaponInHandCondition extends DialogueCondition {

    public static final ResourceLocation conditionTypeName = new ResourceLocation(MKNpc.MODID, "has_weapon_in_hand");

    public HasWeaponInHandCondition(){
        super(conditionTypeName);
    }

    @Override
    public boolean meetsCondition(ServerPlayerEntity player, LivingEntity source) {
        Item mainHand = player.getHeldItemMainhand().getItem();
        return mainHand instanceof SwordItem || mainHand instanceof AxeItem || mainHand instanceof ShootableItem || mainHand instanceof TridentItem;
    }
}

