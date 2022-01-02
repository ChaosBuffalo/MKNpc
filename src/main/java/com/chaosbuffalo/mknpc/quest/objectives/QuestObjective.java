package com.chaosbuffalo.mknpc.quest.objectives;

import com.chaosbuffalo.mkcore.serialization.ISerializableAttributeContainer;
import com.chaosbuffalo.mkcore.serialization.attributes.ISerializableAttribute;
import com.chaosbuffalo.mkcore.serialization.attributes.StringAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.WorldNpcDataHandler;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
import com.chaosbuffalo.mknpc.quest.data.QuestData;
import com.chaosbuffalo.mknpc.quest.data.objective.ObjectiveInstanceData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class QuestObjective<T extends ObjectiveInstanceData> implements ISerializableAttributeContainer {

    public final static ResourceLocation INVALID_OPTION = new ResourceLocation(MKNpc.MODID, "quest_objective.invalid");

    private final List<ISerializableAttribute<?>> attributes;
    private final ResourceLocation typeName;
    protected final StringAttribute objectiveName = new StringAttribute("objectiveName", "invalid");
    protected final ITextComponent description;

    public QuestObjective(ResourceLocation typeName, String name, ITextComponent description){
        this.attributes = new ArrayList<>();
        addAttribute(objectiveName);
        objectiveName.setValue(name);
        this.typeName = typeName;
        this.description = description;
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

    public <D> D serialize(DynamicOps<D> ops){
        ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
        builder.put(ops.createString("objectiveType"), ops.createString(getTypeName().toString()));
        builder.put(ops.createString("attributes"),
                ops.createMap(attributes.stream().map(attr ->
                        Pair.of(ops.createString(attr.getName()), attr.serialize(ops))
                ).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
        return ops.createMap(builder.build());
    }

    public String getObjectiveName() {
        return objectiveName.getName();
    }

    public ResourceLocation getTypeName() {
        return typeName;
    }

    public static <D> ResourceLocation getType(Dynamic<D> dynamic){
        return new ResourceLocation(dynamic.get("objectiveType").asString().result().orElse(INVALID_OPTION.toString()));
    }

    public <D> void deserialize(Dynamic<D> dynamic){
        Map<String, Dynamic<D>> map = dynamic.get("attributes").asMap(d -> d.asString(""), Function.identity());
        getAttributes().forEach(attr -> {
            Dynamic<D> attrValue = map.get(attr.getName());
            if (attrValue != null) {
                attr.deserialize(attrValue);
            }
        });
    }

    // return true if it works or you dont care
    // return false only if this structure is needed but doesnt meet requirements
    public boolean isStructureRelevant(MKStructureEntry entry){
        return true;
    }

    public abstract T generateInstanceData(Map<ResourceLocation, List<MKStructureEntry>> questStructures);

    public abstract T instanceDataFactory();

    public T loadInstanceData(CompoundNBT nbt){
        T data = instanceDataFactory();
        data.deserializeNBT(nbt);
        return data;
    }

    public abstract PlayerQuestObjectiveData generatePlayerData(WorldNpcDataHandler worldData, QuestData questData);

    public abstract PlayerQuestObjectiveData playerDataFactory();

    public T getInstanceData(QuestData data){
        return (T) data.getObjective(getObjectiveName());
    }

    public void createDataForQuest(QuestData data, Map<ResourceLocation, List<MKStructureEntry>> questStructures){
        data.putObjective(getObjectiveName(), generateInstanceData(questStructures));
    }

    public ITextComponent getDescription(){
        return description;
    }

    public abstract boolean isComplete(PlayerQuestObjectiveData playerData);
}