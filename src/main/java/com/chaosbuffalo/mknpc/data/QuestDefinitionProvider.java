package com.chaosbuffalo.mknpc.data;

import com.chaosbuffalo.mknpc.MKNpc;
import com.chaosbuffalo.mknpc.quest.QuestDefinition;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;

public abstract class QuestDefinitionProvider implements IDataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator generator;

    public QuestDefinitionProvider(DataGenerator generator) {
        this.generator = generator;
    }


    public void writeDefinition(QuestDefinition definition, @Nonnull DirectoryCache cache){
        Path outputFolder = this.generator.getOutputFolder();
        ResourceLocation key = definition.getName();
        Path path = outputFolder.resolve("data/" + key.getNamespace() + "/mkquests/" + key.getPath() + ".json");
        try {
            JsonElement element = definition.serialize(JsonOps.INSTANCE);
            IDataProvider.save(GSON, cache, element, path);
        } catch (IOException e){
            MKNpc.LOGGER.error("Couldn't write quest {}", path, e);
        }
    }

    @Override
    public String getName() {
        return "MKNpc Quest Definitions";
    }
}
