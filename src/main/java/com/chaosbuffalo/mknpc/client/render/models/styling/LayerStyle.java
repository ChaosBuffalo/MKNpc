package com.chaosbuffalo.mknpc.client.render.models.styling;

import net.minecraft.util.ResourceLocation;

public class LayerStyle {

    private final ResourceLocation textureLoc;
    private final float layerSize;

    public ResourceLocation getTextureLoc() {
        return textureLoc;
    }

    public float getLayerSize() {
        return layerSize;
    }

    public LayerStyle(ResourceLocation textureLoc, float size){
        this.layerSize = size;
        this.textureLoc = textureLoc;
    }
}
