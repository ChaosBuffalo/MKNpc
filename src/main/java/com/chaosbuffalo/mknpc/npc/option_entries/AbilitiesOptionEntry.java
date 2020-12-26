package com.chaosbuffalo.mknpc.npc.option_entries;

import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.abilities.MKAbilityInfo;
import com.chaosbuffalo.mknpc.npc.NpcAbilityEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class AbilitiesOptionEntry implements INpcOptionEntry {

    private final List<NpcAbilityEntry> abilities;

    public AbilitiesOptionEntry(List<NpcAbilityEntry> entries){
        this.abilities = entries;
    }

    public AbilitiesOptionEntry(){
        this(new ArrayList<>());
    }

    @Override
    public void applyToEntity(Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.getCapability(CoreCapabilities.ENTITY_CAPABILITY).ifPresent((cap) -> {
                for (MKAbilityInfo ability : cap.getKnowledge().getAllAbilities()) {
                    cap.getKnowledge().unlearnAbility(ability.getId());
                }
                for (NpcAbilityEntry entry : abilities) {
                    MKAbility ability = MKCoreRegistry.getAbility(entry.getAbilityName());
                    if (ability != null) {
                        cap.getKnowledge().learnAbility(ability, entry.getPriority());
                    }
                }
            });
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        ListNBT abilitiesList = new ListNBT();
        for (NpcAbilityEntry entry : abilities){
            abilitiesList.add(entry.serializeNBT());
        }
        tag.put("abilities", abilitiesList);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT abilitiesList = nbt.getList("abilities", Constants.NBT.TAG_COMPOUND);
        abilities.clear();
        for (INBT tag : abilitiesList){
            NpcAbilityEntry entry = new NpcAbilityEntry();
            entry.deserializeNBT((CompoundNBT) tag);
            abilities.add(entry);
        }
    }
}
