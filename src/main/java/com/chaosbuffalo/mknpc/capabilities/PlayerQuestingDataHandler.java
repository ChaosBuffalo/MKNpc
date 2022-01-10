package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.core.MKPlayerData;
import com.chaosbuffalo.mkcore.core.persona.IPersonaExtension;
import com.chaosbuffalo.mkcore.core.persona.IPersonaExtensionProvider;
import com.chaosbuffalo.mkcore.core.persona.Persona;
import com.chaosbuffalo.mkcore.sync.SyncMapUpdater;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.quest.Quest;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.InterModComms;

import javax.annotation.Nullable;
import java.util.*;

public class PlayerQuestingDataHandler implements IPlayerQuestingData {

    private PlayerEntity player;
    private MKPlayerData playerData;

    public PlayerQuestingDataHandler() {
    }

    public void attach(PlayerEntity player) {
        // Do not attempt to access any persona-specific data here because at this time
        // it's impossible to get a copy of MKPlayerData
        this.player = player;
    }

    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    private MKPlayerData getPlayerData() {
        if (playerData == null) {
            playerData = MKCore.getPlayer(player).orElseThrow(IllegalStateException::new);
        }
        return playerData;
    }

    public void advanceQuest(IWorldNpcData worldHandler, QuestChainInstance questChainInstance){
        getPersonaData().advanceQuestChain(worldHandler, questChainInstance, this);
    }

    @Override
    public void questProgression(IWorldNpcData worldHandler, QuestChainInstance questChainInstance) {
        getPersonaData().questProgression(worldHandler, questChainInstance);
    }


    public Collection<PlayerQuestChainInstance> getQuestChains(){
        return getPersonaData().questChains.values();
    }

    @Override
    public Optional<PlayerQuestChainInstance> getQuestChain(UUID questId) {
        return getPersonaData().getChain(questId);
    }

    @Override
    public void startQuest(IWorldNpcData worldHandler, UUID questId) {
        QuestChainInstance chain = worldHandler.getQuest(questId);
        if (chain != null){
            MKNpc.LOGGER.info("Player {} started quest {}", getPlayer(), chain);
            getPersonaData().startQuest(worldHandler, chain);
        } else {
            MKNpc.LOGGER.warn("Tried to start quest with id {} but it doesn't exist in the world data", questId);
        }

    }

    @Override
    public boolean isOnQuest(UUID questId) {
        return getPersonaData().isOnQuest(questId);
    }

    @Override
    public Optional<String> getCurrentQuestStep(UUID questId) {
        return getPersonaData().getCurrentQuestStep(questId);
    }

    private PersonaQuestData getPersonaData(){
        return getPlayerData().getPersonaExtension(PersonaQuestData.class);
    }

    @Override
    public CompoundNBT serializeNBT() {
        // This would be where global data that is shared across personas would be persisted.
        // Currently there is none.
        CompoundNBT tag = new CompoundNBT();
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
    }

    public static class PersonaQuestData implements IPersonaExtension {
        final static ResourceLocation NAME = new ResourceLocation(MKNpc.MODID, "player_quest_data");
        private final Map<UUID, PlayerQuestChainInstance> questChains = new HashMap<>();
        private final SyncMapUpdater<UUID, PlayerQuestChainInstance> questChainUpdater;
        private final Set<UUID> completedQuests = new HashSet<>();

        private final Persona persona;

        public PersonaQuestData(Persona persona) {
            this.persona = persona;
            questChainUpdater = new SyncMapUpdater<>(
                    "questChains",
                    () -> questChains,
                    UUID::toString,
                    UUID::fromString,
                    this::createNewEntry
            );
            persona.getKnowledge().addSyncPrivate(questChainUpdater);
        }

        public Optional<PlayerQuestChainInstance> getChain(UUID questId){
            PlayerQuestChainInstance questChain = questChains.get(questId);
            if (questChain == null){
                return Optional.empty();
            }
            return Optional.of(questChain);
        }

        public Optional<String> getCurrentQuestStep(UUID questId){
            PlayerQuestChainInstance questChain = questChains.get(questId);
            if (questChain == null){
                return Optional.empty();
            }
            return Optional.of(questChain.getCurrentQuest());
        }

        public boolean isOnQuest(UUID questId){
            PlayerQuestChainInstance questChain = questChains.get(questId);
            if (questChain == null){
                return false;
            }
            return !questChain.isQuestComplete();
        }

