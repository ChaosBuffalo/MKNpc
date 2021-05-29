package com.chaosbuffalo.mknpc.client.render.models.styling;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ModelLook {

    private final ResourceLocation baseTexture;
    private final ModelStyle baseStyle;
    private final Map<String, ResourceLocation> layerTexture;

    public ModelLook(ModelStyle baseStyle, ResourceLocation baseTexture, ResourceLocation... textureLayers){
        this.baseTexture = baseTexture;
        this.baseStyle = baseStyle;
        this.layerTexture = new HashMap<>();
        setTexturesForLayers(baseStyle, textureLayers);
    }

    public void setTexturesForLayers(ModelStyle style, ResourceLocation... textures){
        int index = 0;
        for (LayerStyle layer : style.getAdditionalLayers()){
            layerTexture.put(layer.getLayerName(), textures[index]);
            index++;
        }
    }

    @Nullable
    public ResourceLocation getLayerTexture(String layer){
        return layerTexture.get(layer);
    }

    public ResourceLocation getBaseTexture() {
        return baseTexture;
    }

    public String getStyleName(boolean isWearingArmor) {
        return baseStyle.needsArmorVariant() && isWearingArmor ? String.format("%s_armored", baseStyle.getName()) : baseStyle.getName();
    }
}
