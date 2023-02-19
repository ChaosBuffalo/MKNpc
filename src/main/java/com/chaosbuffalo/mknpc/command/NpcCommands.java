package com.chaosbuffalo.mknpc.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;

public class NpcCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(MKSummonCommand.register());
        dispatcher.register(MKQuestCommand.register());
        dispatcher.register(MKStructureCommands.register());
    }

    public static void registerArguments() {
        ArgumentTypes.register("npc_definition_id", NpcDefinitionIdArgument.class, new ArgumentSerializer<>(NpcDefinitionIdArgument::definition));
        ArgumentTypes.register("quest_definition_id", QuestDefinitionIdArgument.class, new ArgumentSerializer<>(QuestDefinitionIdArgument::definition));
    }
}