        public void startQuest(IWorldNpcData worldHandler, QuestChainInstance questChain){
            if (!questChain.getDefinition().isRepeatable() && completedQuests.contains(questChain.getQuestId())){
                MKNpc.LOGGER.info("Can't start quest with definition {} for {} already completed {}",
                        questChain.getDefinition().getName(), persona.getPlayerData().getEntity(), questChain.getQuestId());
                return;
            }
            PlayerQuestChainInstance quest = createNewEntry(questChain.getQuestId());
            quest.setupQuestChain(questChain);
            quest.setCurrentQuest(questChain.getStartingQuestName());
            PlayerQuestData questData = questChain.getDefinition().getFirstQuest().generatePlayerQuestData(
                    worldHandler, questChain.getQuestChainData().getQuestData(quest.getCurrentQuest()));
            quest.addQuestData(questData);
            questChains.put(questChain.getQuestId(), quest);
            questChainUpdater.markDirty(questChain.getQuestId());
        }

        public void questProgression(IWorldNpcData worldHandler, QuestChainInstance questChainInstance){
            questChainUpdater.markDirty(questChainInstance.getQuestId());
        }

        public void advanceQuestChain(IWorldNpcData worldHandler, QuestChainInstance questChainInstance, IPlayerQuestingData questingData){
            PlayerQuestChainInstance chain = questChains.get(questChainInstance.getQuestId());
            if (chain != null){
                String currentQuestName = chain.getCurrentQuest();
                Quest currentQuest = questChainInstance.getDefinition().getQuest(currentQuestName);
                if (currentQuest != null){
                    currentQuest.grantRewards(questingData);
                }
                Optional<Quest> nextQuest = questChainInstance.getNextQuest(currentQuestName);
                if (nextQuest.isPresent()){
                    Quest quest = nextQuest.get();
                    chain.addQuestData(quest.generatePlayerQuestData(worldHandler,
                            questChainInstance.getQuestChainData().getQuestData(quest.getQuestName())));
                    chain.setCurrentQuest(quest.getQuestName());
                } else {
                    chain.setQuestComplete(true);
                    completedQuests.add(chain.getQuestId());
                }
                questChainUpdater.markDirty(chain.getQuestId());
            }
        }

        private void onDirtyEntry(PlayerQuestChainInstance entry) {
            questChainUpdater.markDirty(entry.getQuestId());
        }

        private PlayerQuestChainInstance createNewEntry(UUID id) {
            PlayerQuestChainInstance entry = new PlayerQuestChainInstance(id);
            entry.setDirtyNotifier(this::onDirtyEntry);
            return entry;
        }

        @Override
        public ResourceLocation getName() {
            return NAME;
        }

        @Override
        public void onPersonaActivated() {

        }

        @Override
        public void onPersonaDeactivated() {

        }

        @Override
        public CompoundNBT serialize() {
            CompoundNBT tag = new CompoundNBT();
            ListNBT chainsNbt = new ListNBT();
            for (PlayerQuestChainInstance chain : questChains.values()){
                chainsNbt.add(chain.serialize());
            }
            tag.put("chains", chainsNbt);
            return tag;
        }

        @Override
        public void deserialize(CompoundNBT nbt) {
            ListNBT chainsNbt = nbt.getList("chains", Constants.NBT.TAG_COMPOUND);
            for (INBT chainNbt : chainsNbt){
                PlayerQuestChainInstance newChain = new PlayerQuestChainInstance((CompoundNBT) chainNbt);
                newChain.setDirtyNotifier(this::onDirtyEntry);
                questChains.put(newChain.getQuestId(), newChain);
                if (newChain.isQuestComplete()){
                    completedQuests.add(newChain.getQuestId());
                }
            }
        }
    }

    private static PersonaQuestData createNewPersonaData(Persona persona){
        return new PersonaQuestData(persona);
    }


    public static void registerPersonaExtension() {
        IPersonaExtensionProvider factory = PlayerQuestingDataHandler::createNewPersonaData;
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("mkcore", "register_persona_extension", () -> {
            MKNpc.LOGGER.info("MK NPC register player quest persona by IMC");
            return factory;
        });
    }

    public static class Storage implements Capability.IStorage<IPlayerQuestingData> {


        @Nullable
        @Override
        public INBT writeNBT(Capability<IPlayerQuestingData> capability, IPlayerQuestingData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IPlayerQuestingData> capability, IPlayerQuestingData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}