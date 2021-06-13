package com.chaosbuffalo.mknpc.event;

import com.chaosbuffalo.mkcore.core.damage.MKDamageSource;
import com.chaosbuffalo.mkcore.effects.SpellTriggers;
import com.chaosbuffalo.mknpc.MKNpc;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid= MKNpc.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class EntityHandler {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        MKNpc.getNpcData(event.getEntity()).ifPresent((cap) -> {
            if (cap.wasMKSpawned()){
                event.setCanceled(true);
            } else {
                if (cap.needsDefinitionApplied()){
                    cap.applyDefinition();
                }
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityDamage(LivingDamageEvent event){
        if (event.getSource() instanceof MKDamageSource){
            if (event.getEntityLiving() instanceof PlayerEntity &&
                    !(event.getSource().getTrueSource() instanceof PlayerEntity)){
                event.setAmount((float) (event.getAmount() * MKNpc.getDifficultyScale(event.getEntityLiving())));
            }
        }
    }
}
