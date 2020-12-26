package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcAbilityEntry;
import com.chaosbuffalo.mknpc.npc.NpcAttributeEntry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.option_entries.AbilitiesOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AbilitiesOption extends WorldPermanentOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "abilities");
    private final List<NpcAbilityEntry> abilities;

    public AbilitiesOption(){
        super(NAME);
        abilities = new ArrayList<>();
    }

    protected void addAbilityEntry(NpcAbilityEntry entry){
        abilities.add(entry);
    }

    @Override
    protected INpcOptionEntry makeOptionEntry(NpcDefinition definition, Random random) {
        List<NpcAbilityEntry> finalChoices = new ArrayList<>();
        for (NpcAbilityEntry entry : abilities) {
            if (random.nextDouble() <= entry.getChance()) {
                finalChoices.add(entry);
            }
        }
        return new AbilitiesOptionEntry(finalChoices);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup,
                ops.createString("options"),
                ops.createList(abilities.stream().map(x -> x.serialize(ops)))
        ).result().orElse(sup);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        List<NpcAbilityEntry> entries = dynamic.get("options").asList(d -> {
            NpcAbilityEntry entry = new NpcAbilityEntry();
            entry.deserialize(d);
            return entry;
        });
        abilities.clear();
        abilities.addAll(entries);
    }

    @Override
    public void fromJson(Gson gson, JsonObject object) {
        JsonArray abilityArray = object.getAsJsonArray(NAME.toString());
        for (JsonElement ability : abilityArray) {
            JsonObject abilityObj = ability.getAsJsonObject();
            ResourceLocation abilityName = new ResourceLocation(abilityObj.get("abilityName").getAsString());
            double chance = abilityObj.has("chance") ? abilityObj.get("chance").getAsDouble() : 1.1;
            NpcAbilityEntry entry = new NpcAbilityEntry(abilityName, abilityObj.get("priority").getAsInt(), chance);
            addAbilityEntry(entry);
        }
    }
}
