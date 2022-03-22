package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mkcore.serialization.attributes.IntAttribute;
import com.chaosbuffalo.mkcore.serialization.attributes.ResourceLocationAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.quest.data.objective.ObjectiveInstanceData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;

public abstract class StructureInstanceObjective<T extends ObjectiveInstanceData> extends QuestObjective<T> {
    public final static ResourceLocation INVALID_OPTION = new ResourceLocation(MKNpc.MODID, "structure.invalid");

    protected final ResourceLocationAttribute structureName = new ResourceLocationAttribute("structure", INVALID_OPTION);
    protected final IntAttribute structureIndex = new IntAttribute("structureIndex", 0);

    public StructureInstanceObjective(ResourceLocation typeName, String name, ResourceLocation structure, IFormattableTextComponent... description) {
        this(typeName, name, description);
        structureName.setValue(structure);

    }

    public StructureInstanceObjective(ResourceLocation typeName, String name, ResourceLocation structure, int index, IFormattableTextComponent... description) {
        this(typeName, name, description);
        structureName.setValue(structure);
        structureIndex.setValue(index);

    }


    public int getStructureIndex() {
        return structureIndex.value();
    }

    public StructureInstanceObjective(ResourceLocation typeName, String name, IFormattableTextComponent... description) {
        super(typeName, name, description);
        addAttributes(structureName, structureIndex);

    }

    public ResourceLocation getStructureName() {
        return structureName.getValue();
    }
}
