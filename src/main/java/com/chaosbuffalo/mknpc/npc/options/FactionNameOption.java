package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.option_entries.FactionNameOptionEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INameEntry;
import com.chaosbuffalo.mknpc.npc.option_entries.INpcOptionEntry;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
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

    public FactionNameOption setTitle(String title){
        this.title = title;
        return this;
    }

    public FactionNameOption setHasLastName(boolean value){
        this.hasLastName = value;
        return this;
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
    public <D> void deserialize(Dynamic<D> dynamic) {
        this.title = dynamic.get("title").asString(null);
        this.hasLastName = dynamic.get("hasLastName").asBoolean(false);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        D sup = super.serialize(ops);
        return ops.mergeToMap(sup, ImmutableMap.of(
                ops.createString("title"), ops.createString(title),
                ops.createString("hasLastName"), ops.createBoolean(hasLastName)
        )).result().orElse(sup);
    }

}
