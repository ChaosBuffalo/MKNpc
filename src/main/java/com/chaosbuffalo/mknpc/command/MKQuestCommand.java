package com.chaosbuffalo.mknpc.command;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.quest.QuestChainInstance;
import com.chaosbuffalo.mknpc.quest.QuestDefinition;
import com.chaosbuffalo.mknpc.quest.QuestDefinitionManager;
import com.chaosbuffalo.mknpc.quest.generation.QuestChainBuildResult;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MKQuestCommand {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("mkquest")
                .then(Commands.literal("gen")
                        .then(Commands.argument("quest", QuestDefinitionIdArgument.definition())
                        .suggests(MKQuestCommand::suggestQuestDefinitions)
                        .executes(MKQuestCommand::generateQuest)))
                .then(Commands.literal("start")
                    .then(Commands.argument("id", StringArgumentType.string())
                    .executes(MKQuestCommand::startQuest)));
    }

    static CompletableFuture<Suggestions> suggestQuestDefinitions(final CommandContext<CommandSource> context,
                                                                  final SuggestionsBuilder builder) throws CommandSyntaxException {
        return ISuggestionProvider.suggest(QuestDefinitionManager.DEFINITIONS.keySet().stream()
                .map(ResourceLocation::toString), builder);
    }

    static int startQuest(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().asPlayer();
        String questIdStr = StringArgumentType.getString(ctx, "id");
        UUID questId = UUID.fromString(questIdStr);
        MinecraftServer server = player.getServer();
        if (server != null){
            World world = server.getWorld(World.OVERWORLD);
            if (world != null) {
                LazyOptional<IWorldNpcData> worldL = world.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY);
                Optional<IWorldNpcData> wrldOpt = worldL.resolve();
                if (wrldOpt.isPresent()){
                    IWorldNpcData worldData = wrldOpt.get();
                    MKNpc.getPlayerQuestData(player).ifPresent(x -> {
                        x.startQuest(worldData, questId);
                    });

                }


            }
        }

        return Command.SINGLE_SUCCESS;
    }

    static int generateQuest(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().asPlayer();
        ResourceLocation definition_id = ctx.getArgument("quest", ResourceLocation.class);
        QuestDefinition definition = QuestDefinitionManager.getDefinition(definition_id);
        BlockPos pos = new BlockPos(player.getPositionVec());
        if (definition != null){
            MinecraftServer server = player.getServer();
            if (server != null){
                World world = server.getWorld(World.OVERWORLD);
                if (world != null){
                    Optional<QuestChainBuildResult> quest = world.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY)
                            .map(x -> x.buildQuest(definition, pos)).orElse(Optional.empty());
                    if (quest.isPresent()){
                        QuestChainInstance newQuest = quest.get().instance;
                        player.sendMessage(new StringTextComponent(String.format("Generated quest: %s", newQuest.getQuestId().toString())), Util.DUMMY_UUID);
                        return Command.SINGLE_SUCCESS;
                    }
                }
            }
            player.sendMessage(new StringTextComponent("Failed to generate quest"), Util.DUMMY_UUID);
        } else {
            player.sendMessage(new StringTextComponent("Definition not found."), Util.DUMMY_UUID);
        }
        return Command.SINGLE_SUCCESS;
    }
}
