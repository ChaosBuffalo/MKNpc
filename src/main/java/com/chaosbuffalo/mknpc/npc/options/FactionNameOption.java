package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.option_entries.FactionNameOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FactionNameOption extends WorldPermanentOption {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "faction_name");

    @Nullable
    private String title;
    private boolean hasLastName;

    public FactionNameOption() {
        super(NAME, ApplyOrder.LATE);
        hasLastName = false;
    }

    @Override
    public boolean providesName() {
        return true;
    }

    @Nullable
    @Override
    public String getDisplayName() {
        String name = "";
        if (title != null){
            name += title;
        }
        name += " ";
        name += "[First]";
        if (hasLastName){
            name += " ";
            name += "[Last]";
        }
        return name;
    }


    @Nullable
    private static <T> T getRandomEntry(LivingEntity entity, Set<T> set){
        List<T> list = new ArrayList<>(set);
        if (list.size() <= 0){
            return null;
        }
        return list.get(entity.getRNG().nextInt(list.size()));
    }

    @Override
    protected INpcOptionEntry makeOptionEntry(NpcDefinition definition, Entity entity) {
        if (entity instanceof LivingEntity){
            return entity.getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY).map((cap) -> {
                String name = "";
                if (title != null){
                    name += title;
                }
                MKFaction faction = MKFactionRegistry.getFaction(cap.getFactionName());
                if (faction != null){
                    name += " ";
                    String firstName = getRandomEntry((LivingEntity) entity, faction.getFirstNames());
                    if (firstName == null){
                        firstName = "No Name";
                    }
                    name += firstName;
                    if (hasLastName){
                        name += " ";
                        String lastName = getRandomEntry((LivingEntity) entity, faction.getLastNames());
                        if (lastName == null){
                            lastName = "Unknown";
                        }
                        name += lastName;
                    }
                }
                return new FactionNameOptionEntry(name);
            }).orElse(new FactionNameOptionEntry(""));
        } else {
            return new FactionNameOptionEntry("");
        }
    }

    @Override
    public void fromJson(Gson gson, JsonObject object) {
        JsonObject json = object.getAsJsonObject(NAME.toString());
        if (json.has("title")){
            this.title = json.get("title").getAsString();
        }
        if (json.has("hasLastName")){
            this.hasLastName = json.get("hasLastName").getAsBoolean();
        }
    }
}
