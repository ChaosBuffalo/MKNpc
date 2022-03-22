package com.chaosbuffalo.mknpc.quest.requirements;

import com.chaosbuffalo.mkchat.dialogue.conditions.DialogueCondition;
import com.chaosbuffalo.mkcore.serialization.IDynamicMapTypedSerializer;
import com.chaosbuffalo.mkcore.serialization.ISerializableAttributeContainer;
import com.chaosbuffalo.mkcore.serialization.attributes.ISerializableAttribute;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class QuestRequirement implements ISerializableAttributeContainer, IDynamicMapTypedSerializer {

    public interface Deserializer extends Function<Dynamic<?>, QuestRequirement> {

    }

    private static final String TYPE_ENTRY_NAME = "questReqType";
    private final List<ISerializableAttribute<?>> attributes = new ArrayList<>();
    private final ResourceLocation requirementType;

    public QuestRequirement(ResourceLocation typeName) {
        this.requirementType = typeName;
    }

    @Override
    public List<ISerializableAttribute<?>> getAttributes() {
        return attributes;
    }

    @Override
    public void addAttribute(ISerializableAttribute<?> iSerializableAttribute) {
        attributes.add(iSerializableAttribute);
    }

    @Override
    public void addAttributes(ISerializableAttribute<?>... iSerializableAttributes) {
        attributes.addAll(Arrays.asList(iSerializableAttributes));
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        builder.put(ops.createString("attributes"), serializeAttributeMap(ops));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        deserializeAttributeMap(dynamic, "attributes");
    }

    public static <D> Optional<ResourceLocation> getType(Dynamic<D> dynamic) {
        return IDynamicMapTypedSerializer.getType(dynamic, TYPE_ENTRY_NAME);
    }

    @Override
    public ResourceLocation getTypeName() {
        return requirementType;
    }

    @Override
    public String getTypeEntryName() {
        return TYPE_ENTRY_NAME;
    }

    public abstract boolean meetsRequirements(PlayerEntity player);

    public abstract DialogueCondition getDialogueCondition();

}
