package com.chaosbuffalo.mknpc.client.render.models.styling;

import net.minecraft.util.ResourceLocation;

public class OrcLooks {

    public static final ResourceLocation BASE_TEXTURE = new ResourceLocation("mknpc", "textures/entity/green_lady.png");
    private static final ResourceLocation CLOTHES_TEXTURE_1 = new ResourceLocation("mknpc", "textures/entity/orc_clothes_1.png");
    private static final ResourceLocation HAIR_TEXTURE_1 = new ResourceLocation("mknpc", "textures/entity/orc_hair_1.png");
    private static final ResourceLocation LONG_HAIR_TEXTURE_1 = new ResourceLocation("mknpc", "textures/entity/orc_long_hair_layer_2.png");


    public static ModelLook ORC_LOOK_1 = new ModelLook(ModelStyles.LONG_HAIR_STYLE, BASE_TEXTURE, HAIR_TEXTURE_1,
            CLOTHES_TEXTURE_1, LONG_HAIR_TEXTURE_1);

    public static ModelLook DEFAULT = new ModelLook(ModelStyles.BASIC_STYLE, BASE_TEXTURE);
}
