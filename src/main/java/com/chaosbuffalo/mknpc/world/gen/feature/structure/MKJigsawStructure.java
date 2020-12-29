package com.chaosbuffalo.mknpc.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.UUID;

public class MKJigsawStructure extends JigsawStructure {


    public MKJigsawStructure(Codec<VillageConfig> codec, int groundLevel, boolean offsetVertical,
                             boolean offsetFromWorldSurface) {
        super(codec, groundLevel, offsetVertical, offsetFromWorldSurface);
    }

    public Structure.IStartFactory<VillageConfig> getStartFactory() {
        return (p_242778_1_, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_) ->
                new Start(this, p_242778_2_, p_242778_3_, p_242778_4_, p_242778_5_, p_242778_6_);
    }



    public static class Start extends MarginedStructureStart<VillageConfig> {
        private final MKJigsawStructure structure;
        private final UUID instanceId;

        public Start(MKJigsawStructure p_i241979_1_, int p_i241979_2_, int p_i241979_3_,
                     MutableBoundingBox p_i241979_4_, int p_i241979_5_, long seed) {
            super(p_i241979_1_, p_i241979_2_, p_i241979_3_, p_i241979_4_, p_i241979_5_, seed);
            this.structure = p_i241979_1_;
            instanceId = UUID.randomUUID();
        }

        public UUID getInstanceId() {
            return instanceId;
        }

        @Override
        public CompoundNBT write(int chunkX, int chunkZ) {
            CompoundNBT tag = super.write(chunkX, chunkZ);
            if (isValid()){
                tag.putString("instanceId", instanceId.toString());
            }
            return tag;
        }

        public void func_230364_a_(DynamicRegistries dynamicRegistries, ChunkGenerator chunkGenerator,
                                   TemplateManager templateManager, int chunkX, int chunkY, Biome biome,
                                   VillageConfig config) {
            BlockPos blockpos = new BlockPos(chunkX * 16, this.structure.field_242774_u, chunkY * 16);
            JigsawManager.func_242837_a(dynamicRegistries, config,
                    (tempManager, piece, pos, groundLevelDelta, rotation, boundingBox) ->
                            new MKAbstractJigsawPiece(tempManager, piece, pos, groundLevelDelta, rotation,
                                    boundingBox, getStructure().getRegistryName(), getInstanceId()),
                    chunkGenerator, templateManager, blockpos, this.components, this.rand,
                    this.structure.field_242775_v, this.structure.field_242776_w);
            this.recalculateStructureSize();
        }
    }
}
