package com.chaosbuffalo.mknpc.client.render.models.layers;

import com.chaosbuffalo.mknpc.client.render.models.styling.LayerStyle;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;

import java.util.function.Function;

public class MKAdditionalBipedLayer<T extends MKEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    private final LayerStyle style;
    private final EntityModel<T> layerModel;

    public MKAdditionalBipedLayer(IEntityRenderer<T, M> entityRendererIn,
                                  Function<Float, M> modelSupplier,
                                  LayerStyle style) {
        super(entityRendererIn);
        this.layerModel = modelSupplier.apply(style.getLayerSize());
        this.style = style;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn,
                       T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        renderCopyCutoutModel(
                this.getEntityModel(), this.layerModel, style.getTextureLoc(), matrixStackIn, bufferIn,
                packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
                headPitch, partialTicks, 1.0F, 1.0F, 1.0F
        );
    }
}
