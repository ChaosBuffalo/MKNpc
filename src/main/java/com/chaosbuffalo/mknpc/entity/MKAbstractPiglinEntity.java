package com.chaosbuffalo.mknpc.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.piglin.PiglinAction;
import net.minecraft.item.TieredItem;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.GroundPathHelper;
import net.minecraft.world.World;

public abstract class MKAbstractPiglinEntity extends MKEntity implements IPiglinActionProvider{

    protected MKAbstractPiglinEntity(EntityType<? extends MKAbstractPiglinEntity> type, World worldIn) {
        super(type, worldIn);
        setupBreakDoors();
        this.setPathPriority(PathNodeType.DANGER_FIRE, 16.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
    }

    private void setupBreakDoors() {
        if (GroundPathHelper.isGroundNavigator(this)) {
            ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
        }
    }

    @Override
    public double getYOffset() {
        return this.isChild() ? -0.05D : -0.45D;
    }

    @Override
    public abstract PiglinAction getPiglinAction();

    protected boolean isHoldingMeleeWeapon() {
        return getHeldItemMainhand().getItem() instanceof TieredItem;
    }
}
