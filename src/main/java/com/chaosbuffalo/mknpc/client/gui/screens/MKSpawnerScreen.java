package com.chaosbuffalo.mknpc.client.gui.screens;

import com.chaosbuffalo.mkcore.client.gui.GuiTextures;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKLayout;
import com.chaosbuffalo.mkwidgets.client.gui.screens.MKScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.text.StringTextComponent;

public class MKSpawnerScreen extends MKScreen {
    protected final int PANEL_WIDTH = 320;
    protected final int PANEL_HEIGHT = 240;
    private final MKSpawnerTileEntity spawnerTileEntity;

    public MKSpawnerScreen(MKSpawnerTileEntity spawnerTileEntity) {
        super(new StringTextComponent("MK Spawner Screen"));
        this.spawnerTileEntity = spawnerTileEntity;
    }

    public MKSpawnerTileEntity getSpawnerTileEntity() {
        return spawnerTileEntity;
    }

    @Override
    public void setupScreen() {
        super.setupScreen();
        addState("main", this::setupChooseDefinition);
        pushState("main");
    }

    private MKLayout setupChooseDefinition(){
        int xPos = width / 2 - PANEL_WIDTH / 2;
        int yPos = height / 2 - PANEL_HEIGHT / 2;
        MKLayout root = new MKLayout(xPos, yPos, PANEL_WIDTH, PANEL_HEIGHT);
        root.setMargins(5, 5, 5, 5);
        root.setPaddingTop(5).setPaddingBot(5);
        return root;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int xPos = width / 2 - PANEL_WIDTH / 2;
        int yPos = height / 2 - PANEL_HEIGHT / 2;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiTextures.CORE_TEXTURES.bind(getMinecraft());
        RenderSystem.disableLighting();
        GuiTextures.CORE_TEXTURES.drawRegionAtPos(GuiTextures.BACKGROUND_320_240, xPos, yPos);
        super.render(mouseX, mouseY, partialTicks);
        RenderSystem.enableLighting();
    }
}
