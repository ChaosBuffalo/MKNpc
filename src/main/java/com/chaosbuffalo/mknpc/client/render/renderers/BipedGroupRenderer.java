package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelLook;
import com.chaosbuffalo.mknpc.entity.IModelLookProvider;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class BipedGroupRenderer<T extends MKEntity, M extends BipedModel<T>> extends LivingRenderer<T, M> {

    private final Map<String, MKBipedRenderer<T, M>> renderers;
    private M currentModel;
    private final Map<String, ModelLook> looks;

    public BipedGroupRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, null, 0.5f);
        this.renderers = new HashMap<>();
        this.looks = new HashMap<>();
        currentModel = null;
    }

    protected void putLook(String name, ModelLook look){
        if (!renderers.containsKey(look.getStyleName(false)) || !renderers.containsKey(look.getStyleName(true))){
            MKNpc.LOGGER.error("Tried to register look {} to {}, but renderer for style is missing.",
                    name, this);
            return;
        }
        this.looks.put(name, look);
    }

    protected void putRenderer(String key, MKBipedRenderer<T, M> renderer){
        renderers.put(key, renderer);
        if (currentModel == null){
            setCurrentModel(renderer.getEntityModel());
        }
    }

    @Nullable
    protected ModelLook getLookForEntity(T entityIn){
        return looks.get(entityIn.getCurrentModelLook());
    }

    @Nullable
    protected MKBipedRenderer<T, M> getRenderer(T entityIn){
        ModelLook look = getLookForEntity(entityIn);
        if (look == null){
            return null;
        }
        MKBipedRenderer<T, M> renderer = renderers.get(look.getStyleName(
                !entityIn.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()));
        if (renderer != null){
            renderer.setLook(look);
        }
        return renderer;
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
        MKBipedRenderer<T, M> currentRenderer = getRenderer(entityIn);
        if (currentRenderer != null){
            setCurrentModel(currentRenderer.getEntityModel());
            currentRenderer.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            this.shadowSize = currentRenderer.shadowSize;
        } else {
            MKNpc.LOGGER.error("No renderer group named {} found for {}",
                    entityIn.getCurrentModelLook(), entityIn);
        }
    }

    @Nonnull
    public abstract ResourceLocation getBaseTexture(T entity);

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        MKBipedRenderer<T, M> renderer = getRenderer(entity);
        if (renderer != null){
            return renderer.getEntityTexture(entity);
        } else {
            return getBaseTexture(entity);
        }
    }

}
