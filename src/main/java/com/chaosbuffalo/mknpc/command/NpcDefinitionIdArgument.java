package com.chaosbuffalo.mknpc.command;

import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mknpc.npc.NpcDefinition;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;

import java.util.concurrent.CompletableFuture;

public class NpcDefinitionIdArgument  implements ArgumentType<ResourceLocation> {
    public NpcDefinitionIdArgument() {
    }

    public static NpcDefinitionIdArgument definition() {
        return new NpcDefinitionIdArgument();
    }

    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return ResourceLocation.read(reader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(NpcDefinitionManager.DEFINITIONS.keySet().stream()
                .map(ResourceLocation::toString), builder);
    }
}