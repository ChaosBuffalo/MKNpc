package com.chaosbuffalo.mknpc.client.gui.screens;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.client.gui.GuiTextures;
import com.chaosbuffalo.mknpc.client.gui.widgets.*;
import com.chaosbuffalo.mknpc.entity.MKEntity;
import com.chaosbuffalo.mknpc.network.PacketHandler;
import com.chaosbuffalo.mknpc.network.SetSpawnListPacket;
import com.chaosbuffalo.mknpc.spawn.MKSpawnerTileEntity;
import com.chaosbuffalo.mknpc.spawn.SpawnOption;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.MarginConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.VerticalStackConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKLayout;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutHorizontal;
import com.chaosbuffalo.mkwidgets.client.gui.screens.MKScreen;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKButton;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKImage;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKModal;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKText;
import com.google.common.collect.Lists;
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
        MKText spawnListLabel = new MKText(font, "Spawn List:");
        root.addWidget(spawnListLabel);
        root.addConstraintToWidget(new MarginConstraint(MarginConstraint.MarginType.TOP), spawnListLabel);
        root.addConstraintToWidget(new MarginConstraint(MarginConstraint.MarginType.LEFT), spawnListLabel);
        SpawnOptionList options = new SpawnOptionList(xPos + 40, yPos + 15, 240, 100, font,
                getSpawnerTileEntity().getSpawnList());
        MKButton addOption = new MKButton(xPos + PANEL_WIDTH / 2 - 50,
                options.getY() + options.getHeight() + 10, 100, 20,
                "Add Spawn");
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
                    getSpawnerTileEntity().getSpawnList().addOption(newOption);
                    options.populate();
                    closeModal(popup);
                });
                popup.addWidget(definitions);
                addModal(popup);
           return true;
        });
        IncrementableField spawnTimeController = new IncrementableField(0, 0, 20, "Respawn Time",
                (double) getSpawnerTileEntity().getRespawnTime() / GameConstants.TICKS_PER_SECOND, font,
                (field, value) -> {
                    double boundedValue = Math.max(1.0, value);
                    getSpawnerTileEntity().setRespawnTime((int) Math.round(boundedValue * GameConstants.TICKS_PER_SECOND));
                    field.setValue(boundedValue);
                });
        root.addConstraintToWidget(new MarginConstraint(MarginConstraint.MarginType.LEFT), spawnTimeController);
        root.addConstraintToWidget(new VerticalStackConstraint(), spawnTimeController);
        root.addWidget(options);
        root.addWidget(addOption);
        root.addWidget(spawnTimeController);
        RadioButtonList<MKEntity.NonCombatMoveType> movementBehaviors = new RadioButtonList<>(0, 0, 200, font, "Movement: ",
                Lists.newArrayList(
                        new RadioButtonList.RadioValue<>(MKEntity.NonCombatMoveType.STATIONARY, "Stationary"),
                        new RadioButtonList.RadioValue<>(MKEntity.NonCombatMoveType.RANDOM_WANDER, "Random Wander")
                        ),
                getSpawnerTileEntity()::setMoveType);
        movementBehaviors.selectEntry(getSpawnerTileEntity().getMoveType());
        root.addConstraintToWidget(new MarginConstraint(MarginConstraint.MarginType.LEFT), movementBehaviors);
        root.addConstraintToWidget(new VerticalStackConstraint(), movementBehaviors);
        root.addWidget(movementBehaviors);
        root.setMargins(6, 6, 6, 6);
        root.setPaddingTop(2).setPaddingBot(2);
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
