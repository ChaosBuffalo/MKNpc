package com.chaosbuffalo.mknpc.npc.options;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.UUID;

public class NameOption extends StringOption implements INameProvider {
    public static final ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "name");

    public NameOption() {
        super(NAME);
    }

    @Override
    public StringTextComponent getEntityName(NpcDefinition definition, World world, UUID spawnId) {
        return new StringTextComponent(getValue());
    }

    @Override
    public void applyToEntity(NpcDefinition definition, Entity entity, String value) {
        if (!value.isEmpty()) {
            entity.setCustomName(new StringTextComponent(value));
        }
    }

    @Nullable
    @Override
    public String getDisplayName() {
        return getValue();
    }
}
