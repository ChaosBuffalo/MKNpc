package com.chaosbuffalo.mknpc.mixins;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.IAdditionalStartData;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

@Mixin(Structure.class)
public abstract class StructureMixins {

    @Shadow @Final private static Logger LOGGER;

    @Shadow @Final private static Map<ResourceLocation, ResourceLocation> OLD_TO_NEW_NAMING_MAP;

    /**
     * @author kovak
     * @reason starts cant have additional data without this
     */
    @Overwrite
    @Nullable
    public static StructureStart<?> deserializeStructureStart(TemplateManager manager, CompoundNBT nbt, long seed){
        String s = nbt.getString("id");
        if ("INVALID".equals(s)) {
            return StructureStart.DUMMY;
        } else {
            Structure<?> structure = Registry.STRUCTURE_FEATURE.getOrDefault(new ResourceLocation(s.toLowerCase(Locale.ROOT)));
            if (structure == null) {
                LOGGER.error("Unknown feature id: {}", (Object)s);
                return null;
            } else {
                int i = nbt.getInt("ChunkX");
                int j = nbt.getInt("ChunkZ");
                int k = nbt.getInt("references");
                MutableBoundingBox mutableboundingbox = nbt.contains("BB") ? new MutableBoundingBox(nbt.getIntArray("BB")) : MutableBoundingBox.getNewBoundingBox();
                ListNBT listnbt = nbt.getList("Children", 10);

                try {
                    StructureStart<?> structurestart = structure.createStructureStart(i, j, mutableboundingbox, k, seed);
                    if (structurestart instanceof IAdditionalStartData){
                        ((IAdditionalStartData) structurestart).readAdditional(nbt);
                    }

                    for(int l = 0; l < listnbt.size(); ++l) {
                        CompoundNBT compoundnbt = listnbt.getCompound(l);
                        String s1 = compoundnbt.getString("id").toLowerCase(Locale.ROOT);
                        ResourceLocation resourcelocation = new ResourceLocation(s1);
                        ResourceLocation resourcelocation1 = OLD_TO_NEW_NAMING_MAP.getOrDefault(resourcelocation, resourcelocation);
                        IStructurePieceType istructurepiecetype = Registry.STRUCTURE_PIECE.getOrDefault(resourcelocation1);
                        if (istructurepiecetype == null) {
                            LOGGER.error("Unknown structure piece id: {}", (Object)resourcelocation1);
                        } else {
                            try {
                                StructurePiece structurepiece = istructurepiecetype.load(manager, compoundnbt);
                                structurestart.getComponents().add(structurepiece);
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
