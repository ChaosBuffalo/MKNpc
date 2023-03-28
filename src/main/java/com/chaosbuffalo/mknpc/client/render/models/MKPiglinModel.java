package com.chaosbuffalo.mknpc.client.render.models;

import com.chaosbuffalo.mknpc.entity.IPiglinActionProvider;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.util.Mth;

//public class MKPiglinModel<T extends MKEntity & IPiglinActionProvider> extends MKPlayerModel<T> {
//    public final ModelPart rightEar;
//    public final ModelPart leftEar;
//    private final ModelPart bodyDefault;
//    private final ModelPart headDefault;
//    private final ModelPart leftArmDefault;
//    private final ModelPart rightArmDefault;
//
//    public MKPiglinModel(float modelSize, int texWidth, int texHeight, boolean hasTwoEars) {
//        super(modelSize, false);
//        this.texWidth = texWidth;
//        this.texHeight = texHeight;
//        this.body = new ModelPart(this, 16, 16);
//        this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSize);
//        this.head = new ModelPart(this);
//        //head
//        this.head.texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, modelSize);
//        //nose
//        this.head.texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, modelSize);
//        //tusks
//        this.head.texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, modelSize);
//        this.head.texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, modelSize);
//        this.rightEar = new ModelPart(this);
//        this.rightEar.setPos(4.5F, -6.0F, 0.0F);
//        this.rightEar.texOffs(51, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, modelSize);
//        this.head.addChild(this.rightEar);
//        this.leftEar = new ModelPart(this);
//        this.leftEar.setPos(-4.5F, -6.0F, 0.0F);
//        if (hasTwoEars){
//            this.leftEar.texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, modelSize);
//        } else {
//            this.leftEar.texOffs(51, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, modelSize);
//        }
//        this.head.addChild(this.leftEar);
//        this.hat = new ModelPart(this);
//        this.bodyDefault = this.body.createShallowCopy();
//        this.headDefault = this.head.createShallowCopy();
//        this.leftArmDefault = this.leftArm.createShallowCopy();
//        this.rightArmDefault = this.leftArm.createShallowCopy();
//    }
//
//    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//        this.body.copyFrom(this.bodyDefault);
//        this.head.copyFrom(this.headDefault);
//        this.leftArm.copyFrom(this.leftArmDefault);
//        this.rightArm.copyFrom(this.rightArmDefault);
//        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
//        float f = ((float)Math.PI / 6F);
//        float f1 = ageInTicks * 0.1F + limbSwing * 0.5F;
//        float f2 = 0.08F + limbSwingAmount * 0.4F;
//        this.rightEar.zRot = (-(float)Math.PI / 6F) - Mth.cos(f1 * 1.2F) * f2;
//        this.leftEar.zRot = ((float)Math.PI / 6F) + Mth.cos(f1) * f2;
//
//        PiglinArmPose piglinaction = entityIn.getPiglinAction();
//        if (piglinaction == PiglinArmPose.DANCING) {
//            float f3 = ageInTicks / 60.0F;
//            this.leftEar.zRot = ((float)Math.PI / 6F) + ((float)Math.PI / 180F) * Mth.sin(f3 * 30.0F) * 10.0F;
//            this.rightEar.zRot = (-(float)Math.PI / 6F) - ((float)Math.PI / 180F) * Mth.cos(f3 * 30.0F) * 10.0F;
//            this.head.x = Mth.sin(f3 * 10.0F);
//            this.head.y = Mth.sin(f3 * 40.0F) + 0.4F;
//            this.rightArm.zRot = ((float)Math.PI / 180F) * (70.0F + Mth.cos(f3 * 40.0F) * 10.0F);
//            this.leftArm.zRot = this.rightArm.zRot * -1.0F;
//            this.rightArm.y = Mth.sin(f3 * 40.0F) * 0.5F + 1.5F;
//            this.leftArm.y = Mth.sin(f3 * 40.0F) * 0.5F + 1.5F;
//            this.body.y = Mth.sin(f3 * 40.0F) * 0.35F;
//        } else if (piglinaction == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON && this.attackTime == 0.0F) {
//            this.rotateMainHandArm(entityIn);
//        } else if (piglinaction == PiglinArmPose.CROSSBOW_HOLD) {
//            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, !entityIn.isLeftHanded());
//        } else if (piglinaction == PiglinArmPose.CROSSBOW_CHARGE) {
//            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entityIn, !entityIn.isLeftHanded());
//        } else if (piglinaction == PiglinArmPose.ADMIRING_ITEM) {
//            this.head.xRot = 0.5F;
//            this.head.yRot = 0.0F;
//            if (entityIn.isLeftHanded()) {
//                this.rightArm.yRot = -0.5F;
//                this.rightArm.xRot = -0.9F;
//            } else {
//                this.leftArm.yRot = 0.5F;
//                this.leftArm.xRot = -0.9F;
//            }
//        }
//        this.bipedLeftLegwear.copyFrom(this.leftLeg);
//        this.bipedRightLegwear.copyFrom(this.rightLeg);
//        this.bipedLeftArmwear.copyFrom(this.leftArm);
//        this.bipedRightArmwear.copyFrom(this.rightArm);
//        this.bipedBodyWear.copyFrom(this.body);
//        this.hat.copyFrom(this.head);
//    }
//
//    @Override
//    protected void setupAttackAnimation(T entityIn, float ageInTicks) {
//        if (this.attackTime > 0.0F && entityIn.getPiglinAction() == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON) {
//            AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, entityIn, this.attackTime, ageInTicks);
//        } else {
//            super.setupAttackAnimation(entityIn, ageInTicks);
//        }
//    }
//
//    private void rotateMainHandArm(T entityIn) {
//        if (entityIn.isLeftHanded()) {
//            this.leftArm.xRot = -1.8F;
//        } else {
//            this.rightArm.xRot = -1.8F;
//        }
//
//    }
//}
