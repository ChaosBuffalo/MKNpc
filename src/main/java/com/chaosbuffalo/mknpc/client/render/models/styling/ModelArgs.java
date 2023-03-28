package com.chaosbuffalo.mknpc.client.render.models.styling;

import net.minecraft.client.model.geom.builders.CubeDeformation;

public class ModelArgs {
    public CubeDeformation deformation;
    public boolean overrideBaseParts;
    public float heightOffset;

    public ModelArgs(CubeDeformation deformation, boolean overrideBaseParts, float heightOffset) {
        this.heightOffset = heightOffset;
        this.overrideBaseParts = overrideBaseParts;
        this.deformation = deformation;
    }

    public ModelArgs copyWithOverrides(boolean override, CubeDeformation deformation) {
        return new ModelArgs(deformation, override, heightOffset);
    }
}