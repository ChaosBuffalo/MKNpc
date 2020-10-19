package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.option_entries.FactionNameOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INameEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class FactionNameOption extends WorldPermanentOption implements INameProvider {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "faction_name");

    @Nullable
    private String title;
    private boolean hasLastName;

    public FactionNameOption() {
        super(NAME, ApplyOrder.LATE);
        hasLastName = false;
    }


    @Override
    @Nullable
    public StringTextComponent getEntityName(NpcDefinition definition, World world, UUID spawnId) {
        return world.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY).map(
                cap->{
                    INpcOptionEntry entry = cap.getEntityOptionEntry(definition, this, spawnId);
                    if (entry instanceof INameEntry){
                        return ((INameEntry) entry).getName();
                    } else {
                        return new StringTextComponent("Name Error");
                    }
                }).orElse(new StringTextComponent("Name Error"));
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
    private static <T> T getRandomEntry(Random random, Set<T> set){
        List<T> list = new ArrayList<>(set);
        if (list.size() <= 0){
            return null;
        }
        return list.get(random.nextInt(list.size()));
    }

    @Override
    protected INpcOptionEntry makeOptionEntry(NpcDefinition definition, Random random) {
        String name = "";
        if (title != null){
            name += title;
        }
        MKFaction faction = MKFactionRegistry.getFaction(definition.getFactionName());
        if (faction != null){
            name += " ";
            String firstName = getRandomEntry(random, faction.getFirstNames());
            if (firstName == null){
                firstName = "No Name";
            }
            name += firstName;
            if (hasLastName){
                name += " ";
                String lastName = getRandomEntry(random, faction.getLastNames());
                if (lastName == null){
                    lastName = "Unknown";
                }
                name += lastName;
            }
        }
        return new FactionNameOptionEntry(name);
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
