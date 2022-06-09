package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.client.rendering.skeleton.BipedSkeleton;
import com.chaosbuffalo.mkcore.client.rendering.skeleton.MCBone;
import com.chaosbuffalo.mkcore.fx.particles.ParticleAnimation;
import com.chaosbuffalo.mkcore.fx.particles.ParticleAnimationManager;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.client.render.models.layers.MKAdditionalBipedLayer;
import com.chaosbuffalo.mknpc.client.render.models.styling.LayerStyle;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelLook;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyle;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Optional;
import java.util.function.Function;

public class MKBipedRenderer<T extends MKEntity, M extends BipedModel<T>> extends BipedRenderer<T, M> implements ILayerTextureProvider<T, M> {
    private final ModelStyle style;
    private final float defaultShadowSize;
    private ModelLook look;
    private final ModelLook defaultLook;
    private final BipedSkeleton<T, M> skeleton;

    public MKBipedRenderer(EntityRendererManager rendererManager, M modelBipedIn,
                           Function<Float, M> modelSupplier, ModelStyle style,
                           ModelLook defaultLook, float shadowSize) {
        super(rendererManager, modelBipedIn, shadowSize);
        this.style = style;
        this.defaultShadowSize = shadowSize;
        this.defaultLook = defaultLook;
        this.skeleton = new BipedSkeleton<>(modelBipedIn);
        for (LayerStyle layer : style.getAdditionalLayers()){
            addLayer(new MKAdditionalBipedLayer<>(this, modelSupplier, layer));
        }
        if (style.shouldDrawArmor()){
            addLayer(new BipedArmorLayer<>(this, modelSupplier.apply(.5f), modelSupplier.apply(1.0f)));
        }

    }

    public void setLook(ModelLook look) {
        this.look = look;
    }

    public ModelLook getLook() {
        return look != null ? look : defaultLook;
    }

    public ModelStyle getStyle() {
        return style;
    }

    @Override
    protected void preRenderCallback(T entity, MatrixStack matrixStackIn, float partialTickTime) {
        float scale = entity.getRenderScale();
        this.shadowSize = defaultShadowSize * scale;
        matrixStackIn.scale(scale, scale, scale);
    }

    @Override
    public ResourceLocation getLayerTexture(String layerName, T entity) {
        ResourceLocation tex = getLook().getLayerTexture(layerName);
        if (tex == null) {
            MKNpc.LOGGER.error("Layer texture {} missing for {}", layerName, entity);
        }
        return tex != null ? tex : ModelLook.MISSING_TEXTURE;
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return getLook().getBaseTexture();
    }

    @Override
    public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        MKEntity.VisualCastState castState = entityIn.getVisualCastState();
        if (castState == MKEntity.VisualCastState.CASTING || castState == MKEntity.VisualCastState.RELEASE){
            MKAbility ability = entityIn.getCastingAbility();
            if (ability != null){
                if (ability.hasCastingParticles()){
                    ParticleAnimation anim = ParticleAnimationManager.ANIMATIONS.get(ability.getCastingParticles());
                    if (anim != null){
                        Optional<Vector3d> leftPos = getHandPosition(partialTicks, entityIn, HandSide.LEFT);
                        leftPos.ifPresent(pos -> anim.spawn(entityIn.getEntityWorld(), pos, null));
                        Optional<Vector3d> rightPos = getHandPosition(partialTicks, entityIn, HandSide.RIGHT);
                        rightPos.ifPresent(pos -> anim.spawn(entityIn.getEntityWorld(), pos, null));
                    }
                }
            }
        }
        entityIn.getParticleEffectTracker().getParticleInstances().forEach(instance -> {
            instance.update(entityIn, skeleton, partialTicks, getRenderOffset(entityIn, partialTicks));
        });
    }

    private Optional<Vector3d> getHandPosition(float partialTicks, T entityIn, HandSide handSide){
        return MCBone.getPositionOfBoneInWorld(entityIn, skeleton, partialTicks,
                getRenderOffset(entityIn, partialTicks), handSide == HandSide.LEFT ?
                        BipedSkeleton.LEFT_HAND_BONE_NAME : BipedSkeleton.RIGHT_HAND_BONE_NAME);
    }
}
