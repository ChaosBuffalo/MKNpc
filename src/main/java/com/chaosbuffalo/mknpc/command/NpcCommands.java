package com.chaosbuffalo.mknpc.command;

import com.chaosbuffalo.mkcore.command.HotBarCommand;
import com.chaosbuffalo.mkcore.command.arguments.AbilityIdArgument;
import com.chaosbuffalo.mkcore.command.arguments.TalentLineIdArgument;
import com.chaosbuffalo.mkcore.command.arguments.TalentTreeIdArgument;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;

public class NpcCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(MKSummonCommand.register());
    }

    public static void registerArguments() {
        ArgumentTypes.register("npc_definition_id", NpcDefinitionIdArgument.class, new ArgumentSerializer<>(NpcDefinitionIdArgument::definition));
    }
}
