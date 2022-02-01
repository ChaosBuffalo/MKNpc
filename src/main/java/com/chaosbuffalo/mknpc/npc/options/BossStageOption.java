package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.entity.boss.BossStage;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BossStageOption extends NpcDefinitionOption{
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "boss_stage");
    private final List<BossStage> stages = new ArrayList<>();


    public BossStageOption() {
        super(NAME, ApplyOrder.MIDDLE);
    }


    public void addStage(BossStage stage){
        this.stages.add(stage);
    }

    public BossStageOption withStage(BossStage stage){
        addStage(stage);
        return this;
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        stages.clear();
        stages.addAll(dynamic.get("stages").asList(x -> {
            BossStage newStage = new BossStage();
            newStage.deserialize(x);
            return newStage;
        }));
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup,
                ops.createString("stages"),
                ops.createList(stages.stream().map(x -> x.serialize(ops)))
        ).result().orElse(sup);
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity) {
        if (entity instanceof MKEntity){
            for (BossStage stage : stages){
                BossStage copy = stage.copy();
                copy.setDefinition(definition);
                ((MKEntity) entity).addBossStage(copy);
            }
        } else {
            MKNpc.LOGGER.warn("Failed to apply boss stage option {} is not an MKEntity", entity);
        }
    }
}
