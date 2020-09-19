package com.chaosbuffalo.mknpc.client.gui.screens;

import com.chaosbuffalo.mkcore.client.gui.GuiTextures;
import com.chaosbuffalo.mknpc.client.gui.widgets.NamedButtonEntry;
import com.chaosbuffalo.mknpc.client.gui.widgets.NpcDefinitionList;
import com.chaosbuffalo.mknpc.client.gui.widgets.SpawnOptionList;
import com.chaosbuffalo.mknpc.network.PacketHandler;
import com.chaosbuffalo.mknpc.network.SetSpawnListPacket;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.spawn.SpawnOption;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKLayout;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutHorizontal;
import com.chaosbuffalo.mkwidgets.client.gui.screens.MKScreen;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKButton;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKImage;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKModal;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.text.StringTextComponent;


public class MKSpawnerScreen extends MKScreen {
    protected final int PANEL_WIDTH = 320;
    protected final int PANEL_HEIGHT = 240;
    protected final int POPUP_WIDTH = 180;
    protected final int POPUP_HEIGHT = 201;
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

    private MKStackLayoutHorizontal getNamedButton(String labelKey, String buttonText){
        return new NamedButtonEntry(0, 0, 20, buttonText, labelKey, font);
    }

    private MKLayout setupChooseDefinition(){
        int xPos = width / 2 - PANEL_WIDTH / 2;
        int yPos = height / 2 - PANEL_HEIGHT / 2;
        MKLayout root = new MKLayout(xPos, yPos, PANEL_WIDTH, PANEL_HEIGHT);
        SpawnOptionList options = new SpawnOptionList(xPos + 20, yPos + 20, 280,
                PANEL_HEIGHT - 60, font, spawnerTileEntity.getSpawnList());
        MKButton addOption = new MKButton(xPos + PANEL_WIDTH / 2 - 30, yPos + PANEL_HEIGHT - 30, 60, 20,
                "Add");
        addOption.setPressedCallback((button, mouse) -> {
                MKModal popup = new MKModal();
                int screenWidth = getWidth();
                int screenHeight = getHeight();
                int popupX = (screenWidth - POPUP_WIDTH) / 2;
                int popupY = (screenHeight - POPUP_HEIGHT) / 2;
                MKImage background = GuiTextures.CORE_TEXTURES.getImageForRegion(
                        GuiTextures.BACKGROUND_180_200, popupX, popupY, POPUP_WIDTH, POPUP_HEIGHT);
                popup.addWidget(background);
                NpcDefinitionList definitions = new NpcDefinitionList(popupX, popupY, POPUP_WIDTH, POPUP_HEIGHT,
                        font, (client) -> {
                    SpawnOption newOption = new SpawnOption(1.0, client.getDefinitionName());
                    spawnerTileEntity.getSpawnList().addOption(newOption);
                    options.populate();
                    closeModal(popup);
                });
                popup.addWidget(definitions);
                addModal(popup);
           return true;
        });
        root.addWidget(addOption);
        root.addWidget(options);
        root.setMargins(5, 5, 5, 5);
        root.setPaddingTop(5).setPaddingBot(5);
        return root;
    }

    @Override
    public void onClose() {
        PacketHandler.getNetworkChannel().sendToServer(new SetSpawnListPacket(spawnerTileEntity));
        super.onClose();
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
