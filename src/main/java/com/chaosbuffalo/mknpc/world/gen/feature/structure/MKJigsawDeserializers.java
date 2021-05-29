package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import net.minecraft.world.gen.feature.jigsaw.IJigsawDeserializer;

public class MKJigsawDeserializers {

    public static final IJigsawDeserializer<MKSingleJigsawPiece> MK_SINGLE_JIGSAW_DESERIALIZER = IJigsawDeserializer.
            func_236851_a_("mk_single_jigsaw", MKSingleJigsawPiece.codec);
}
