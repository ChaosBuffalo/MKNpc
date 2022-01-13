package com.chaosbuffalo.mknpc.inventories;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IPlayerQuestingData;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.objectives.ITradeObjectiveHandler;
import com.chaosbuffalo.mknpc.quest.objectives.QuestObjective;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class QuestGiverInventoryContainer extends ChestContainer {
    private final MKEntity entity;

    public QuestGiverInventoryContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn,
                                        IInventory p_i50092_4_, int rows, MKEntity entity) {
        super(type, id, playerInventoryIn, p_i50092_4_, rows);
        this.entity = entity;
    }

    public static QuestGiverInventoryContainer createGeneric9X1(int id, PlayerInventory player, MKEntity entity) {
        return new QuestGiverInventoryContainer(ContainerType.GENERIC_9X1, id, player, new Inventory(9), 1, entity);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        IInventory inventory = getLowerChestInventory();
        List<ItemStack> nonEmpty = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                nonEmpty.add(stack);
            }
            inventory.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        if (nonEmpty.isEmpty()){
            return;
        }
        Optional<? extends IPlayerQuestingData> playerQuestOpt = MKNpc.getPlayerQuestData(playerIn).resolve();
        MinecraftServer server = playerIn.getServer();
        if (server != null){
            World overWorld = server.getWorld(World.OVERWORLD);
            if (overWorld != null){
                Optional<? extends IWorldNpcData> worldDataOpt = overWorld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY).resolve();
                if (worldDataOpt.isPresent()){
                    IWorldNpcData worldData = worldDataOpt.get();;
                    if (playerQuestOpt.isPresent()){
                        IPlayerQuestingData playerQuest = playerQuestOpt.get();
                        Collection<PlayerQuestChainInstance> chains = playerQuest.getQuestChains();
                        for (PlayerQuestChainInstance chain : chains){
                            QuestChainInstance questChain = worldData.getQuest(chain.getQuestId());
                            if (questChain == null) {
                                continue;
                            }
                            for (String questName : chain.getCurrentQuests()){
                                Quest currentQuest = questChain.getDefinition().getQuest(questName);
                                if (currentQuest != null) {
                                    for (QuestObjective<?> obj : currentQuest.getObjectives()){
                                        if (obj instanceof ITradeObjectiveHandler){
                                            int[] matches = ((ITradeObjectiveHandler) obj).findMatches(nonEmpty);
                                            if (matches == null){
                                                continue;
                                            } else {
                                                ((ITradeObjectiveHandler) obj).onPlayerTradeSuccess(playerIn,
                                                        chain.getQuestData(currentQuest.getQuestName())
                                                                .getObjective(obj.getObjectiveName()),
                                                        questChain.getQuestChainData().getQuestData(questName), chain, entity);
                                                questChain.signalQuestProgress(worldData, playerQuest, currentQuest, chain, false);
                                                return;
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for (ItemStack is : nonEmpty){
            StringTextComponent name = new StringTextComponent(String.format("<%s>", entity.getDisplayName().getString()));
            playerIn.sendMessage(new TranslationTextComponent("mknpc.quest.trade.dont_need", name,
                    playerIn.getName(), is.getCount(), is.getDisplayName()), Util.DUMMY_UUID);
            playerIn.inventory.placeItemBackInInventory(playerIn.getEntityWorld(), is);
        }
    }

}
