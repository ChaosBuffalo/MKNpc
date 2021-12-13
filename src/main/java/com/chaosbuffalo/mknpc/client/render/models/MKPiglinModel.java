package com.chaosbuffalo.mknpc.client.render.models;

import com.chaosbuffalo.mknpc.entity.IPiglinActionProvider;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.client.renderer.model.ModelHelper;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.piglin.PiglinAction;
import net.minecraft.util.math.MathHelper;

public class MKPiglinModel<T extends MKEntity & IPiglinActionProvider> extends MKPlayerModel<T> {
    public final ModelRenderer rightEar;
    public final ModelRenderer leftEar;
    private final ModelRenderer bodyDefault;
    private final ModelRenderer headDefault;
    private final ModelRenderer leftArmDefault;
    private final ModelRenderer rightArmDefault;

    public MKPiglinModel(float modelSize, int texWidth, int texHeight, boolean hasTwoEars) {
        super(modelSize, false);
        this.textureWidth = texWidth;
        this.textureHeight = texHeight;
        this.bipedBody = new ModelRenderer(this, 16, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSize);
        this.bipedHead = new ModelRenderer(this);
        //head
        this.bipedHead.setTextureOffset(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, modelSize);
        //nose
        this.bipedHead.setTextureOffset(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, modelSize);
        //tusks
        this.bipedHead.setTextureOffset(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, modelSize);
        this.bipedHead.setTextureOffset(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, modelSize);
        this.rightEar = new ModelRenderer(this);
        this.rightEar.setRotationPoint(4.5F, -6.0F, 0.0F);
        this.rightEar.setTextureOffset(51, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, modelSize);
        this.bipedHead.addChild(this.rightEar);
        this.leftEar = new ModelRenderer(this);
        this.leftEar.setRotationPoint(-4.5F, -6.0F, 0.0F);
        if (hasTwoEars){
            this.leftEar.setTextureOffset(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, modelSize);
        } else {
            this.leftEar.setTextureOffset(51, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, modelSize);
        }
        this.bipedHead.addChild(this.leftEar);
        this.bipedHeadwear = new ModelRenderer(this);
        this.bodyDefault = this.bipedBody.getModelAngleCopy();
        this.headDefault = this.bipedHead.getModelAngleCopy();
        this.leftArmDefault = this.bipedLeftArm.getModelAngleCopy();
        this.rightArmDefault = this.bipedLeftArm.getModelAngleCopy();
    }

    public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.bipedBody.copyModelAngles(this.bodyDefault);
        this.bipedHead.copyModelAngles(this.headDefault);
        this.bipedLeftArm.copyModelAngles(this.leftArmDefault);
        this.bipedRightArm.copyModelAngles(this.rightArmDefault);
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float f = ((float)Math.PI / 6F);
        float f1 = ageInTicks * 0.1F + limbSwing * 0.5F;
        float f2 = 0.08F + limbSwingAmount * 0.4F;
        this.rightEar.rotateAngleZ = (-(float)Math.PI / 6F) - MathHelper.cos(f1 * 1.2F) * f2;
        this.leftEar.rotateAngleZ = ((float)Math.PI / 6F) + MathHelper.cos(f1) * f2;

        PiglinAction piglinaction = entityIn.getPiglinAction();
        if (piglinaction == PiglinAction.DANCING) {
            float f3 = ageInTicks / 60.0F;
            this.leftEar.rotateAngleZ = ((float)Math.PI / 6F) + ((float)Math.PI / 180F) * MathHelper.sin(f3 * 30.0F) * 10.0F;
            this.rightEar.rotateAngleZ = (-(float)Math.PI / 6F) - ((float)Math.PI / 180F) * MathHelper.cos(f3 * 30.0F) * 10.0F;
            this.bipedHead.rotationPointX = MathHelper.sin(f3 * 10.0F);
            this.bipedHead.rotationPointY = MathHelper.sin(f3 * 40.0F) + 0.4F;
            this.bipedRightArm.rotateAngleZ = ((float)Math.PI / 180F) * (70.0F + MathHelper.cos(f3 * 40.0F) * 10.0F);
            this.bipedLeftArm.rotateAngleZ = this.bipedRightArm.rotateAngleZ * -1.0F;
            this.bipedRightArm.rotationPointY = MathHelper.sin(f3 * 40.0F) * 0.5F + 1.5F;
            this.bipedLeftArm.rotationPointY = MathHelper.sin(f3 * 40.0F) * 0.5F + 1.5F;
            this.bipedBody.rotationPointY = MathHelper.sin(f3 * 40.0F) * 0.35F;
        } else if (piglinaction == PiglinAction.ATTACKING_WITH_MELEE_WEAPON && this.swingProgress == 0.0F) {
            this.rotateMainHandArm(entityIn);
        } else if (piglinaction == PiglinAction.CROSSBOW_HOLD) {
            ModelHelper.func_239104_a_(this.bipedRightArm, this.bipedLeftArm, this.bipedHead, !entityIn.isLeftHanded());
        } else if (piglinaction == PiglinAction.CROSSBOW_CHARGE) {
            ModelHelper.func_239102_a_(this.bipedRightArm, this.bipedLeftArm, entityIn, !entityIn.isLeftHanded());
        } else if (piglinaction == PiglinAction.ADMIRING_ITEM) {
            this.bipedHead.rotateAngleX = 0.5F;
            this.bipedHead.rotateAngleY = 0.0F;
            if (entityIn.isLeftHanded()) {
                this.bipedRightArm.rotateAngleY = -0.5F;
                this.bipedRightArm.rotateAngleX = -0.9F;
            } else {
                this.bipedLeftArm.rotateAngleY = 0.5F;
                this.bipedLeftArm.rotateAngleX = -0.9F;
            }
        }
        this.bipedLeftLegwear.copyModelAngles(this.bipedLeftLeg);
        this.bipedRightLegwear.copyModelAngles(this.bipedRightLeg);
        this.bipedLeftArmwear.copyModelAngles(this.bipedLeftArm);
        this.bipedRightArmwear.copyModelAngles(this.bipedRightArm);
        this.bipedBodyWear.copyModelAngles(this.bipedBody);
        this.bipedHeadwear.copyModelAngles(this.bipedHead);
    }

    @Override
    protected void func_230486_a_(T entityIn, float ageInTicks) {
        if (this.swingProgress > 0.0F && entityIn.getPiglinAction() == PiglinAction.ATTACKING_WITH_MELEE_WEAPON) {
            ModelHelper.func_239103_a_(this.bipedRightArm, this.bipedLeftArm, entityIn, this.swingProgress, ageInTicks);
        } else {
            super.func_230486_a_(entityIn, ageInTicks);
        }
    }

    private void rotateMainHandArm(T entityIn) {
        if (entityIn.isLeftHanded()) {
            this.bipedLeftArm.rotateAngleX = -1.8F;
        } else {
            this.bipedRightArm.rotateAngleX = -1.8F;
        }

    }
}
