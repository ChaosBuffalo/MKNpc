package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.SingleChunkConfig;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.TestStructure;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.TestStructurePieces;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKNpcWorldGen {

    public static IStructurePieceType TEST_PIECE_TYPE;
    public static TestStructure TEST_STRUCTURE;
    public static final ResourceLocation TEST_STRUCTURE_NAME = new ResourceLocation(MKNpc.MODID, "test");

    public static void registerStructurePieces(){
        TEST_PIECE_TYPE = Registry.register(Registry.STRUCTURE_PIECE, TEST_STRUCTURE_NAME.toString(),
                TestStructurePieces.Piece::new);
    }

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> evt){
        TEST_STRUCTURE = new TestStructure(SingleChunkConfig::deserialize);
        TEST_STRUCTURE.setRegistryName(TEST_STRUCTURE_NAME);
        evt.getRegistry().register(TEST_STRUCTURE);

    }



    public static void biomeSetup(){
        for (Biome biome : ForgeRegistries.BIOMES.getValues()){
            biome.addStructure(TEST_STRUCTURE.withConfiguration(new SingleChunkConfig(0, 0)));
            biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES,
                    TEST_STRUCTURE.withConfiguration(new SingleChunkConfig(0, 0)));
        }
    }
}
