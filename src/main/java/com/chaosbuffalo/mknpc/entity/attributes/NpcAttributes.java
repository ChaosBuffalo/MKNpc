package com.chaosbuffalo.mknpc.entity.attributes;

import net.minecraft.entity.ai.attributes.RangedAttribute;

public class NpcAttributes {

        public static final RangedAttribute AGGRO_RANGE = (RangedAttribute) new RangedAttribute(null,
                "mk.aggro_range", 5, 0, 128)
                .setDescription("Aggro Range")
                .setShouldWatch(true);


}
