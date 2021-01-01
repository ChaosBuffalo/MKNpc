package com.chaosbuffalo.mknpc.client.render.models.styling;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelStyle {

    private final ResourceLocation baseTexture;

    private final List<LayerStyle> additionalLayers;

    private final boolean hasArmor;

    public ResourceLocation getBaseTexture() {
        return baseTexture;
    }

    public List<LayerStyle> getAdditionalLayers() {
        return additionalLayers;
    }

    public boolean shouldDrawArmor() {
        return hasArmor;
    }

    public ModelStyle(ResourceLocation textureLoc, boolean hasArmor, LayerStyle... layers){
        this.baseTexture = textureLoc;
        this.hasArmor = hasArmor;
        this.additionalLayers = new ArrayList<>();
        this.additionalLayers.addAll(Arrays.asList(layers));
    }


}
