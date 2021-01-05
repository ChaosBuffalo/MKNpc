package com.chaosbuffalo.mknpc.client.render.models.styling;

public class LayerStyle {

    private final float layerSize;
    private final String layerName;

    public float getLayerSize() {
        return layerSize;
    }

    public String getLayerName() {
        return layerName;
    }

    public LayerStyle(String layerName, float size){
        this.layerSize = size;
        this.layerName = layerName;
    }
}
