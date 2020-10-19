package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;


public interface INameProvider {

    StringTextComponent getEntityName(NpcDefinition definition, World world, UUID spawnId);

    @Nullable
    String getDisplayName();
}
