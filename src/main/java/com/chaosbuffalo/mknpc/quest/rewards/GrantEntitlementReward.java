package com.chaosbuffalo.mknpc.quest.rewards;

import com.chaosbuffalo.mkchat.dialogue.DialogueNode;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.core.entitlements.EntitlementInstance;
import com.chaosbuffalo.mkcore.core.entitlements.MKEntitlement;
import com.chaosbuffalo.mknpc.MKNpc;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class GrantEntitlementReward extends QuestReward{
    public final static ResourceLocation TYPE_NAME = new ResourceLocation(MKNpc.MODID, "quest_reward.entitlement");
    private MKEntitlement entitlement;

    public GrantEntitlementReward(MKEntitlement entitlement) {
        super(TYPE_NAME, new TranslationTextComponent("mknpc.quest_reward.entitlement.message", entitlement.getDescription()));
        this.entitlement = entitlement;
    }

    public GrantEntitlementReward() {
        super(TYPE_NAME, defaultDescription);
    }


    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("entitlement"), ops.createString(entitlement.getRegistryName().toString()));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        ResourceLocation entitlementId = dynamic.get("entitlement").asString()
                .resultOrPartial(MKCore.LOGGER::error)
                .map(ResourceLocation::new)
                .orElse(MKCoreRegistry.INVALID_ENTITLEMENT);
        entitlement = MKCoreRegistry.getEntitlement(entitlementId);
    }

    @Override
    public void grantReward(PlayerEntity player) {
        if (entitlement != null){
            MKCore.getPlayer(player).ifPresent(x -> x.getEntitlements()
                    .addEntitlement(new EntitlementInstance(entitlement, UUID.randomUUID())));
            player.sendMessage(new TranslationTextComponent("mknpc.grant_entitlement.message",
                    entitlement.getDescription()).mergeStyle(TextFormatting.GOLD), Util.DUMMY_UUID);
        }
    }
}
