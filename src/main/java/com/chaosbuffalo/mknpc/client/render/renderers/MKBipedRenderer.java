package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.client.render.models.layers.MKAdditionalBipedLayer;
import com.chaosbuffalo.mknpc.client.render.models.styling.LayerStyle;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelLook;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyle;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class MKBipedRenderer<T extends MKEntity, M extends BipedModel<T>> extends BipedRenderer<T, M> implements ILayerTextureProvider<T, M> {
    private final ModelStyle style;
    private final float defaultShadowSize;
    private ModelLook look;
    private final ModelLook defaultLook;

    public MKBipedRenderer(EntityRendererManager rendererManager, M modelBipedIn,
                           Function<Float, M> modelSupplier, ModelStyle style,
                           ModelLook defaultLook, float shadowSize) {
        super(rendererManager, modelBipedIn, shadowSize);
        this.style = style;
        this.defaultShadowSize = shadowSize;
        this.defaultLook = defaultLook;
        for (LayerStyle layer : style.getAdditionalLayers()){
            addLayer(new MKAdditionalBipedLayer<>(this, modelSupplier, layer));
        }
        if (style.shouldDrawArmor()){
            addLayer(new BipedArmorLayer<>(this, modelSupplier.apply(.5f), modelSupplier.apply(1.0f)));
        }
    }

    public void setLook(ModelLook look) {
        this.look = look;
    }

    public ModelLook getLook() {
        return look != null ? look : defaultLook;
    }

    public ModelStyle getStyle() {
        return style;
    }

    @Override
    protected void preRenderCallback(T entity, MatrixStack matrixStackIn, float partialTickTime) {
        float scale = entity.getRenderScale();
        this.shadowSize = defaultShadowSize * scale;
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getLayerTexture(String layerName, T entity) {
        return getLook().getLayerTexture(layerName);
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return getLook().getBaseTexture();
    }
}
