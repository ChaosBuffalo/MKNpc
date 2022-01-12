package com.chaosbuffalo.mknpc.client.gui.widgets;

import com.chaosbuffalo.mknpc.capabilities.IPlayerQuestingData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestChainInstance;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestObjectiveData;
import com.chaosbuffalo.mknpc.quest.data.player.PlayerQuestReward;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.MarginConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.OffsetConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKLayout;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutVertical;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKRectangle;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKText;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


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
            questLayout.setMargins(5, 5, 5, 5);
            questLayout.setPaddings(0, 0, 2, 2);

            List<Map.Entry<String, PlayerQuestData>> quests = new ArrayList<>(currentChain.getQuestData().entrySet());
            Collections.reverse(quests);
            int index = 0;
            for (Map.Entry<String, PlayerQuestData> questEntry : quests){
                PlayerQuestData current = questEntry.getValue();
                MKText quest_desc = new MKText(fontRenderer, current.getDescription());
                quest_desc.setColor(index == 0 ? 0xffffffff : 0x99ffffff);
                quest_desc.setMultiline(true);
                quest_desc.setWidth(getWidth() - 10);
                questLayout.addWidget(quest_desc);
                MKText objectiveName = new MKText(fontRenderer, new TranslationTextComponent("mknpc.gui.objectives.name").mergeStyle(TextFormatting.BOLD));
                objectiveName.setColor(index == 0 ? 0xffffffff : 0x99ffffff);
                questLayout.addWidget(objectiveName);
                for (PlayerQuestObjectiveData obj : current.getObjectives()){
                    obj.getDescription().forEach(desc -> {
                        MKText obj_desc = new MKText(fontRenderer, obj.isComplete() ?
                                desc.deepCopy().mergeStyle(TextFormatting.STRIKETHROUGH) : desc);
                        obj_desc.setMultiline(true);
                        obj_desc.setColor(obj.isComplete() ? 0x99ffffff : 0xffffffff);
                        obj_desc.setWidth(getWidth() - 30);
                        questLayout.addWidget(obj_desc);
                        questLayout.addConstraintToWidget(new OffsetConstraint(20, 0, true, false), obj_desc);
                    });

                }
                List<PlayerQuestReward> rewards = current.getQuestRewards();
                if (rewards.size() >0 ){
                    MKText rewardName = new MKText(fontRenderer, new TranslationTextComponent("mknpc.gui.rewards.name").mergeStyle(TextFormatting.BOLD));
                    rewardName.setColor(index == 0 ? 0xffffffff : 0x99ffffff);
                    questLayout.addWidget(rewardName);
                    for (PlayerQuestReward reward : rewards){
                        MKText reward_desc = new MKText(fontRenderer, index == 0 ? reward.getDescription() :
                                reward.getDescription().deepCopy().mergeStyle(TextFormatting.STRIKETHROUGH));
                        reward_desc.setMultiline(true);
                        reward_desc.setColor(index == 0 ? 0xffffffff : 0x99ffffff);
                        reward_desc.setWidth(getWidth() - 30);
                        questLayout.addWidget(reward_desc);
                        questLayout.addConstraintToWidget(new OffsetConstraint(20, 0, true, false), reward_desc);
                    }
                }
                MKRectangle div = new MKRectangle(0, 0, getWidth() - 10, 1, 0x99ffffff);
                questLayout.addWidget(div);
                index++;
            }
            questLayout.manualRecompute();
            addWidget(questLayout);
            setHeight(questLayout.getHeight());
        }
    }


    public void setCurrentChain(PlayerQuestChainInstance currentChain) {
        this.currentChain = currentChain;
        clearWidgets();
        setup();
    }
}
