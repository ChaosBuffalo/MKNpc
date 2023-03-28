package com.chaosbuffalo.mknpc.mixins;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.IAdditionalStartData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

@Mixin(StructureFeature.class)
public abstract class StructureMixins {

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private static Map<ResourceLocation, ResourceLocation> RENAMES;


    /**
     * @author kovak
     * @reason starts cant have additional data without this
     */
    @Overwrite
    @Nullable
    public static StructureStart<?> loadStaticStart(ServerLevel p_160448_, CompoundTag p_160449_, long p_160450_) {
        String s = p_160449_.getString("id");
        if ("INVALID".equals(s)) {
            return StructureStart.INVALID_START;
        } else {
            StructureFeature<?> structurefeature = Registry.STRUCTURE_FEATURE.get(new ResourceLocation(s.toLowerCase(Locale.ROOT)));
            if (structurefeature == null) {
                LOGGER.error("Unknown feature id: {}", (Object)s);
                return null;
            } else {
                ChunkPos chunkpos = new ChunkPos(p_160449_.getInt("ChunkX"), p_160449_.getInt("ChunkZ"));
                int i = p_160449_.getInt("references");
                ListTag listtag = p_160449_.getList("Children", 10);

                try {
                    StructureStart<?> structurestart = structurefeature.createStart(chunkpos, i, p_160450_);
                    if (structurestart instanceof IAdditionalStartData){
                        ((IAdditionalStartData) structurestart).readAdditional(p_160449_);
                    }

                    for(int j = 0; j < listtag.size(); ++j) {
                        CompoundTag compoundtag = listtag.getCompound(j);
                        String s1 = compoundtag.getString("id").toLowerCase(Locale.ROOT);
                        ResourceLocation resourcelocation = new ResourceLocation(s1);
                        ResourceLocation resourcelocation1 = RENAMES.getOrDefault(resourcelocation, resourcelocation);
                        StructurePieceType structurepiecetype = Registry.STRUCTURE_PIECE.get(resourcelocation1);
                        if (structurepiecetype == null) {
                            LOGGER.error("Unknown structure piece id: {}", (Object)resourcelocation1);
                        } else {
                            try {
                                StructurePiece structurepiece = structurepiecetype.load(p_160448_, compoundtag);
                                structurestart.addPiece(structurepiece);
                            } catch (Exception exception) {
                                LOGGER.error("Exception loading structure piece with id {}", resourcelocation1, exception);
                            }
                        }
                    }

                    return structurestart;
                } catch (Exception exception1) {
                    LOGGER.error("Failed Start with id {}", s, exception1);
                    return null;
                }
            }
        }
    }
}

