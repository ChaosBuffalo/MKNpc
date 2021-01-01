package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.client.render.models.MKSkeletalModel;
import com.chaosbuffalo.mknpc.client.render.models.styling.LayerStyle;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyle;
import com.chaosbuffalo.mknpc.entity.MKSkeletonEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;


public class SkeletalRenderer extends MKBipedRenderer<MKSkeletonEntity, MKSkeletalModel<MKSkeletonEntity>> {
    private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation(
            "textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation ASSASSIN_CLOTHES = new ResourceLocation(MKNpc.MODID,
            "textures/entity/skeleton/assassin.png");
    private static final ResourceLocation KING_CLOTHES = new ResourceLocation(MKNpc.MODID,
            "textures/entity/skeleton/king.png");

    public static final ModelStyle ASSASSIN_MODEL_STYLE = new ModelStyle(SKELETON_TEXTURES, true,
            new LayerStyle(ASSASSIN_CLOTHES, 0.25f));

    public static final ModelStyle KING_MODEL_STYLE = new ModelStyle(SKELETON_TEXTURES, true,
            new LayerStyle(KING_CLOTHES, 0.25f));

    public SkeletalRenderer(EntityRendererManager rendererManager, ModelStyle style) {
        super(rendererManager, new MKSkeletalModel<>(0.0f, false),
                (size) -> new MKSkeletalModel<>(size, true), style,
                0.5f);
    }
}
