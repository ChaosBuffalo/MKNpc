package com.chaosbuffalo.mknpc.mixins;

import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingRenderer.class)
public class LivingRendererMixins {

    @Unique
    private LivingEntity toRender;

    @ModifyVariable(
            method = "Lnet/minecraft/client/renderer/entity/LivingRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At("HEAD"),
            index = 1,
            ordinal = 0,
            argsOnly = true
    )
    private LivingEntity captureSource(LivingEntity entity) {
        this.toRender = entity;
        return entity;
    }

    @ModifyConstant(method = "Lnet/minecraft/client/renderer/entity/LivingRenderer;render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            constant = @Constant(floatValue = 0.15f))
    private float modifyTransparency(float value) {
        if (toRender instanceof MKEntity){
            return ((MKEntity) toRender).getTranslucency();
        }
        return value;
    }
}
