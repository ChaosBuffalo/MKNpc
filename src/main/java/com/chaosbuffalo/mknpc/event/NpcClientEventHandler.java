package com.chaosbuffalo.mknpc.event;


import com.chaosbuffalo.mkcore.init.CoreParticles;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, value = Dist.CLIENT)
public class NpcClientEventHandler {

    private static KeyBinding questMenuBind;
    private static int ticks = -1;

    public static void initKeybindings() {
        questMenuBind = new KeyBinding("key.hud.questmenu", GLFW.GLFW_KEY_K, "key.mknpc.category");
        ClientRegistry.registerKeyBinding(questMenuBind);
    }

    @SubscribeEvent
    public static void onKeyEvent(InputEvent.KeyInputEvent event) {
        handleInputEvent();
    }

    public static void handleInputEvent() {
//        PlayerEntity player = Minecraft.getInstance().player;
//        if (player == null)
//            return;
//
//        MKPlayerData playerData = MKCore.getPlayerOrNull(player);
//        if (playerData == null)
//            return;
//
//        while (questMenuBind.isPressed()) {
//            Minecraft.getInstance().displayGuiScreen(new QuestPage());
//        }
    }

    @SubscribeEvent
    public static void onRenderLast(RenderWorldLastEvent event){
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player != null){

            if (player.ticksExisted != ticks){
                ticks = player.ticksExisted;
                Set<GlobalPos> alreadySeen = new HashSet<>();
                player.getCapability(NpcCapabilities.PLAYER_QUEST_DATA_CAPABILITY).ifPresent(x -> {
                    x.getQuestChains().forEach(pQuestChain -> {
                        pQuestChain.getCurrentQuests().forEach(questName -> {
                            PlayerQuestData playerQuestData = pQuestChain.getQuestData(questName);
                            for (PlayerQuestObjectiveData objectiveData : playerQuestData.getObjectives()){
                                if (!objectiveData.isComplete()){
                                    Map<String, GlobalPos> posMap = objectiveData.getBlockPosData();
                                    for (GlobalPos pos : posMap.values()){
                                        if (pos.getDimension().equals(player.getEntityWorld().getDimensionKey()) && !alreadySeen.contains(pos)){
                                            event.getContext().addParticle(CoreParticles.INDICATOR_PARTICLE, true,
                                                    pos.getPos().getX() + 0.5, pos.getPos().getY() + 1.0,
                                                    pos.getPos().getZ() + 0.5, 0.0, 0.0, 0.0);
                                            alreadySeen.add(pos);
                                        }

                                    }
                                }
                            }
                        });

                    });
                });
            }

        }
    }
}
