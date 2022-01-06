package com.chaosbuffalo.mknpc.event;


import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.core.MKPlayerData;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.client.gui.screens.QuestScreen;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

import java.util.Map;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, value = Dist.CLIENT)
public class NpcClientEventHandler {

    private static KeyBinding questMenuBind;

    public static void initKeybindings() {
        questMenuBind = new KeyBinding("key.hud.questmenu", GLFW.GLFW_KEY_K, "key.mknpc.category");
        ClientRegistry.registerKeyBinding(questMenuBind);
    }

    @SubscribeEvent
    public static void onKeyEvent(InputEvent.KeyInputEvent event) {
        handleInputEvent();
    }

    public static void handleInputEvent() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null)
            return;

        MKPlayerData playerData = MKCore.getPlayerOrNull(player);
        if (playerData == null)
            return;

        while (questMenuBind.isPressed()) {
            Minecraft.getInstance().displayGuiScreen(new QuestScreen());
        }
    }

    @SubscribeEvent
    public static void onRenderLast(RenderWorldLastEvent event){
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player != null){
            player.getCapability(NpcCapabilities.PLAYER_QUEST_DATA_CAPABILITY).ifPresent(x -> {
                x.getQuestChains().forEach(pQuestChain -> {
                    PlayerQuestData playerQuestData = pQuestChain.getQuestData(pQuestChain.getCurrentQuest());
                    for (PlayerQuestObjectiveData objectiveData : playerQuestData.getObjectives()){
                        if (!objectiveData.isComplete()){
                            Map<String, BlockPos> posMap = objectiveData.getBlockPosData();
                            for (BlockPos pos : posMap.values()){
                                event.getContext().addParticle(ParticleTypes.BARRIER, true,
                                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 0.0, 0.0,0.0);
                            }
                        }
                    }
                });
            });
        }
    }
}
