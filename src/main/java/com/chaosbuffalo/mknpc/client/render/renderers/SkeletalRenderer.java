package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.client.render.models.MKSkeletalModel;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyle;
import com.chaosbuffalo.mknpc.entity.MKSkeletonEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;


public class SkeletalRenderer extends MKBipedRenderer<MKSkeletonEntity, MKSkeletalModel<MKSkeletonEntity>> {

    public SkeletalRenderer(EntityRendererManager rendererManager, ModelStyle style) {
        super(rendererManager, new MKSkeletalModel<>(0.0f, false),
                (size) -> new MKSkeletalModel<>(size, true), style,
                SkeletonStyles.DEFAULT_LOOK,
                0.5f);
    }
}
