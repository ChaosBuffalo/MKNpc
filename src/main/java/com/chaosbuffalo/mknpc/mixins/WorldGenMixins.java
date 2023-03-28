package com.chaosbuffalo.mknpc.mixins;


import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

@Mixin(LakeFeature.class)
public class WorldGenMixins {


    @Redirect(at = @At(value = "INVOKE", target="Lnet/minecraft/world/level/WorldGenLevel;startsForFeature(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/levelgen/feature/StructureFeature;)Ljava/util/stream/Stream;"),
            method = "Lnet/minecraft/world/level/levelgen/feature/LakeFeature;place(Lnet/minecraft/world/level/levelgen/feature/FeaturePlaceContext;)Z")
    private Stream<? extends StructureStart<?>> proxyGetStructures(WorldGenLevel iSeedReader, SectionPos p_241827_1_, StructureFeature<?> p_241827_2_){
            Stream<? extends StructureStart<?>> original = iSeedReader.startsForFeature(p_241827_1_, p_241827_2_);
            Stream<? extends StructureStart<?>> ours = original;
            for (StructureFeature<?> struct : MKNpcWorldGen.NO_WATER_STRUCTURES){
                ours = Stream.concat(ours, iSeedReader.startsForFeature(p_241827_1_, struct));
            }
            return ours;
    }


}
