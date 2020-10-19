package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.UUID;

public class MKStructurePieceArgs {
    public final Structure<?> structure;
    public final TemplateManager templateManager;
    public final BlockPos blockPos;
    public final Rotation rotation;
    public final SharedSeedRandom random;
    public final UUID structureId;
    public final List<StructurePiece> componentsOut;

    public MKStructurePieceArgs(Structure<?> structure, TemplateManager templateManager,
                                BlockPos blockPos, Rotation rotation, SharedSeedRandom random,
                                UUID structureId, List<StructurePiece> componentsOut){
        this.structure = structure;
        this.templateManager = templateManager;
        this.blockPos = blockPos;
        this.rotation = rotation;
        this.random = random;
        this.structureId = structureId;
        this.componentsOut = componentsOut;
    }
}
