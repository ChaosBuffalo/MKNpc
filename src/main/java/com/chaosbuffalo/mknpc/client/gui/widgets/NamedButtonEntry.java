package com.chaosbuffalo.mknpc.client.gui.widgets;

import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKButton;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKText;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.StringTextComponent;

public class NamedButtonEntry extends CenteringHorizontalLayout {
    private final MKButton button;
    private final MKText label;


    public NamedButtonEntry(int x, int y, int height, String buttonText,
                            String nameText, FontRenderer fontRenderer) {
        super(x, y, height, fontRenderer);
        button = new MKButton(Math.max(fontRenderer.getStringWidth(buttonText), 100),
                buttonText);
        label = new MKText(fontRenderer, nameText, fontRenderer.getStringWidth(nameText));
        addWidget(label);
        addWidget(button);
    }

    public void updateButtonText(String newText){
        button.setWidth(Math.max(fontRenderer.getStringWidth(newText), 100));
        button.buttonText = new StringTextComponent(newText);
    }

    public MKButton getButton() {
        return button;
    }
}
