package com.chaosbuffalo.mknpc.quest.dialogue.effects;

import com.chaosbuffalo.mkchat.dialogue.DialogueNode;
import com.chaosbuffalo.mkchat.dialogue.effects.DialogueEffect;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.core.entitlements.EntitlementInstance;
import com.chaosbuffalo.mkcore.core.entitlements.MKEntitlement;
import com.chaosbuffalo.mknpc.MKNpc;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.UUID;

public class GrantEntitlementEffect extends DialogueEffect {
    public static final ResourceLocation effectTypeName = new ResourceLocation(MKNpc.MODID, "grant_entitlement");
    private MKEntitlement entitlement;

    public GrantEntitlementEffect(MKEntitlement entitlement) {
        this();
        this.entitlement = entitlement;
    }

    public GrantEntitlementEffect() {
        super(effectTypeName);
    }

    @Override
    public GrantEntitlementEffect copy() {
        // No runtime mutable state
        return new GrantEntitlementEffect(entitlement);
    }

    @Override
    public void applyEffect(ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity, DialogueNode dialogueNode) {
        if (entitlement != null) {
            MKCore.getPlayer(serverPlayerEntity).ifPresent(x -> x.getEntitlements()
                    .addEntitlement(new EntitlementInstance(entitlement, UUID.randomUUID())));
            serverPlayerEntity.sendMessage(new TranslationTextComponent("mknpc.grant_entitlement.message",
                    entitlement.getDescription()).mergeStyle(TextFormatting.GOLD), Util.DUMMY_UUID);
        }
    }


    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("entitlement"), ops.createString(entitlement.getRegistryName().toString()));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        ResourceLocation entitlementId = dynamic.get("entitlement").asString()
                .resultOrPartial(MKCore.LOGGER::error)
                .map(ResourceLocation::new)
                .orElse(MKCoreRegistry.INVALID_ENTITLEMENT);
        entitlement = MKCoreRegistry.getEntitlement(entitlementId);
    }
}
