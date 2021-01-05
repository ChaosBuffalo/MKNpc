package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.client.render.models.MKSkeletalModel;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelLook;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyles;
import com.chaosbuffalo.mknpc.entity.MKSkeletonEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;


public class SkeletalGroupRenderer extends BipedGroupRenderer<MKSkeletonEntity, MKSkeletalModel<MKSkeletonEntity>> {

    public SkeletalGroupRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        putRenderer(ModelStyles.BASIC_NAME, new SkeletalRenderer(rendererManager, ModelStyles.BASIC_STYLE));
        putRenderer(ModelStyles.CLOTHES_ONLY_NAME, new SkeletalRenderer(rendererManager, ModelStyles.CLOTHES_ONLY_STYLE));

        for (Map.Entry<String, ModelLook> entry : SkeletonStyles.SKELETON_LOOKS.entrySet()){
            putLook(entry.getKey(), entry.getValue());
        }
    }

    @Nonnull
    @Override
    public ResourceLocation getBaseTexture(MKSkeletonEntity entity) {
        return SkeletonStyles.SKELETON_TEXTURES;
    }
}
