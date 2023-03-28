package com.chaosbuffalo.mknpc.client.render.models;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

import java.util.List;
import java.util.Random;

//public class MKPlayerModel<T extends MKEntity> extends MKBipedModel<T> {
//    private List<ModelPart> modelRenderers = Lists.newArrayList();
//    private final boolean hasSmallArms;
//    public final ModelPart bipedLeftArmwear;
//    public final ModelPart bipedRightArmwear;
//    public final ModelPart bipedLeftLegwear;
//    public final ModelPart bipedRightLegwear;
//    public final ModelPart bipedBodyWear;
//
//    public MKPlayerModel(float modelSize, boolean smallArmsIn) {
//        super(RenderType::entityTranslucent, modelSize, 0.0F, 64, 64);
//        this.hasSmallArms = smallArmsIn;
//        if (smallArmsIn) {
//            this.leftArm = new ModelPart(this, 32, 48);
//            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
//            this.leftArm.setPos(5.0F, 2.5F, 0.0F);
//            this.rightArm = new ModelPart(this, 40, 16);
//            this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
//            this.rightArm.setPos(-5.0F, 2.5F, 0.0F);
//            this.bipedLeftArmwear = new ModelPart(this, 48, 48);
//            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize + 0.25F);
//            this.bipedLeftArmwear.setPos(5.0F, 2.5F, 0.0F);
//            this.bipedRightArmwear = new ModelPart(this, 40, 32);
//            this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize + 0.25F);
//            this.bipedRightArmwear.setPos(-5.0F, 2.5F, 10.0F);
//        } else {
//            this.leftArm = new ModelPart(this, 32, 48);
//            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
//            this.leftArm.setPos(5.0F, 2.0F, 0.0F);
//            this.bipedLeftArmwear = new ModelPart(this, 48, 48);
//            this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize + 0.25F);
//            this.bipedLeftArmwear.setPos(5.0F, 2.0F, 0.0F);
//            this.bipedRightArmwear = new ModelPart(this, 40, 32);
//            this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize + 0.25F);
//            this.bipedRightArmwear.setPos(-5.0F, 2.0F, 10.0F);
//        }
//        this.leftLeg = new ModelPart(this, 16, 48);
//        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
//        this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
//        this.bipedLeftLegwear = new ModelPart(this, 0, 48);
//        this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize + 0.25F);
//        this.bipedLeftLegwear.setPos(1.9F, 12.0F, 0.0F);
//        this.bipedRightLegwear = new ModelPart(this, 0, 32);
//        this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize + 0.25F);
//        this.bipedRightLegwear.setPos(-1.9F, 12.0F, 0.0F);
//        this.bipedBodyWear = new ModelPart(this, 16, 32);
//        this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSize + 0.25F);
//        this.bipedBodyWear.setPos(0.0F, 0.0F, 0.0F);
//    }
//
//    public void setAllVisible(boolean visible) {
//        super.setAllVisible(visible);
//        this.bipedLeftArmwear.visible = visible;
//        this.bipedRightArmwear.visible = visible;
//        this.bipedLeftLegwear.visible = visible;
//        this.bipedRightLegwear.visible = visible;
//        this.bipedBodyWear.visible = visible;
//    }
//
//    protected Iterable<ModelPart> bodyParts() {
//        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.bipedLeftLegwear,
//                this.bipedRightLegwear, this.bipedLeftArmwear, this.bipedRightArmwear, this.bipedBodyWear));
//    }
//
//    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//        super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
//        this.bipedLeftLegwear.copyFrom(this.leftLeg);
//        this.bipedRightLegwear.copyFrom(this.rightLeg);
//        this.bipedLeftArmwear.copyFrom(this.leftArm);
//        this.bipedRightArmwear.copyFrom(this.rightArm);
//        this.bipedBodyWear.copyFrom(this.body);
//    }
//
//    public void translateToHand(HumanoidArm sideIn, PoseStack matrixStackIn) {
//        ModelPart modelrenderer = this.getArm(sideIn);
//        if (this.hasSmallArms) {
//            float f = 0.5F * (float)(sideIn == HumanoidArm.RIGHT ? 1 : -1);
//            modelrenderer.x += f;
//            modelrenderer.translateAndRotate(matrixStackIn);
//            modelrenderer.x -= f;
//        } else {
//            modelrenderer.translateAndRotate(matrixStackIn);
//        }
//
//    }
//
//    public ModelPart getRandomModelRenderer(Random randomIn) {
//        return this.modelRenderers.get(randomIn.nextInt(this.modelRenderers.size()));
//    }
//
//    @Override
//    public void accept(ModelPart rendererIn) {
//        if (this.modelRenderers == null) {
//            this.modelRenderers = Lists.newArrayList();
//        }
//        this.modelRenderers.add(rendererIn);
//    }
//}
