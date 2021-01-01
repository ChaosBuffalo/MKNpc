package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.client.render.models.MKSkeletalModel;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyle;
import com.chaosbuffalo.mknpc.entity.MKSkeletonEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;


public class SkeletalGroupRenderer extends LivingGroupRenderer<MKSkeletonEntity, MKSkeletalModel<MKSkeletonEntity>> {

    public SkeletalGroupRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        for (Map.Entry<String, ModelStyle> entry : SkeletonStyles.SKELETON_STYLES.entrySet()){
            putRenderer(entry.getKey(), new SkeletalRenderer(rendererManager, entry.getValue()));
        }
    }

    @Nonnull
    @Override
    public ResourceLocation getBaseTexture(MKSkeletonEntity entity) {
        return SkeletonStyles.SKELETON_TEXTURES;
    }
}
