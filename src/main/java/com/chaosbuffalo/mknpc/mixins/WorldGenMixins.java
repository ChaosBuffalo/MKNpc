package com.chaosbuffalo.mknpc.mixins;


import com.chaosbuffalo.mknpc.init.MKNpcWorldGen;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.LakesFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.stream.Stream;

@Mixin(LakesFeature.class)
public class WorldGenMixins {


    @Redirect(at = @At(value = "INVOKE", target="Lnet/minecraft/world/ISeedReader;func_241827_a(Lnet/minecraft/util/math/SectionPos;Lnet/minecraft/world/gen/feature/structure/Structure;)Ljava/util/stream/Stream;"),
            method = "Lnet/minecraft/world/gen/feature/LakesFeature;generate(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/BlockStateFeatureConfig;)Z")
    private Stream<? extends StructureStart<?>> proxyGetStructures(ISeedReader iSeedReader, SectionPos p_241827_1_, Structure<?> p_241827_2_){
            Stream<? extends StructureStart<?>> original = iSeedReader.func_241827_a(p_241827_1_, p_241827_2_);
            Stream<? extends StructureStart<?>> ours = original;
            for (Structure<?> struct : MKNpcWorldGen.NO_WATER_STRUCTURES){
                ours = Stream.concat(ours, iSeedReader.func_241827_a(p_241827_1_, struct));
            }
            return ours;
    }


}
