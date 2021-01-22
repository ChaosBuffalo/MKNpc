package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.client.render.models.MKBipedModel;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyles;
import com.chaosbuffalo.mknpc.client.render.models.styling.OrcLooks;
import com.chaosbuffalo.mknpc.entity.GreenLadyEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class OrcGroupRenderer extends BipedGroupRenderer<GreenLadyEntity, MKBipedModel<GreenLadyEntity>> {

    public OrcGroupRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        putRenderer(ModelStyles.LONG_HAIR_NAME, new OrcRenderer(rendererManager, ModelStyles.LONG_HAIR_STYLE));
        putRenderer(ModelStyles.LONG_HAIR_ARMORED_NAME,
                new OrcRenderer(rendererManager, ModelStyles.ARMORED_LONG_HAIR_STYLE));
        putRenderer(ModelStyles.BASIC_NAME, new OrcRenderer(rendererManager, ModelStyles.BASIC_STYLE));

        putLook("default", OrcLooks.ORC_LOOK_1);
    }

    @Nonnull
    @Override
    public ResourceLocation getBaseTexture(GreenLadyEntity entity) {
        return OrcLooks.BASE_TEXTURE;
    }
}