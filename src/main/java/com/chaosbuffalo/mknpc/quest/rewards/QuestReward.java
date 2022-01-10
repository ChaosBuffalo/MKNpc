package com.chaosbuffalo.mknpc.quest.rewards;

import com.chaosbuffalo.mkcore.serialization.ISerializableAttributeContainer;
import com.chaosbuffalo.mkcore.serialization.attributes.ISerializableAttribute;
import com.chaosbuffalo.mknpc.MKNpc;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class QuestReward implements ISerializableAttributeContainer {
    public final static ResourceLocation INVALID_OPTION = new ResourceLocation(MKNpc.MODID, "quest_reward.invalid");
    protected static final IFormattableTextComponent defaultDescription = new StringTextComponent("Placeholder");
    private IFormattableTextComponent description;
    private final ResourceLocation typeName;
    private final List<ISerializableAttribute<?>> attributes = new ArrayList<>();

    public QuestReward(ResourceLocation typeName, IFormattableTextComponent description){
        this.description = description;
        this.typeName = typeName;
    }

    public IFormattableTextComponent getDescription(){
        return description;
    }

    @Override
    public List<ISerializableAttribute<?>> getAttributes() {
        return attributes;
    }

    public ResourceLocation getTypeName() {
        return typeName;
    }

    @Override
    public void addAttribute(ISerializableAttribute<?> iSerializableAttribute) {
        this.attributes.add(iSerializableAttribute);
    }

    @Override
    public void addAttributes(ISerializableAttribute<?>... iSerializableAttributes) {
        this.attributes.addAll(Arrays.asList(iSerializableAttributes));
    }

    public <D> D serialize(DynamicOps<D> ops){
        ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
        builder.put(ops.createString("rewardType"), ops.createString(getTypeName().toString()));
        builder.put(ops.createString("description"), ops.createString(ITextComponent.Serializer.toJson(description)));
        builder.put(ops.createString("attributes"),
                ops.createMap(attributes.stream().map(attr ->
                        Pair.of(ops.createString(attr.getName()), attr.serialize(ops))
                ).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
        putAdditionalData(ops, builder);
        return ops.createMap(builder.build());
    }

    public static <D> ResourceLocation getType(Dynamic<D> dynamic){
        return new ResourceLocation(dynamic.get("rewardType").asString().result().orElse(INVALID_OPTION.toString()));
    }

    public <D> void deserialize(Dynamic<D> dynamic){
        Map<String, Dynamic<D>> map = dynamic.get("attributes").asMap(d -> d.asString(""), Function.identity());
        description = ITextComponent.Serializer.getComponentFromJson(
                dynamic.get("description").asString(ITextComponent.Serializer.toJson(defaultDescription)));
        getAttributes().forEach(attr -> {
            Dynamic<D> attrValue = map.get(attr.getName());
            if (attrValue != null) {
                attr.deserialize(attrValue);
            }
        });
        readAdditionalData(dynamic);
    }

    public <D> void readAdditionalData(Dynamic<D> dynamic){

    }

    public abstract void grantReward(PlayerEntity player);

    public <D> void putAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder){

    }

}
