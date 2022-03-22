package com.chaosbuffalo.mknpc.init;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.world.gen.feature.MKSpringFeature;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MKNpc.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKNpcWorldGen {

    public static final ResourceLocation UNKNOWN_PIECE = new ResourceLocation(MKNpc.MODID, "unknown_structure_piece");
    public static IStructurePieceType TEST_PIECE_TYPE;
    public static IStructurePieceType TEST_JIGSAW_PIECE_TYPE;
    public static TestStructure TEST_STRUCTURE;
    public static MKSpringFeature SPRING_REPLACEMENT;
    public static ConfiguredFeature<?, ?> SPRING_WATER_REPLACEMENT;
    public static final ResourceLocation TEST_STRUCTURE_NAME = new ResourceLocation(MKNpc.MODID, "test");
    private static StructureFeature<?, ?> TEST_STRUCTURE_FEATURE;

    public static MKJigsawStructure TEST_JIGSAW;
    public static ResourceLocation TEST_JIG_SAW_NAME = new ResourceLocation(MKNpc.MODID, "test_jigsaw");
    private static StructureFeature<?, ?> TEST_JIGSAW_FEATURE;
    private static List<Structure<?>> NO_WATER_STRUCTURES = new ArrayList<>();
    public static IStructurePieceType MK_JIGSAW_PIECE_TYPE;

    public static void registerStructurePieces(){
        TEST_PIECE_TYPE = Registry.register(Registry.STRUCTURE_PIECE, TEST_STRUCTURE_NAME.toString(),
                TestStructurePieces.Piece::new);
        MK_JIGSAW_PIECE_TYPE = Registry.register(Registry.STRUCTURE_PIECE, "mk_jigsaw",
                MKAbstractJigsawPiece::new);
    }

    @SubscribeEvent
    public static void registerStructure(RegistryEvent.Register<Structure<?>> evt){
        TEST_STRUCTURE = new TestStructure(ChunkPosConfig.CODEC);
        TEST_STRUCTURE.setRegistryName(TEST_STRUCTURE_NAME);
        TEST_STRUCTURE_FEATURE = TEST_STRUCTURE.withConfiguration(new ChunkPosConfig(0, 0));
        Structure.NAME_STRUCTURE_BIMAP.put(TEST_STRUCTURE_NAME.toString(), TEST_STRUCTURE);
        Structure.STRUCTURE_DECORATION_STAGE_MAP.put(TEST_STRUCTURE, GenerationStage.Decoration.SURFACE_STRUCTURES);
        evt.getRegistry().register(TEST_STRUCTURE);

        TEST_JIGSAW = new MKJigsawStructure(VillageConfig.field_236533_a_, 0, true, true, false);
        TEST_JIGSAW.setRegistryName(TEST_JIG_SAW_NAME);
        Structure.NAME_STRUCTURE_BIMAP.put(TEST_JIG_SAW_NAME.toString(), TEST_JIGSAW);
        Structure.STRUCTURE_DECORATION_STAGE_MAP.put(TEST_JIGSAW, GenerationStage.Decoration.SURFACE_STRUCTURES);
        TEST_JIGSAW_FEATURE = TEST_JIGSAW.withConfiguration(new VillageConfig(
                () -> TestJigsawStructurePools.BASE_PATTERN, TestJigsawStructurePools.GEN_DEPTH));
        evt.getRegistry().register(TEST_JIGSAW);

    }

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> evt){
        SPRING_REPLACEMENT = new MKSpringFeature(LiquidsConfig.CODEC);
        SPRING_REPLACEMENT.setRegistryName(MKNpc.MODID, "spring_feature");
        evt.getRegistry().register(SPRING_REPLACEMENT);
    }

    public static void addNoWaterStructure(Structure<?> structure) {
        if (structure != null) {
            NO_WATER_STRUCTURES.add(structure);
        }
    }

    public static List<Structure<?>> getNoWaterStructures() {
        return NO_WATER_STRUCTURES;
    }

    public static void worldSetup(FMLServerAboutToStartEvent event){
//        event.getServer().func_244267_aX().getRegistry(Registry.NOISE_SETTINGS_KEY).forEach(dimensionSettings -> {
//            dimensionSettings.getStructures().func_236195_a_().put(TEST_STRUCTURE, new StructureSeparationSettings(2, 1, 34222645));
//            dimensionSettings.getStructures().func_236195_a_().put(TEST_JIGSAW, new StructureSeparationSettings(10, 5, 32441244));
//        });
    }

    public static void biomeSetup(BiomeLoadingEvent event){
//        event.getGeneration().withStructure(TEST_STRUCTURE_FEATURE);
//        event.getGeneration().withStructure(TEST_JIGSAW_FEATURE);
    }
}
