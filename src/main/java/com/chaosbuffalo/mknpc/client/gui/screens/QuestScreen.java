package com.chaosbuffalo.mknpc.client.gui.screens;

import com.chaosbuffalo.mkcore.client.gui.AbilityPanelScreen;
import com.chaosbuffalo.mkcore.client.gui.GuiTextures;
import com.chaosbuffalo.mkcore.client.gui.widgets.*;
import com.chaosbuffalo.mknpc.capabilities.NpcCapabilities;
import com.chaosbuffalo.mknpc.client.gui.widgets.QuestListEntry;
import com.chaosbuffalo.mknpc.client.gui.widgets.QuestPanel;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKLayout;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutVertical;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKRectangle;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKWidget;
import com.chaosbuffalo.mkwidgets.utils.TextureRegion;
import net.minecraft.util.text.StringTextComponent;


public class QuestScreen extends AbilityPanelScreen {

    private QuestPanel questPanel;

    public QuestScreen() {
        super(new StringTextComponent("Quest Log"));
    }

    @Override
    public void setupScreen() {
        super.setupScreen();
        addState("quests", this::createQuestsPage);
        pushState("quests");
    }

    private MKWidget createQuestsPage() {
        int xPos = width / 2 - PANEL_WIDTH / 2;
        int yPos = height / 2 - PANEL_HEIGHT / 2;
        TextureRegion dataBoxRegion = GuiTextures.CORE_TEXTURES.getRegion(GuiTextures.DATA_BOX);
        if (minecraft == null || minecraft.player == null || dataBoxRegion == null) {
            return new MKLayout(xPos, yPos, PANEL_WIDTH, PANEL_HEIGHT);
        }
        int xOffset = GuiTextures.CORE_TEXTURES.getCenterXOffset(
                GuiTextures.DATA_BOX, GuiTextures.BACKGROUND_320_240);
        MKLayout root = getRootLayout(xPos, yPos, xOffset, dataBoxRegion.width, true);
        minecraft.player.getCapability(NpcCapabilities.PLAYER_QUEST_DATA_CAPABILITY).ifPresent((pData) -> {
            int contentX = xPos + xOffset;
            int contentY = yPos + DATA_BOX_OFFSET;
            int contentWidth = dataBoxRegion.width;
            int contentHeight = dataBoxRegion.height;
            ScrollingListPanelLayout panel = new ScrollingListPanelLayout(
                    contentX, contentY, contentWidth, contentHeight);
            currentScrollingPanel = panel;
            QuestPanel questPanel = new QuestPanel(0, 0, panel.getContentScrollView().getWidth(),
                    panel.getContentScrollView().getWidth(), pData, font);
            this.questPanel = questPanel;
            panel.setContent(questPanel);
            MKStackLayoutVertical stackLayout = new MKStackLayoutVertical(0, 0,
                    panel.getListScrollView().getWidth());
            stackLayout.setMarginTop(4).setMarginBot(4).setPaddingTop(2).setMarginLeft(4)
                    .setMarginRight(4).setPaddingBot(2).setPaddingRight(2);
            stackLayout.doSetChildWidth(true);
            pData.getQuestChains().forEach(questChain -> {
                if (!questChain.isQuestComplete()){
                    QuestListEntry questEntry = new QuestListEntry(0, 0, 16, font, questChain, questPanel);
                    stackLayout.addWidget(questEntry);
                    MKRectangle div = new MKRectangle(0, 0,
                            panel.getListScrollView().getWidth() - 8, 1, 0x99ffffff);
                    stackLayout.addWidget(div);
                }
            });
            panel.setList(stackLayout);
            root.addWidget(panel);
        });
        return root;
    }
}
