package com.chaosbuffalo.mknpc.client.render.renderers;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.client.render.models.styling.LayerStyle;
import com.chaosbuffalo.mknpc.client.render.models.styling.ModelStyle;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class SkeletonStyles {
    public static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation(
            "textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation STRAY_SKELETON_TEXTURES = new ResourceLocation(
            "textures/entity/skeleton/stray.png");
    private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation(
            "textures/entity/skeleton/wither_skeleton.png");

    private static final ResourceLocation ASSASSIN_CLOTHES = new ResourceLocation(MKNpc.MODID,
            "textures/entity/skeleton/assassin.png");
    private static final ResourceLocation KING_CLOTHES = new ResourceLocation(MKNpc.MODID,
            "textures/entity/skeleton/king.png");
    private static final ResourceLocation STRAY_CLOTHES_TEXTURES = new ResourceLocation(
            "textures/entity/skeleton/stray_overlay.png");

    private static final Map<String, ResourceLocation> TEXTURE_VARIANTS = new HashMap<>();
    private static final Map<String, ResourceLocation> CLOTHING_VARIANTS = new HashMap<>();

    public static final Map<String, ModelStyle> SKELETON_STYLES = new HashMap<>();

    public static void putStyle(String name, ModelStyle style){
        SKELETON_STYLES.put(name, style);
    }

    static {
        TEXTURE_VARIANTS.put("wither", WITHER_SKELETON_TEXTURES);
        TEXTURE_VARIANTS.put("stray", STRAY_SKELETON_TEXTURES);
        TEXTURE_VARIANTS.put("default", SKELETON_TEXTURES);
        CLOTHING_VARIANTS.put("assassin", ASSASSIN_CLOTHES);
        CLOTHING_VARIANTS.put("king", KING_CLOTHES);
        CLOTHING_VARIANTS.put("stray", STRAY_CLOTHES_TEXTURES);

        for (Map.Entry<String, ResourceLocation> textureVariant : TEXTURE_VARIANTS.entrySet()){
            for (Map.Entry<String, ResourceLocation> clothingVariant : CLOTHING_VARIANTS.entrySet()){
                putStyle(String.format("%s_%s", textureVariant.getKey(), clothingVariant.getKey()),
                        new ModelStyle(textureVariant.getValue(), true,
                                new LayerStyle(clothingVariant.getValue(), 0.25f)));
            }
            putStyle(textureVariant.getKey(), new ModelStyle(textureVariant.getValue(), true));
        }

    }
}
