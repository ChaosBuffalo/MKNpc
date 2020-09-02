package com.chaosbuffalo.mknpc.npc;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class NpcDefinitionClient {

    private final ResourceLocation defName;
    private final String name;
    private final ResourceLocation faction;

    public NpcDefinitionClient(NpcDefinition definition){
        this(definition.getDefinitionName(), definition.getName(), definition.getFactionName());
    }

    public NpcDefinitionClient(ResourceLocation defName, String name, ResourceLocation faction){
        this.defName = defName;
        this.name = name;
        this.faction = faction;
    }

    public String getName() {
        return name;
    }

    public void toBuffer(PacketBuffer buffer){
        buffer.writeResourceLocation(getDefinitionName());
        buffer.writeString(getName());
        buffer.writeResourceLocation(getFaction());
    }

    public ResourceLocation getDefinitionName() {
        return defName;
    }

    public ResourceLocation getFaction() {
        return faction;
    }

    public static NpcDefinitionClient fromBuffer(PacketBuffer buffer){
        return new NpcDefinitionClient(buffer.readResourceLocation(),
                buffer.readString(), buffer.readResourceLocation());
    }

}
