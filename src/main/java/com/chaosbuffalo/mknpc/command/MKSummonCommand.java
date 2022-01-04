package com.chaosbuffalo.mknpc.command;

import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.util.concurrent.CompletableFuture;

public class MKSummonCommand {

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("mksummon")
                .then(Commands.argument("npc_definition", NpcDefinitionIdArgument.definition())
                        .suggests(MKSummonCommand::suggestNpcDefinitions)
                        .executes(MKSummonCommand::summon));
    }

    static CompletableFuture<Suggestions> suggestNpcDefinitions(final CommandContext<CommandSource> context,
                                                                final SuggestionsBuilder builder) throws CommandSyntaxException {
        return ISuggestionProvider.suggest(NpcDefinitionManager.DEFINITIONS.keySet().stream()
                .map(ResourceLocation::toString), builder);
    }

    static int summon(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().asPlayer();
        ResourceLocation definition_id = ctx.getArgument("npc_definition", ResourceLocation.class);
        NpcDefinition definition = NpcDefinitionManager.getDefinition(definition_id);
        if (definition != null){
            Entity entity = definition.createEntity(player.getServerWorld(), player.getPositionVec());
            if (entity != null){
                player.getServerWorld().addEntity(entity);
                if (entity instanceof MobEntity){
                    ((MobEntity) entity).onInitialSpawn(player.getServerWorld(), player.getServerWorld().getDifficultyForLocation(
                            new BlockPos(entity.getPositionVec())), SpawnReason.COMMAND, null, null);
                }
            } else {
                player.sendMessage(new StringTextComponent(String.format("Failed to summon: %s",
                        definition_id.toString())), Util.DUMMY_UUID);
            }
        } else {
            player.sendMessage(new StringTextComponent("Definition not found."), Util.DUMMY_UUID);
        }
        return Command.SINGLE_SUCCESS;
    }
}
