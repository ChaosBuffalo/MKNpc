package com.chaosbuffalo.mknpc.quest.rewards;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.core.entitlements.EntitlementInstance;
import com.chaosbuffalo.mkcore.core.entitlements.MKEntitlement;
import com.chaosbuffalo.mkcore.serialization.attributes.RegistryEntryAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

public class GrantEntitlementReward extends QuestReward {
    public final static ResourceLocation TYPE_NAME = new ResourceLocation(MKNpc.MODID, "quest_reward.entitlement");
    private final RegistryEntryAttribute<MKEntitlement> entitlementId =
            new RegistryEntryAttribute<>("entitlement", MKCoreRegistry.ENTITLEMENTS, MKCoreRegistry.INVALID_ENTITLEMENT);
    private MKEntitlement entitlement;

    public GrantEntitlementReward(MKEntitlement entitlement) {
        this();
        this.entitlement = entitlement;
        entitlementId.setValue(entitlement.getRegistryName());
    }

    public GrantEntitlementReward() {
        super(TYPE_NAME, defaultDescription);
        addAttribute(entitlementId);
    }

    @Override
    public IFormattableTextComponent getDescription() {
        return new TranslationTextComponent("mknpc.quest_reward.entitlement.message", entitlement.getDescription());
    }

    @Override
    protected boolean hasPersistentDescription() {
        return false;
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        entitlement = entitlementId.resolve()
                .orElseThrow(() -> new NoSuchElementException(String.format(Locale.ENGLISH, "Entitlement '%s' needed " +
                        "by quest reward was not found", entitlementId.getValue())));
    }

    @Override
    public void grantReward(PlayerEntity player) {
        if (entitlement != null) {
            MKCore.getPlayer(player).ifPresent(x -> x.getEntitlements()
                    .addEntitlement(new EntitlementInstance(entitlement, UUID.randomUUID())));
            player.sendMessage(new TranslationTextComponent("mknpc.grant_entitlement.message",
                    entitlement.getDescription()).mergeStyle(TextFormatting.GOLD), Util.DUMMY_UUID);
        }
    }
}
