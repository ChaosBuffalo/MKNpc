package com.chaosbuffalo.mknpc.client.gui.widgets;

import com.chaosbuffalo.mknpc.npc.NpcDefinitionClient;
import com.chaosbuffalo.mknpc.npc.NpcDefinitionManager;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutVertical;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKScrollView;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKWidget;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class NpcDefinitionList extends MKWidget {
    private MKScrollView definitionScrollview;
    private MKStackLayoutVertical definitionLayout;
    private final FontRenderer font;
    private final Consumer<NpcDefinitionClient> selectCallback;

    public NpcDefinitionList(int x, int y, int width, int height, FontRenderer font,
                             Consumer<NpcDefinitionClient> callback) {
        super(x, y, width, height);
        this.selectCallback = callback;
        definitionScrollview = new MKScrollView(x, y + 60,
                width, height - 60, true);
        definitionLayout = new MKStackLayoutVertical(0, 0, width);
        addWidget(definitionScrollview);
        this.font = font;
        populateDefinitions(definitionLayout);
        definitionScrollview.addWidget(definitionLayout);
    }

    private void populateDefinitions(MKStackLayoutVertical layout){
        List<NpcDefinitionClient> defs = new ArrayList<>(NpcDefinitionManager.CLIENT_DEFINITIONS.values());
        defs.sort(Comparator.comparing(NpcDefinitionClient::getName));
        for (NpcDefinitionClient clientDef : defs){
            layout.addWidget(new NpcDefinitionEntry(
                    clientDef,
                    getWidth() - layout.getMarginLeft() - layout.getMarginRight(),
                    font,
                    selectCallback));
        }
    }
}
