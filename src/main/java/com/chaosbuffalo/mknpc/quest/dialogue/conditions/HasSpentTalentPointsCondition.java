package com.chaosbuffalo.mknpc.quest.dialogue.conditions;

import com.chaosbuffalo.mkchat.dialogue.conditions.DialogueCondition;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mknpc.MKNpc;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;


public class HasSpentTalentPointsCondition extends DialogueCondition {

    public static final ResourceLocation conditionTypeName = new ResourceLocation(MKNpc.MODID, "has_spent_talents");
    private int talentCount;

    public HasSpentTalentPointsCondition(int talentCount){
        super(conditionTypeName);
        this.talentCount = talentCount;
    }

    public HasSpentTalentPointsCondition(){
        this(0);
    }

    @Override
    public boolean meetsCondition(ServerPlayerEntity player, LivingEntity source) {
        return MKCore.getPlayer(player).map(x -> {
            int unspent = x.getKnowledge().getTalentKnowledge().getUnspentTalentPoints();
            int total = x.getKnowledge().getTalentKnowledge().getTotalTalentPoints();
            int spent = total - unspent;
            return spent >= talentCount;
        }).orElse(false);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        super.deserialize(dynamic);
        this.talentCount = dynamic.get("talentCount").asInt(0);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D ret = super.serialize(ops);
        return ops.mergeToMap(ret, ImmutableMap.of(
                ops.createString("talentCount"), ops.createInt(talentCount)
        )).result().orElse(ret);
    }
}
