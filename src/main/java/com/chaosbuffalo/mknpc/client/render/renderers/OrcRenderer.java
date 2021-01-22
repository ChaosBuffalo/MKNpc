package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.client.render.models.MKBipedModel;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyle;
import com.chaosbuffalo.mknpc.client.render.models.styling.OrcLooks;
import com.chaosbuffalo.mknpc.entity.GreenLadyEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class OrcRenderer extends MKBipedRenderer<GreenLadyEntity, MKBipedModel<GreenLadyEntity>> {

    public OrcRenderer(EntityRendererManager rendererManager, ModelStyle style) {
        super(rendererManager, new MKBipedModel<>(0.0F, 0.0f, 64, 32),
                (size) -> new MKBipedModel<>(size, 0.0f, 64, 32),
                style, OrcLooks.DEFAULT,0.5f);
    }
}
