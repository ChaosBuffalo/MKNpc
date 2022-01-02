package com.chaosbuffalo.mknpc.capabilities;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.core.MKPlayerData;
import com.chaosbuffalo.mkcore.core.persona.IPersonaExtension;
import com.chaosbuffalo.mkcore.core.persona.IPersonaExtensionProvider;
import com.chaosbuffalo.mkcore.core.persona.Persona;
import com.chaosbuffalo.mkcore.sync.SyncMapUpdater;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionEntry;
import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.InterModComms;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerQuestDataHandler implements IPlayerQuestData {

    private PlayerEntity player;
    private MKPlayerData playerData;

    public PlayerQuestDataHandler() {
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
        }

        public void startQuest(QuestChainInstance questChain){
            PlayerQuestChainInstance quest = createNewEntry(questChain.getQuestId());
            quest.setCurrentQuest(questChain.getStartingQuestName());

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
            return tag;
        }

        @Override
        public void deserialize(CompoundNBT nbt) {


        }
    }

    private static PersonaQuestData createNewPersonaData(Persona persona){
        return new PersonaQuestData(persona);
    }


    public static void registerPersonaExtension() {
        IPersonaExtensionProvider factory = PlayerQuestDataHandler::createNewPersonaData;
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("mkcore", "register_persona_extension", () -> {
            MKNpc.LOGGER.info("MK NPC register player quest persona by IMC");
            return factory;
        });
    }
}