package com.chaosbuffalo.mknpc.client.render.models;

import com.chaosbuffalo.mkcore.client.rendering.animations.AdditionalBipedAnimation;
import com.chaosbuffalo.mkcore.client.rendering.animations.BipedCastAnimation;
import com.chaosbuffalo.mknpc.client.render.animations.MKEntityCompleteCastAnimation;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;


public class MKBipedModel<T extends MKEntity> extends BipedModel<T> {
    private final BipedCastAnimation<MKEntity> castAnimation = new BipedCastAnimation<>(this);
    private final MKEntityCompleteCastAnimation completeCastAnimation = new MKEntityCompleteCastAnimation(this);


    public MKBipedModel(float modelSize, float yOffsetIn, int textureWidthIn, int textureHeightIn) {
        super(modelSize, yOffsetIn, textureWidthIn, textureHeightIn);
    }

    @Override
    public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        // bow pose stuff from skeleton
        ItemStack itemstack = entityIn.getHeldItem(Hand.MAIN_HAND);
        if (itemstack.getItem() instanceof BowItem && entityIn.isAggressive()) {
            if (entityIn.getPrimaryHand() == HandSide.RIGHT) {
                this.rightArmPose = ArmPose.BOW_AND_ARROW;
                this.leftArmPose = ArmPose.EMPTY;
            } else {
                this.leftArmPose = ArmPose.BOW_AND_ARROW;
                this.rightArmPose = ArmPose.EMPTY;
            }
        }
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount,
                                  float ageInTicks, float netHeadYaw, float headPitch) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // bow pose stuff from skeleton
        ItemStack itemstack = entityIn.getHeldItemMainhand();
        if (entityIn.isAggressive() && (itemstack.isEmpty() || !(itemstack.getItem() instanceof net.minecraft.item.BowItem))) {
            float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
            float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
            this.bipedRightArm.rotateAngleZ = 0.0F;
            this.bipedLeftArm.rotateAngleZ = 0.0F;
            this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
            this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
            this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F);
            this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F);
            this.bipedRightArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
            this.bipedLeftArm.rotateAngleX -= f * 1.2F - f1 * 0.4F;
            ModelHelper.func_239101_a_(this.bipedRightArm, this.bipedLeftArm, ageInTicks);
        }
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
