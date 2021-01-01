package com.chaosbuffalo.mknpc.client.render.models;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;

public class MKSkeletalModel<T extends MKEntity> extends MKBipedModel<T> {

    private final boolean isFullSize;

    public MKSkeletalModel(float modelSize, boolean fullSize) {
        super(modelSize);
        this.isFullSize = fullSize;
        if (!fullSize) {
            this.bipedRightArm = new ModelRenderer(this, 40, 16);
            this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
            this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
            this.bipedLeftArm = new ModelRenderer(this, 40, 16);
            this.bipedLeftArm.mirror = true;
            this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
            this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            this.bipedRightLeg = new ModelRenderer(this, 0, 16);
            this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
            this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
            this.bipedLeftLeg = new ModelRenderer(this, 0, 16);
            this.bipedLeftLeg.mirror = true;
            this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, modelSize);
            this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
        }
    }

    public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
        if (!isFullSize){
            float f = sideIn == HandSide.RIGHT ? 1.0F : -1.0F;
            ModelRenderer modelrenderer = this.getArmForSide(sideIn);
            modelrenderer.rotationPointX += f;
            modelrenderer.translateRotate(matrixStackIn);
            modelrenderer.rotationPointX -= f;
        } else {
            super.translateHand(sideIn, matrixStackIn);
        }
    }
}
