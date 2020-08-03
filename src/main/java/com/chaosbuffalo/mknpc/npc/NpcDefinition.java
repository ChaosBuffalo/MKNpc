package com.chaosbuffalo.mknpc.npc;

import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.MKAbilityInfo;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.Capabilities;
import com.chaosbuffalo.mknpc.utils.RandomCollection;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NpcDefinition {
    private final ResourceLocation entityType;
    private final ResourceLocation definitionName;
    private String name;
    private final List<NpcAbilityEntry> abilities;
    private final List<NpcAttributeEntry> attributes;
    private int experiencePoints;
    private final Map<EquipmentSlotType, List<NpcItemChoice>> itemChoices;

    public NpcDefinition(ResourceLocation definitionName, ResourceLocation entityType){
        this.definitionName = definitionName;
        this.abilities = new ArrayList<>();
        this.attributes = new ArrayList<>();
        this.itemChoices = new HashMap<>();
        this.entityType = entityType;
        this.experiencePoints = 100;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void addAttribute(IAttribute attribute, double value){
        attributes.add(new NpcAttributeEntry(attribute, value));
    }

    public void addAbility(ResourceLocation location, int priority){
        abilities.add(new NpcAbilityEntry(location, priority));
    }

    public void addItemChoice(EquipmentSlotType slot, NpcItemChoice choice){
        if (!itemChoices.containsKey(slot)){
            itemChoices.put(slot, new ArrayList<>());
        }
        itemChoices.get(slot).add(choice);
    }

    public void addAbilityEntry(NpcAbilityEntry entry){
        abilities.add(entry);
    }

    public void addAttributeEntry(NpcAttributeEntry entry){
        attributes.add(entry);
    }

    public ResourceLocation getDefinitionName() {
        return definitionName;
    }

    public static NpcDefinition deserializeJson(Gson gson, ResourceLocation name, JsonObject obj){
        ResourceLocation typeName = new ResourceLocation(obj.get("entityType").getAsString());
        NpcDefinition def = new NpcDefinition(name, typeName);
        if (obj.has("abilities")){
            JsonArray abilityArray = obj.getAsJsonArray("abilities");
            for (JsonElement ability : abilityArray) {
                JsonObject abilityObj = ability.getAsJsonObject();
                ResourceLocation abilityName = new ResourceLocation(abilityObj.get("abilityName").getAsString());
                NpcAbilityEntry entry = new NpcAbilityEntry(abilityName, abilityObj.get("priority").getAsInt());
                def.addAbilityEntry(entry);
            }
        }
        if (obj.has("attributes")){
            JsonArray attributeArray  = obj.getAsJsonArray("attributes");
            for (JsonElement attr : attributeArray){
                NpcAttributeEntry entry = gson.fromJson(attr, NpcAttributeEntry.class);
                def.addAttributeEntry(entry);
            }
        }
        if (obj.has("name")){
            def.setName(obj.get("name").getAsString());
        }
        if (obj.has("experience")){
            def.setExperiencePoints(obj.get("experience").getAsInt());
        }
        if (obj.has("equipment")){
            JsonObject equipmentObject = obj.getAsJsonObject("equipment");
            for (Map.Entry<String, JsonElement> entry : equipmentObject.entrySet()){
                EquipmentSlotType slot = EquipmentSlotType.fromString(entry.getKey());
                JsonArray itemChoiceArray = entry.getValue().getAsJsonArray();
                for (JsonElement itemChoiceEle : itemChoiceArray){
                    JsonObject itemChoiceObj = itemChoiceEle.getAsJsonObject();
                    float dropChance = .00f;
                    if (itemChoiceObj.has("dropChance")) {
                        dropChance = itemChoiceObj.get("dropChance").getAsFloat();
                    }
                    String itemName = itemChoiceObj.get("item").getAsString();
                    Item item;
                    if (itemName.equals("EMPTY")) {
                        item = null;
                    } else {
                        item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
                    }
                    if (item == null && !itemName.equals("EMPTY")) {
                        MKNpc.LOGGER.debug("Failed to load item for {} in definition: {}",
                                itemChoiceObj.get("item").getAsString(), name);
                        continue;
                    } else {
                        ItemStack itemStack;
                        if (item == null) {
                            itemStack = ItemStack.EMPTY;
                        } else {
                            itemStack = new ItemStack(item, 1);
                        }
                        NpcItemChoice choice = new NpcItemChoice(itemStack,
                                itemChoiceObj.get("weight").getAsDouble(),
                                dropChance);
                        def.addItemChoice(slot, choice);
                    }
                }
            }
        }
        return def;
    }

    public void applyDefinition(Entity entity){
        if (entity instanceof LivingEntity){
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.getCapability(com.chaosbuffalo.mkcore.Capabilities.ENTITY_CAPABILITY).ifPresent((cap) -> {
                for (MKAbilityInfo ability : cap.getKnowledge().getAbilities()){
                    cap.getKnowledge().unlearnAbility(ability.getId());
                }
                for (NpcAbilityEntry entry : abilities){
                    MKAbility ability = MKCoreRegistry.getAbility(entry.getAbilityName());
                    if (ability != null){
                        cap.getKnowledge().learnAbility(ability, entry.getPriority());
                    }
                }
            });
            AbstractAttributeMap attributeMap = livingEntity.getAttributes();
            for (NpcAttributeEntry entry : attributes){
                IAttributeInstance attribute = attributeMap.getAttributeInstanceByName(entry.getAttributeName());
                if (attribute != null){
                    attribute.setBaseValue(entry.getValue());
                }
            }
            applyItemChoices(livingEntity);
            livingEntity.setHealth(livingEntity.getMaxHealth());
        }
        if (getName() != null){
            entity.setCustomName(new StringTextComponent(getName()));
        }
    }

    public void applyItemChoices(LivingEntity entity){
        for (Map.Entry<EquipmentSlotType, List<NpcItemChoice>> entry : itemChoices.entrySet()){
            RandomCollection<NpcItemChoice> slotChoices = new RandomCollection<>();
            for (NpcItemChoice choice : entry.getValue()){
                slotChoices.add(choice.weight, choice);
            }
            NpcItemChoice.livingEquipmentAssign(entity, entry.getKey(), slotChoices.next());
        }
    }

    public void spawnEntity(World world, Vec3d pos){
       EntityType<?> type = ForgeRegistries.ENTITIES.getValue(entityType);
       if (type != null){
           Entity entity = type.create(world);
           if (entity == null){
               return;
           }
           entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
           entity.getCapability(Capabilities.NPC_DATA_CAPABILITY).ifPresent(
                   cap -> cap.setDefinition(this));
           applyDefinition(entity);
           world.addEntity(entity);
       }
    }
}
