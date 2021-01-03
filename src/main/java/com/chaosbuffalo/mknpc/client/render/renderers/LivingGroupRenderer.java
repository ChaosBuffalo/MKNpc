package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.IRenderGroupEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class LivingGroupRenderer<T extends LivingEntity & IRenderGroupEntity, M extends EntityModel<T>> extends LivingRenderer<T, M> {

    private final Map<String, LivingRenderer<T, M>> renderers;
    private M currentModel;

    public LivingGroupRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, null, 0.5f);
        this.renderers = new HashMap<>();
        currentModel = null;
    }

    protected void putRenderer(String key, LivingRenderer<T, M> renderer){
        renderers.put(key, renderer);
        if (currentModel == null){
            setCurrentModel(renderer.getEntityModel());
        }
    }

    @Nullable
    protected LivingRenderer<T, M> getRenderer(T entityIn){
        return renderers.get(entityIn.getCurrentRenderGroup());
    }

    @Override
    public M getEntityModel() {
        return currentModel;
    }

    protected void setCurrentModel(M newModel){
        this.currentModel = newModel;
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn,
                       IRenderTypeBuffer bufferIn, int packedLightIn) {
        LivingRenderer<T, M> currentRenderer = getRenderer(entityIn);
        if (currentRenderer != null){
            setCurrentModel(currentRenderer.getEntityModel());
            currentRenderer.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            this.shadowSize = currentRenderer.shadowSize;
        } else {
            MKNpc.LOGGER.error("No renderer group named {} found for {}",
                    entityIn.getCurrentRenderGroup(), entityIn);
        }
    }

    @Nonnull
    public abstract ResourceLocation getBaseTexture(T entity);

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        LivingRenderer<T, M> renderer = getRenderer(entity);
        if (renderer != null){
            return renderer.getEntityTexture(entity);
        } else {
            return getBaseTexture(entity);
        }
    }

}
