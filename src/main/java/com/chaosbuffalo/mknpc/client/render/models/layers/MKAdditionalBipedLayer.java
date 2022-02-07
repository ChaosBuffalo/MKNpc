package com.chaosbuffalo.mknpc.client.render.models.layers;

import com.chaosbuffalo.mknpc.client.render.models.styling.LayerStyle;
import com.chaosbuffalo.mknpc.client.render.renderers.ILayerTextureProvider;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class MKAdditionalBipedLayer<T extends MKEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    private final LayerStyle style;
    private final EntityModel<T> layerModel;
    private final ILayerTextureProvider<T, M> renderer;

    public MKAdditionalBipedLayer(ILayerTextureProvider<T, M> entityRendererIn,
                                  Function<Float, M> modelSupplier,
                                  LayerStyle style) {
        super(entityRendererIn);
        this.renderer = entityRendererIn;
        this.layerModel = modelSupplier.apply(style.getLayerSize());
        this.style = style;
    }

    protected static <T extends MKEntity> void renderCopyTranslucent(EntityModel<T> modelParentIn, EntityModel<T> modelIn,
                                                                     ResourceLocation textureLocationIn, MatrixStack matrixStackIn,
                                                                     IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn,
                                                                     float limbSwing, float limbSwingAmount, float ageInTicks,
                                                                     float netHeadYaw, float headPitch, float partialTicks,
                                                                     float red, float green, float blue) {
        if (!entityIn.isInvisible() || entityIn.isGhost()) {
            modelParentIn.copyModelAttributesTo(modelIn);
            modelIn.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
            modelIn.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            renderTranslucentModel(modelIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, red, green, blue);
        }

    }

    protected static <T extends MKEntity> void renderTranslucentModel(EntityModel<T> modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float red, float green, float blue) {
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(textureLocationIn, false));
        modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entityIn, 0.0F), red, green, blue, 1.0F);
    }

    protected static <T extends MKEntity> void renderCopyCutoutModel(EntityModel<T> modelParentIn, EntityModel<T> modelIn,
                                                                     ResourceLocation textureLocationIn, MatrixStack matrixStackIn,
                                                                     IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn,
                                                                     float limbSwing, float limbSwingAmount, float ageInTicks,
                                                                     float netHeadYaw, float headPitch, float partialTicks,
                                                                     float red, float green, float blue) {
        if (!entityIn.isInvisible() || entityIn.isGhost()) {
            modelParentIn.copyModelAttributesTo(modelIn);
            modelIn.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
            modelIn.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            renderCutoutModel(modelIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, red, green, blue);
        }

    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn,
                       T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (style.isTranslucent()){
            renderCopyTranslucent(
                    this.getEntityModel(), this.layerModel,
                    renderer.getLayerTexture(style.getLayerName(), entitylivingbaseIn),
                    matrixStackIn, bufferIn,
                    packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
                    headPitch, partialTicks, 1.0F, 1.0F, 1.0F
            );
        } else {
            renderCopyCutoutModel(
                    this.getEntityModel(), this.layerModel,
                    renderer.getLayerTexture(style.getLayerName(), entitylivingbaseIn),
                    matrixStackIn, bufferIn,
                    packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw,
                    headPitch, partialTicks, 1.0F, 1.0F, 1.0F
            );
        }

    }
}
