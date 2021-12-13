package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.client.render.models.MKPiglinModel;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyle;
import com.chaosbuffalo.mknpc.entity.MKZombifiedPiglinEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class ZombifiedPiglinRenderer extends MKBipedRenderer<MKZombifiedPiglinEntity, MKPiglinModel<MKZombifiedPiglinEntity>> {

    public ZombifiedPiglinRenderer(EntityRendererManager rendererManager, ModelStyle style) {
        super(rendererManager, new MKPiglinModel<>(0.0f, 64, 64, false),
                (size) -> new MKPiglinModel<>(size, 64, 64, false), style,
                PiglinStyles.DEFAULT_ZOMBIE_LOOK,
                0.5f);
    }
}
