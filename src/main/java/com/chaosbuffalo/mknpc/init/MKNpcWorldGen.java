package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKNpcWorldGen {

    public static IStructurePieceType TEST_PIECE_TYPE;
    public static IStructurePieceType TEST_JIGSAW_PIECE_TYPE;
    public static TestStructure TEST_STRUCTURE;
    public static final ResourceLocation TEST_STRUCTURE_NAME = new ResourceLocation(MKNpc.MODID, "test");
    private static StructureFeature<?, ?> TEST_STRUCTURE_FEATURE;

    public static MKJigsawStructure TEST_JIGSAW;
    public static ResourceLocation TEST_JIG_SAW_NAME = new ResourceLocation(MKNpc.MODID, "test_jigsaw");
    private static StructureFeature<?, ?> TEST_JIGSAW_FEATURE;

    public static void registerStructurePieces(){
        TEST_PIECE_TYPE = Registry.register(Registry.STRUCTURE_PIECE, TEST_STRUCTURE_NAME.toString(),
                TestStructurePieces.Piece::new);
    }

    @SubscribeEvent
    public static void registerStructure(RegistryEvent.Register<Structure<?>> evt){
        TEST_STRUCTURE = new TestStructure(ChunkPosConfig.CODEC);
        TEST_STRUCTURE.setRegistryName(TEST_STRUCTURE_NAME);
        TEST_STRUCTURE_FEATURE = TEST_STRUCTURE.withConfiguration(new ChunkPosConfig(0, 0));
        Structure.NAME_STRUCTURE_BIMAP.put(TEST_STRUCTURE_NAME.toString(), TEST_STRUCTURE);
        Structure.STRUCTURE_DECORATION_STAGE_MAP.put(TEST_STRUCTURE, GenerationStage.Decoration.SURFACE_STRUCTURES);
        evt.getRegistry().register(TEST_STRUCTURE);

        TEST_JIGSAW = new MKJigsawStructure(VillageConfig.field_236533_a_, 0, true, true);
        TEST_JIGSAW.setRegistryName(TEST_JIG_SAW_NAME);
        Structure.NAME_STRUCTURE_BIMAP.put(TEST_JIG_SAW_NAME.toString(), TEST_JIGSAW);
        Structure.STRUCTURE_DECORATION_STAGE_MAP.put(TEST_JIGSAW, GenerationStage.Decoration.SURFACE_STRUCTURES);
        TEST_JIGSAW_FEATURE = TEST_JIGSAW.withConfiguration(new VillageConfig(
                () -> TestJigsawStructurePools.BASE_PATTERN, TestJigsawStructurePools.GEN_DEPTH));
        evt.getRegistry().register(TEST_JIGSAW);

    }

    public static void worldSetup(FMLServerAboutToStartEvent event){
        event.getServer().func_244267_aX().getRegistry(Registry.NOISE_SETTINGS_KEY).forEach(dimensionSettings -> {
            dimensionSettings.getStructures().func_236195_a_().put(TEST_STRUCTURE, new StructureSeparationSettings(2, 1, 34222645));
            dimensionSettings.getStructures().func_236195_a_().put(TEST_JIGSAW, new StructureSeparationSettings(10, 5, 32441244));
        });
    }

    public static void biomeSetup(BiomeLoadingEvent event){
        event.getGeneration().withStructure(TEST_STRUCTURE_FEATURE);
        event.getGeneration().withStructure(TEST_JIGSAW_FEATURE);
    }
}
