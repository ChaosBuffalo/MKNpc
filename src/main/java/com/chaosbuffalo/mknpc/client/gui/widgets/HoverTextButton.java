package com.chaosbuffalo.mknpc.client.gui.widgets;

import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;


public class HoverTextButton extends MKText {
    private boolean isClicked;
    private final Runnable callback;

    public HoverTextButton(FontRenderer renderer, String text, Runnable callback) {
        super(renderer, text);
        setWidth(20);
        setHeight(20);
        setIsCentered(true);
        this.callback = callback;
    }

    @Override
    public boolean onMousePressed(Minecraft minecraft, double mouseX, double mouseY, int mouseButton) {
        isClicked = true;
        callback.run();
        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY, int mouseButton) {
        isClicked = false;
        return true;
    }

    @Override
    public void draw(Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks) {
        super.draw(mc, x, y, width, height, mouseX, mouseY, partialTicks);
        if (!isHovered() && isClicked){
            isClicked = false;
        }
    }

    @Override
    public void postDraw(Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks) {
        if (isHovered()) {
            mkFill(x, y, x + width, y + height, 0x55ffffff);
        }
        if (isClicked){
            mkFill(x, y, x + width, y + height, 0x55ffffff);
        }
    }
}
