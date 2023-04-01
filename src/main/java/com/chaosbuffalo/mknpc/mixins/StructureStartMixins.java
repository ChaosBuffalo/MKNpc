package com.chaosbuffalo.mknpc.mixins;


import com.chaosbuffalo.mknpc.world.gen.IStructureStartMixin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.UUID;

@Mixin(StructureStart.class)
public abstract class StructureStartMixins implements IStructureStartMixin {

    protected UUID instanceId;

    @Inject(method = "Lnet/minecraft/world/level/levelgen/structure/StructureStart;<init>(Lnet/minecraft/world/level/levelgen/feature/ConfiguredStructureFeature;Lnet/minecraft/world/level/ChunkPos;ILnet/minecraft/world/level/levelgen/structure/pieces/PiecesContainer;)V",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    protected void proxyInit(ConfiguredStructureFeature<?, ?> feature, ChunkPos chunkPos, int references,
                          PiecesContainer piecesContainer, CallbackInfo ci) {
        instanceId = UUID.randomUUID();
    }

    @Inject(method = "Lnet/minecraft/world/level/levelgen/structure/StructureStart;createTag(Lnet/minecraft/world/level/levelgen/structure/pieces/StructurePieceSerializationContext;Lnet/minecraft/world/level/ChunkPos;)Lnet/minecraft/nbt/CompoundTag;",
            at = @At(target = "Lnet/minecraft/nbt/CompoundTag;put(Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", value = "INVOKE"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void createTag(StructurePieceSerializationContext p_192661_, ChunkPos p_192662_, CallbackInfoReturnable<CompoundTag> cir, CompoundTag compoundtag) {
        compoundtag.putUUID("instanceId", instanceId);
    }

    @Override
    public UUID getInstanceId() {
        return instanceId;
    }

    @Override
    public void loadAdditional(CompoundTag tag) {
        instanceId = tag.getUUID("instanceId");
    }
}
