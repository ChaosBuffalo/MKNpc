package com.chaosbuffalo.mknpc.client.gui.widgets;

import com.chaosbuffalo.mkcore.client.gui.widgets.TalentButton;
import com.chaosbuffalo.mkcore.core.talents.TalentLineDefinition;
import com.chaosbuffalo.mkcore.core.talents.TalentRecord;
import com.chaosbuffalo.mkcore.core.talents.TalentTreeRecord;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import com.chaosbuffalo.mkcore.network.TalentPointActionPacket;
import com.chaosbuffalo.mknpc.capabilities.IPlayerQuestingData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mkwidgets.client.gui.UIConstants;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.MarginConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKLayout;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutVertical;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKText;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;



public class QuestPanel extends MKLayout {
    private final IPlayerQuestingData playerData;
    private PlayerQuestChainInstance currentChain;
    private final FontRenderer fontRenderer;
    private final int originalWidth;
    private final int oringalHeight;

    public QuestPanel(int x, int y, int width, int height,
                      IPlayerQuestingData data, FontRenderer fontRenderer) {
        super(x, y, width, height);
        this.playerData = data;
        this.currentChain = null;
        this.fontRenderer = fontRenderer;
        this.originalWidth = width;
        this.oringalHeight = height;
        setMargins(6, 6, 6, 6);
        setup();
    }

    public PlayerQuestChainInstance getCurrentChain() {
        return currentChain;
    }

    public void setup() {
        if (currentChain == null) {
            MKText noSelectPrompt = new MKText(fontRenderer,
                    new TranslationTextComponent("mknpc.gui.select_quest"));
            addConstraintToWidget(MarginConstraint.TOP, noSelectPrompt);
            addConstraintToWidget(MarginConstraint.LEFT, noSelectPrompt);
            noSelectPrompt.setColor(0xffffffff);
            addWidget(noSelectPrompt);
            setWidth(originalWidth);
            setHeight(oringalHeight);
        } else {
            MKStackLayoutVertical questLayout = new MKStackLayoutVertical(getX(), getY(), getWidth());
            PlayerQuestData current = currentChain.getQuestData(currentChain.getCurrentQuest());
            for (PlayerQuestObjectiveData obj : current.getObjectives()){
                MKText obj_desc = new MKText(fontRenderer, obj.isComplete() ?
                        obj.getDescription().deepCopy().mergeStyle(TextFormatting.STRIKETHROUGH) : obj.getDescription());
                obj_desc.setWidth(getWidth());
                obj_desc.setMultiline(true);
                questLayout.addWidget(obj_desc);
            }
            addWidget(questLayout);
        }
    }


    public void setCurrentChain(PlayerQuestChainInstance currentChain) {
        this.currentChain = currentChain;
        clearWidgets();
        setup();
    }
}
