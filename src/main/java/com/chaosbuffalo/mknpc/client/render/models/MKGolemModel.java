package com.chaosbuffalo.mknpc.client.render.models;


import com.chaosbuffalo.mkcore.client.rendering.animations.AdditionalBipedAnimation;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class MKGolemModel<T extends MKEntity> extends MKBipedModel<T> {


    public MKGolemModel(float modelSize) {
        super(modelSize);
        bipedHead = new ModelRenderer(this).setTextureSize(128, 128);
        bipedHead.setRotationPoint(0.0f, -7.0f, -2.0f);
        bipedHead.setTextureOffset(0, 0).addBox(-4.0f, -12.0f, -5.5f, 8.0f, 10.0f, 8.0f, modelSize);
        bipedHead.setTextureOffset(24, 0).addBox(-1.0f, -5.0f, -7.5f, 2.0f, 4.0f, 2.0f, modelSize);
        bipedBody = new ModelRenderer(this).setTextureSize(128, 128);
        bipedBody.setRotationPoint(0.0f, -7.0f, 0.0f);
        bipedBody.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, modelSize);
        bipedBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, modelSize + 0.5f);
        bipedRightArm = new ModelRenderer(this).setTextureSize(128, 128);
        bipedRightArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        bipedRightArm.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, modelSize);
        bipedLeftArm = new ModelRenderer(this).setTextureSize(128, 128);
        bipedLeftArm.setRotationPoint(0.0F, -7.0F, 0.0F);
        bipedLeftArm.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, modelSize);
        bipedLeftLeg = new ModelRenderer(this, 0, 22).setTextureSize(128, 128);
        bipedLeftLeg.setRotationPoint(-4.0F, 11.0F, 0.0F);
        bipedLeftLeg.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, modelSize);
        bipedRightLeg = new ModelRenderer(this, 0, 22).setTextureSize(128, 128);
        bipedRightLeg.mirror = true;
        bipedRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0F, 11.0F, 0.0F);
        bipedRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, modelSize);
    }

    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        bipedHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
        bipedHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
        bipedLeftLeg.rotateAngleX = -1.5F * MathHelper.func_233021_e_(limbSwing, 13.0F) * limbSwingAmount;
        bipedRightLeg.rotateAngleX = 1.5F * MathHelper.func_233021_e_(limbSwing, 13.0F) * limbSwingAmount;
        bipedLeftLeg.rotateAngleY = 0.0F;
        bipedRightLeg.rotateAngleY = 0.0F;

        AdditionalBipedAnimation<MKEntity> animation = getAdditionalAnimation(entityIn);
        if (animation != null) {
            animation.apply(entityIn);
        }
    }

    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        float swingProgress = entityIn.getSwingProgress(partialTick);
        if (swingProgress > 0) {
            bipedRightArm.rotateAngleX = -2.0F + 1.5F * swingProgress;
            bipedLeftArm.rotateAngleX = -2.0F + 1.5F * swingProgress;
        } else {
            bipedRightArm.rotateAngleX = (-0.2F + 1.5F * MathHelper.func_233021_e_(limbSwing, 13.0F)) * limbSwingAmount;
            bipedLeftArm.rotateAngleX = (-0.2F - 1.5F * MathHelper.func_233021_e_(limbSwing, 13.0F)) * limbSwingAmount;

        }

    }
}