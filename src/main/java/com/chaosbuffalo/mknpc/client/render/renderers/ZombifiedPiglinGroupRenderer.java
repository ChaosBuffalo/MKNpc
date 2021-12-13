package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.client.render.models.MKPiglinModel;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelLook;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyles;
import com.chaosbuffalo.mknpc.entity.MKZombifiedPiglinEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

public class ZombifiedPiglinGroupRenderer extends BipedGroupRenderer<MKZombifiedPiglinEntity, MKPiglinModel<MKZombifiedPiglinEntity>> {

    public ZombifiedPiglinGroupRenderer(EntityRendererManager rendererManager, Map<String, ModelLook> styles) {
        super(rendererManager);
        putRenderer(ModelStyles.BASIC_NAME, new ZombifiedPiglinRenderer(rendererManager, ModelStyles.BASIC_STYLE));
        putRenderer(ModelStyles.CLOTHES_ONLY_NAME, new ZombifiedPiglinRenderer(rendererManager, ModelStyles.CLOTHES_ONLY_STYLE));
        putRenderer(ModelStyles.CLOTHES_ARMOR_NAME, new ZombifiedPiglinRenderer(rendererManager, ModelStyles.CLOTHES_ARMOR_STYLE));
        putRenderer(ModelStyles.CLOTHES_ARMOR_TRANSLUCENT_NAME, new ZombifiedPiglinRenderer(rendererManager, ModelStyles.CLOTHES_ARMOR_TRANSLUCENT_STYLE));

        for (Map.Entry<String, ModelLook> entry : styles.entrySet()) {
            putLook(entry.getKey(), entry.getValue());
        }
    }

    @Nonnull
    @Override
    public ResourceLocation getBaseTexture(MKZombifiedPiglinEntity entity) {
        return PiglinStyles.VANILLA_ZOMBIFIED_PIGLIN_TEXTURE;
    }
}
