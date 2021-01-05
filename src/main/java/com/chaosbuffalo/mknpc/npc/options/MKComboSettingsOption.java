package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class MKComboSettingsOption extends NpcDefinitionOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "mk_combo");
    private int comboCount;
    private int ticks;

    public MKComboSettingsOption() {
        super(NAME, ApplyOrder.MIDDLE);
        ticks = 20;
        comboCount = 1;
    }

    public MKComboSettingsOption setComboCount(int comboCount){
        this.comboCount = comboCount;
        return this;
    }

    public MKComboSettingsOption setComboDelay(int ticks){
        this.ticks = ticks;
        return this;
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        setComboCount(dynamic.get("count").asInt(1));
        setComboDelay(dynamic.get("cooldown").asInt(20));
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity) {
        if (entity instanceof MKEntity){
            ((MKEntity) entity).setAttackComboStatsAndDefault(comboCount, ticks);
        }
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup, ImmutableMap.of(
                ops.createString("count"), ops.createInt(comboCount),
                ops.createString("cooldown"), ops.createInt(ticks)
        )).result().orElse(sup);
    }
}
