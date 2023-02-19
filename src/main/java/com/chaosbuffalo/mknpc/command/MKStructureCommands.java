package com.chaosbuffalo.mknpc.command;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.world.gen.StructureUtils;
import com.chaosbuffalo.mknpc.world.gen.feature.structure.MKJigsawStructure;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;


import java.util.List;
import java.util.Optional;

public class MKStructureCommands {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("mkstruct")
                .then(Commands.literal("list").executes(MKStructureCommands::listStructures));

    }

    static int listStructures(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().asPlayer();
        MinecraftServer server = player.getServer();
        if (server != null){

            Optional<List<MKJigsawStructure.Start>> starts = StructureUtils.getStructuresOverlaps(player);
            if (starts.isPresent()) {
                List<MKJigsawStructure.Start> s = starts.get();
                if (s.isEmpty()) {
                    player.sendMessage(new TranslationTextComponent("mknpc.command.not_in_struct"), Util.DUMMY_UUID);
                } else {
                    s.forEach(start -> {
                        MKJigsawStructure struct = (MKJigsawStructure) start.getStructure();
                        player.sendMessage(new TranslationTextComponent("mknpc.command.in_struct",
                                struct.getStructureName(), start.getInstanceId()), Util.DUMMY_UUID);
                    });
                }

            } else {
                player.sendMessage(new TranslationTextComponent("mknpc.command.not_in_struct"), Util.DUMMY_UUID);
            }





        }

        return Command.SINGLE_SUCCESS;
    }

}
