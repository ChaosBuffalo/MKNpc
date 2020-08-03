package com.chaosbuffalo.mknpc.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public class NpcCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(MKSummonCommand.register());
    }
}
