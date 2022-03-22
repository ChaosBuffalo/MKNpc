package com.chaosbuffalo.mknpc.quest.generation;

import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class QuestChainBuildResult {
    public final QuestChainInstance instance;
    public final Map<ResourceLocation, List<MKStructureEntry>> questStructures;

    public QuestChainBuildResult(QuestChainInstance instance,
                                 Map<ResourceLocation, List<MKStructureEntry>> structuresIn) {
        this.instance = instance;
        questStructures = Collections.unmodifiableMap(structuresIn);
    }
}
