package com.chaosbuffalo.mknpc.world.gen;

import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.init.MKNpcBlocks;
import com.chaosbuffalo.mknpc.tile_entities.MKPoiTileEntity;
import com.chaosbuffalo.mknpc.tile_entities.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.IControlNaturalSpawns;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.MKJigsawStructure;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class StructureUtils {

    public static BlockPos getCorrectionForEvenRotation(Rotation rotation){
        switch (rotation){
            case CLOCKWISE_90:
                return new BlockPos(-1, 0, 0);
            case COUNTERCLOCKWISE_90:
                return new BlockPos(0, 0, -1);
            case CLOCKWISE_180:
                return new BlockPos(-1, 0, -1);
            case NONE:
            default:
                return new BlockPos(0, 0, 0);
        }
    }

    public static void handleMKDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb,
                                          ResourceLocation structureName, UUID instanceId)
    {
        if (function.equals("mkspawner")) {
            TileEntity tileentity = worldIn.getTileEntity(pos.down());
            if (tileentity instanceof MKSpawnerTileEntity) {
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                MKSpawnerTileEntity spawner = (MKSpawnerTileEntity) tileentity;
                spawner.regenerateSpawnID();
                spawner.setStructureName(structureName);
                spawner.setStructureId(instanceId);
            }
        } else if (function.startsWith("mkcontainer")){
            String[] names = function.split("#", 2);
            String labels = names[1];
            TileEntity tileEntity = worldIn.getTileEntity(pos.down());
            if (tileEntity instanceof ChestTileEntity){
                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                tileEntity.getCapability(NpcCapabilities.CHEST_NPC_DATA_CAPABILITY).ifPresent(x ->{
                    x.setStructureId(instanceId);
                    x.setStructureName(structureName);
                    x.generateChestId(labels);
                });
            }
        } else if (function.startsWith("mkpoi")) {
            String[] names = function.split("#", 2);
            String tag = names[1];
            worldIn.setBlockState(pos, MKNpcBlocks.MK_POI_BLOCK.get().getDefaultState(), 3);
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof MKPoiTileEntity) {
                MKPoiTileEntity poi = (MKPoiTileEntity) tile;
                poi.regenerateId();
                poi.setStructureId(instanceId);
                poi.setStructureName(structureName);
                poi.setPoiTag(tag);
            }
        }
    }

    public static Optional<List<MKJigsawStructure.Start>> getStructuresOverlaps(Entity entity) {
        if (entity.getEntityWorld() instanceof ServerWorld){
            StructureManager manager = ((ServerWorld) entity.getEntityWorld()).getStructureManager();
            return Optional.of(ForgeRegistries.STRUCTURE_FEATURES.getValues().stream().filter(x -> x instanceof MKJigsawStructure).map(
                    x -> manager.getStructureStart(entity.getPosition(), false, x)).filter(x -> x != StructureStart.DUMMY)
                    .map(x -> (MKJigsawStructure.Start) x)
                    .collect(Collectors.toList()));
        } else {
            return Optional.empty();
        }
    }
}
