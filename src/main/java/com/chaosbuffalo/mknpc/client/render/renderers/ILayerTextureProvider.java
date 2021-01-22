package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.util.ResourceLocation;

public interface ILayerTextureProvider<T extends MKEntity, M extends EntityModel<T>> extends IEntityRenderer<T, M> {

    ResourceLocation getLayerTexture(String layerName, T entity);
}
