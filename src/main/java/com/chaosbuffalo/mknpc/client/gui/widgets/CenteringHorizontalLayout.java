package com.chaosbuffalo.mknpc.client.gui.widgets;

import com.chaosbuffalo.mkwidgets.client.gui.constraints.CenterYConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.LayoutRelativeHeightConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.StackConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutHorizontal;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.IMKWidget;
import net.minecraft.client.gui.FontRenderer;

public abstract class CenteringHorizontalLayout extends MKStackLayoutHorizontal {
    protected final FontRenderer fontRenderer;

    public CenteringHorizontalLayout(int x, int y, int height, FontRenderer fontRenderer) {
        super(x, y, height);
        this.fontRenderer = fontRenderer;
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    @Override
    public boolean addWidget(IMKWidget widget) {
        super.addWidget(widget);
        clearWidgetConstraints(widget);
        this.addConstraintToWidget(new CenterYConstraint(), widget);
        this.addConstraintToWidget(StackConstraint.HORIZONTAL, widget);
        if (this.shouldSetChildHeight()) {
            this.addConstraintToWidget(new LayoutRelativeHeightConstraint(1.0F), widget);
        }

        return true;
    }
}
