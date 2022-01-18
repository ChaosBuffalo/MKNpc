package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NpcCapabilities {
    public static ResourceLocation MK_NPC_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "npc_data");
    public static ResourceLocation MK_WORLD_NPC_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "world_npc_data");
    public static ResourceLocation MK_CHUNK_NPC_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "chunk_npc_data");
    public static ResourceLocation MK_CHEST_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "chest_npc_data");
    public static ResourceLocation MK_QUEST_CAP_ID = new ResourceLocation(MKNpc.MODID,
            "player_quest_data");

    @CapabilityInject(IEntityNpcData.class)
    public static final Capability<IEntityNpcData> ENTITY_NPC_DATA_CAPABILITY;

    @CapabilityInject(IWorldNpcData.class)
    public static final Capability<IWorldNpcData> WORLD_NPC_DATA_CAPABILITY;

    @CapabilityInject(IChunkNpcData.class)
    public static final Capability<IChunkNpcData> CHUNK_NPC_DATA_CAPABILITY;

    @CapabilityInject(IChestNpcData.class)
    public static final Capability<IChestNpcData> CHEST_NPC_DATA_CAPABILITY;

    @CapabilityInject(IPlayerQuestingData.class)
    public static final Capability<IPlayerQuestingData> PLAYER_QUEST_DATA_CAPABILITY;


    static {
        ENTITY_NPC_DATA_CAPABILITY = null;
        WORLD_NPC_DATA_CAPABILITY = null;
        CHUNK_NPC_DATA_CAPABILITY = null;
        CHEST_NPC_DATA_CAPABILITY = null;
        PLAYER_QUEST_DATA_CAPABILITY = null;
    }

    public static void registerCapabilities() {
        CoreCapabilities.registerLivingEntity(e -> e instanceof MKEntity);
        CapabilityManager.INSTANCE.register(IEntityNpcData.class, new NBTStorage<>(),
                EntityNpcDataHandler::new);
        CapabilityManager.INSTANCE.register(IWorldNpcData.class, new NBTStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IChunkNpcData.class, new NBTStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IChestNpcData.class, new NBTStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IPlayerQuestingData.class, new NBTStorage<>(),
                PlayerQuestingDataHandler::new);
    }

    public abstract static class Provider<CapTarget, CapType extends INBTSerializable<CompoundNBT>> implements ICapabilitySerializable<CompoundNBT> {

        private final CapType data;
        private final LazyOptional<CapType> capOpt;

        public Provider(CapTarget chunk) {
            data = makeData(chunk);
            capOpt = LazyOptional.of(() -> data);
        }

        abstract CapType makeData(CapTarget attached);

        abstract Capability<CapType> getCapability();

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return getCapability().orEmpty(cap, capOpt);
        }

        public void invalidate() {
            capOpt.invalidate();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return data.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            data.deserializeNBT(nbt);
        }
    }

    public static class NBTStorage<T extends INBTSerializable<CompoundNBT>> implements Capability.IStorage<T> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            if (instance == null) {
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
