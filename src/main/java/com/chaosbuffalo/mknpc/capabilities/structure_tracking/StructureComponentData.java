package com.chaosbuffalo.mknpc.capabilities.structure_tracking;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraftforge.common.util.INBTSerializable;


public class StructureComponentData implements INBTSerializable<CompoundNBT> {
    private ResourceLocation pieceName;
    private MutableBoundingBox bounds;

    public StructureComponentData(ResourceLocation pieceName, MutableBoundingBox bounds) {
        this.pieceName = pieceName;
        this.bounds = bounds;
    }

    public StructureComponentData() {
        this.bounds = null;
        this.pieceName = null;
    }

    public MutableBoundingBox getBounds() {
        return bounds;
    }

    public ResourceLocation getPieceName() {
        return pieceName;
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        int[] boundsArr = {bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ};
        nbt.putIntArray("bounds", boundsArr);
        nbt.putString("pieceName", pieceName.toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        int[] boundsArr = nbt.getIntArray("bounds");
        bounds = new MutableBoundingBox(boundsArr);
        pieceName = new ResourceLocation(nbt.getString("pieceName"));
    }
}
