package com.chaosbuffalo.mknpc.client.render.models;

import com.chaosbuffalo.mkcore.client.rendering.animations.AdditionalBipedAnimation;
import com.chaosbuffalo.mkcore.client.rendering.animations.BipedCastAnimation;
import com.chaosbuffalo.mknpc.client.render.animations.MKEntityCompleteCastAnimation;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;

public class MKBipedModel<T extends MKEntity> extends BipedModel<T> {
    private final BipedCastAnimation<MKEntity> castAnimation = new BipedCastAnimation<>(this);
    private final MKEntityCompleteCastAnimation completeCastAnimation = new MKEntityCompleteCastAnimation(this);


    public MKBipedModel(float modelSize, float yOffsetIn, int textureWidthIn, int textureHeightIn) {
        super(modelSize, yOffsetIn, textureWidthIn, textureHeightIn);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount,
                                  float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        AdditionalBipedAnimation<MKEntity> animation = getAdditionalAnimation(entityIn);
        if (animation != null) {
            animation.apply(entityIn);
        }
    }

    public AdditionalBipedAnimation<MKEntity> getAdditionalAnimation(T entityIn) {
        switch (entityIn.getVisualCastState()) {
            case CASTING:
                return castAnimation;
            case RELEASE:
                return completeCastAnimation;
            case NONE:
            default:
                return null;
        }
    }

    public MKBipedModel(float modelSize) {
        super(modelSize);
    }
}
