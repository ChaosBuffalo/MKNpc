package com.chaosbuffalo.mknpc.quest.rewards;

import com.chaosbuffalo.mkcore.serialization.attributes.ResourceLocationAttribute;
import com.chaosbuffalo.mkcore.utils.WorldUtils;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mkweapons.items.randomization.LootConstructor;
import com.chaosbuffalo.mkweapons.items.randomization.LootTier;
import com.chaosbuffalo.mkweapons.items.randomization.LootTierManager;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlotManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.IFormattableTextComponent;

public class MKLootReward extends QuestReward {
    public final static ResourceLocation TYPE_NAME = new ResourceLocation(MKNpc.MODID, "quest_reward.mk_loot");
    protected final ResourceLocationAttribute lootTier = new ResourceLocationAttribute("loot_tier",
            LootTierManager.INVALID_LOOT_TIER);
    protected final ResourceLocationAttribute lootSlot = new ResourceLocationAttribute("loot_slot",
            LootSlotManager.INVALID_LOOT_SLOT);

    public MKLootReward(ResourceLocation lootTier, ResourceLocation lootSlot, IFormattableTextComponent description) {
        super(TYPE_NAME, description);
        this.lootSlot.setValue(lootSlot);
        this.lootTier.setValue(lootTier);
        addAttributes(this.lootTier, this.lootSlot);
    }

    public MKLootReward() {
        super(TYPE_NAME, defaultDescription);
        addAttributes(this.lootTier, this.lootSlot);
    }

    @Override
    public void grantReward(PlayerEntity player) {
        LootTier tier = LootTierManager.getTierFromName(lootTier.getValue());
        LootSlot slot = LootSlotManager.getSlotFromName(lootSlot.getValue());
        if (tier != null && slot != null) {
            LootConstructor constructor = tier.generateConstructorForSlot(player.getRNG(), slot);
            if (constructor != null) {
                ItemStack loot = constructor.constructItem(player.getRNG(), WorldUtils.getDifficultyForGlobalPos(
                        GlobalPos.getPosition(player.getEntityWorld().getDimensionKey(), player.getPosition())));
                player.inventory.placeItemBackInInventory(player.getEntityWorld(), loot);
            }
        }
    }
}
