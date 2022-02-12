package com.chaosbuffalo.mknpc.quest.rewards;

import com.chaosbuffalo.mkcore.serialization.attributes.IntAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class XpReward extends QuestReward {
    public final static ResourceLocation TYPE_NAME = new ResourceLocation(MKNpc.MODID, "quest_reward.xp");
    protected final IntAttribute xpAmount = new IntAttribute("xp", 0);


    public XpReward(int xp) {
        super(TYPE_NAME, defaultDescription);
        addAttribute(xpAmount);
        xpAmount.setValue(xp);
    }

    public XpReward() {
        this(0);
    }

    @Override
    public IFormattableTextComponent getDescription() {
        return new TranslationTextComponent("mknpc.quest_reward.xp.name", xpAmount.value());
    }

    @Override
    protected boolean hasPersistentDescription() {
        return false;
    }

    @Override
    public void grantReward(PlayerEntity player) {
        player.giveExperiencePoints(xpAmount.value());
        player.sendMessage(new TranslationTextComponent("mknpc.quest_reward.xp.message", xpAmount.value())
                .mergeStyle(TextFormatting.GOLD), Util.DUMMY_UUID);
    }
}
