package com.chaosbuffalo.mknpc.client.render;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.client.render.renderers.*;
import com.chaosbuffalo.mknpc.init.MKNpcBlocks;
import com.chaosbuffalo.mknpc.init.MKNpcEntityTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderRegistry {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent evt) {
        RenderTypeLookup.setRenderLayer(MKNpcBlocks.MK_SPAWNER_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(MKNpcBlocks.MK_POI_BLOCK.get(), RenderType.getCutout());

        RenderingRegistry.registerEntityRenderingHandler(MKNpcEntityTypes.SKELETON_TYPE, (rendererManager) ->
                new SkeletalGroupRenderer(rendererManager, SkeletonStyles.SKELETON_LOOKS));

        RenderingRegistry.registerEntityRenderingHandler(MKNpcEntityTypes.ZOMBIFIED_PIGLIN_TYPE, (renderManager) ->
                new ZombifiedPiglinGroupRenderer(renderManager, PiglinStyles.ZOMBIFIED_PIGLIN_LOOKS));

    }
}
