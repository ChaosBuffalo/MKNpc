package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.options.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class NpcDefinition {
    private ResourceLocation entityType;
    private final ResourceLocation definitionName;
    private final ResourceLocation parentName;
    private NpcDefinition parent;
    private final Map<ResourceLocation, NpcDefinitionOption> options;
    private static final Set<String> toSkip = new HashSet<>();
    private static final List<NpcDefinitionOption.ApplyOrder> orders = new ArrayList<>();
    static {
        toSkip.add("entityType");
        toSkip.add("parent");
        orders.add(NpcDefinitionOption.ApplyOrder.EARLY);
        orders.add(NpcDefinitionOption.ApplyOrder.MIDDLE);
        orders.add(NpcDefinitionOption.ApplyOrder.LATE);
    }

    boolean hasParentName(){
        return parentName != null;
    }

    public NpcDefinition(ResourceLocation definitionName, ResourceLocation entityType, ResourceLocation parentName){
        this.definitionName = definitionName;
        this.entityType = entityType;
        this.parentName = parentName;
        this.options = new HashMap<>();
    }

    public boolean isNotable() {
        if (hasOption(NotableOption.NAME)){
            NotableOption option = (NotableOption) getOption(NotableOption.NAME);
            return option.getValue();
        }
        return false;
    }

    public boolean hasParent(){
        return parent != null;
    }

    public boolean isWorldPermanent(){
        for (NpcDefinitionOption option : options.values()){
            if (option instanceof WorldPermanentOption){
                return true;
            }
        }
        if (hasParent()){
            return getParent().isWorldPermanent();
        }
        return false;
    }

    public NpcDefinition getParent() {
        return parent;
    }

    public boolean resolveParents(){
        if (hasParentName()){
            parent = NpcDefinitionManager.getDefinition(parentName);
            return parent != null && parent.resolveParents();
        }
        return true;
    }

    public void resolveEntityType(){
        if (entityType == null){
            entityType = getAncestor().getEntityType();
        }
    }

    public NpcDefinition getAncestor(){
        if (!hasParent()){
            return this;
        } else {
            return getParent().getAncestor();
        }
    }

    public ResourceLocation getParentName() {
        return parentName;
    }

    public ResourceLocation getDefinitionName() {
        return definitionName;
    }

    public boolean hasOption(ResourceLocation optionName){
        return options.containsKey(optionName) ||  (hasParent() && parent.hasOption(optionName));
    }

    public NpcDefinitionOption getOption(ResourceLocation optionName){
        if (!hasOption(optionName)){
            return null;
        }
        if (options.containsKey(optionName)){
            return options.get(optionName);
        } else if (hasParent()){
            return getParent().getOption(optionName);
        }
        return null;
    }

    public void addOption(NpcDefinitionOption option){
        options.put(option.getName(), option);
    }

    public ResourceLocation getFactionName(){
        if (hasOption(FactionOption.NAME)){
            FactionOption option = (FactionOption) getOption(FactionOption.NAME);
            return option.getValue();
        }
        return MKFaction.INVALID_FACTION;
    }

    @Nullable
    public String getDisplayName(){
        for (NpcDefinitionOption option : options.values()){
            if (option instanceof INameProvider){
                return ((INameProvider) option).getDisplayName();
            }
        }
        if (hasParent()){
            return getParent().getDisplayName();
        } else {
            return null;
        }
    }


    public ResourceLocation getEntityType() {
        return entityType;
    }

    public StringTextComponent getNameForEntity(World world, UUID spawnId){
        for (NpcDefinitionOption option : options.values()){
            if (option instanceof INameProvider){
                return ((INameProvider) option).getEntityName(this, world, spawnId);
            }
        }
        if (hasParent()){
            return getParent().getNameForEntity(world, spawnId);
        } else {
            return new StringTextComponent("Name Error");
        }
    }

    public void applyDefinition(Entity entity){
        for (NpcDefinitionOption.ApplyOrder order : orders){
            apply(entity, order);
        }
        // hack to make sure we're at our new max health
        if (entity instanceof LivingEntity){
            ((LivingEntity) entity).setHealth(((LivingEntity) entity).getMaxHealth());
        }
    }


    private void apply(Entity entity, NpcDefinitionOption.ApplyOrder order){
        if (hasParent()){
            getParent().apply(entity, order);
        }
        for (Map.Entry<ResourceLocation, NpcDefinitionOption> option : options.entrySet()){
            if (option.getValue().getOrdering() == order){
                option.getValue().applyToEntity(this, entity);
            }
        }
    }

    @Nullable
    public Entity createEntity(World world, Vec3d pos){
        return createEntity(world, pos, UUID.randomUUID());
    }

    public static NpcDefinition deserializeJson(Gson gson, ResourceLocation name, JsonObject obj){
        ResourceLocation parentName = null;
        if (obj.has("parent")){
            parentName = new ResourceLocation(obj.get("parent").getAsString());
        }
        ResourceLocation typeName = null;
        if (parentName == null){
            typeName = new ResourceLocation(obj.get("entityType").getAsString());
        }
        NpcDefinition def = new NpcDefinition(name, typeName, parentName);
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()){
            if (toSkip.contains(entry.getKey())){
                continue;
            }
            ResourceLocation attrLoc = new ResourceLocation(entry.getKey());
            NpcDefinitionOption option = NpcDefinitionManager.getNpcOption(attrLoc);
            if (option != null){
                option.fromJson(gson, obj);
                def.addOption(option);
            }
        }
        return def;
    }

    @Nullable
    public Entity createEntity(World world, Vec3d pos, UUID uuid){
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(getEntityType());
        if (type != null){
            Entity entity = type.create(world);
            if (entity == null){
                return null;
            }
            entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
            MKNpc.getNpcData(entity).ifPresent(cap -> {
                cap.setDefinition(this);
                cap.setSpawnID(uuid);
            });
            applyDefinition(entity);
            return entity;
        }
        return null;
    }
}
