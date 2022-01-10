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
    public static ResourceLocation effectTypeName = new ResourceLocation(MKNpc.MODID, "grant_entitlement");
    private MKEntitlement entitlement;

    public GrantEntitlementEffect(MKEntitlement entitlement) {
        super(effectTypeName);
        this.entitlement = entitlement;
    }

    public GrantEntitlementEffect() {
        super(effectTypeName);
    }

    @Override
    public void applyEffect(ServerPlayerEntity serverPlayerEntity, LivingEntity livingEntity, DialogueNode dialogueNode) {
        if (entitlement != null){
            MKCore.getPlayer(serverPlayerEntity).ifPresent(x -> x.getEntitlements()
                    .addEntitlement(new EntitlementInstance(entitlement, UUID.randomUUID())));
            serverPlayerEntity.sendMessage(new TranslationTextComponent("mknpc.grant_entitlement.message",
                    entitlement.getDescription()).mergeStyle(TextFormatting.GOLD), Util.DUMMY_UUID);
        }
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup, ImmutableMap.of(
                ops.createString("entitlement"), ops.createString(entitlement.getRegistryName().toString())
        )).result().orElse(sup);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        dynamic.get("entitlement").asString().result().ifPresent(
                x -> entitlement = MKCoreRegistry.getEntitlement(new ResourceLocation(x)));

    }
}
