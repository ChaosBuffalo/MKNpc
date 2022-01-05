package com.chaosbuffalo.mknpc.mixins;


import com.chaosbuffalo.mknpc.capabilities.IChestNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.stats.Stat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixins {

    @Shadow @Nullable public abstract INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos);

    @Shadow protected abstract Stat<ResourceLocation> getOpenStat();

    /**
     * @author kovak
     * @reason showing unique quest chest inventory instead of chest inventory when relevant
     */
    @Overwrite
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            INamedContainerProvider inamedcontainerprovider = null;
            TileEntity te = worldIn.getTileEntity(pos);
            if (te != null){
                Optional<IChestNpcData> chestCap = te.getCapability(NpcCapabilities.CHEST_NPC_DATA_CAPABILITY).resolve();
                if (chestCap.isPresent() && !player.isSneaking()){
                    IChestNpcData chestData = chestCap.get();
                    if (chestData.hasQuestInventoryForPlayer(player)){
                        inamedcontainerprovider = chestData;
                    }
                }
            }
            if (inamedcontainerprovider == null){
                inamedcontainerprovider= this.getContainer(state, worldIn, pos);
            }
            if (inamedcontainerprovider != null) {
                player.openContainer(inamedcontainerprovider);
                player.addStat(this.getOpenStat());
                PiglinTasks.func_234478_a_(player, true);
            }

            return ActionResultType.CONSUME;
        }
    }
}
