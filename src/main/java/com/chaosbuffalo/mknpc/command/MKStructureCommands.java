package com.chaosbuffalo.mknpc.command;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.capabilities.IWorldNpcData;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.capabilities.PointOfInterestEntry;
import com.chaosbuffalo.mknpc.npc.MKStructureEntry;
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
import net.minecraft.world.World;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class MKStructureCommands {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("mkstruct")
                .then(Commands.literal("list").executes(MKStructureCommands::listStructures))
                .then(Commands.literal("pois").executes(MKStructureCommands::listPoiForStruct));

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

    static int listPoiForStruct(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().asPlayer();
        MinecraftServer server = player.getServer();
        if (server != null){

            Optional<List<MKJigsawStructure.Start>> starts = StructureUtils.getStructuresOverlaps(player);
            if (starts.isPresent()) {
                List<MKJigsawStructure.Start> s = starts.get();
                if (s.isEmpty()) {
                    player.sendMessage(new TranslationTextComponent("mknpc.command.not_in_struct"), Util.DUMMY_UUID);
                } else {
                    World overworld = server.getWorld(World.OVERWORLD);
                    if (overworld != null){
                        overworld.getCapability(NpcCapabilities.WORLD_NPC_DATA_CAPABILITY)
                                .ifPresent(cap ->  {
                                    s.forEach(start -> {
                                        MKJigsawStructure struct = (MKJigsawStructure) start.getStructure();
                                        MKStructureEntry entry = cap.getStructureData(start.getInstanceId());
                                        if (entry != null) {
                                            Map<String, List<PointOfInterestEntry>> pois = entry.getPointsOfInterest();
                                            if (pois.entrySet().stream().allMatch(m -> m.getValue().isEmpty())) {
                                                player.sendMessage(new TranslationTextComponent(
                                                        "mknpc.command.pois_struct_no_poi",
                                                        struct.getStructureName(), start.getInstanceId()), Util.DUMMY_UUID);
                                            } else {
                                                player.sendMessage(new TranslationTextComponent("mknpc.command.pois_for_struct",
                                                        struct.getStructureName(), start.getInstanceId()), Util.DUMMY_UUID);
                                                pois.forEach((key, value) -> value.forEach(
                                                        poi -> player.sendMessage(new TranslationTextComponent(
                                                                "mknpc.command.pois_struct_desc",
                                                                key, poi.getLocation().toString()), Util.DUMMY_UUID)));
                                            }
                                        } else {
                                            player.sendMessage(new TranslationTextComponent(
                                                    "mknpc.command.pois_struct_not_found",
                                                    struct.getStructureName(), start.getInstanceId()), Util.DUMMY_UUID);
                                        }
                                    });
                                });
                    } else {
                        player.sendMessage(new TranslationTextComponent("mknpc.command.cant_find_cap"), Util.DUMMY_UUID);
                    }

                }

            } else {
                player.sendMessage(new TranslationTextComponent("mknpc.command.not_in_struct"), Util.DUMMY_UUID);
            }





        }

        return Command.SINGLE_SUCCESS;
    }

}
