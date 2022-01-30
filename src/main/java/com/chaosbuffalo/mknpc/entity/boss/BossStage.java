package com.chaosbuffalo.mknpc.entity.boss;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.options.NpcDefinitionOption;

import java.util.List;

public class BossStage {

    List<NpcDefinitionOption> options;

    private NpcDefinition def;

    public BossStage(NpcDefinition def){
        this.def = def;
    }

    public void apply(MKEntity entity){
        for (NpcDefinitionOption option : options){
            option.applyToEntity(def, entity);
        }
    }
}
