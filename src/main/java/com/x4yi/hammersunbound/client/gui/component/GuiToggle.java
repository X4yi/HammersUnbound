package com.x4yi.hammersunbound.client.gui.component;

import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

public class GuiToggle extends GuiComponent {

    protected String label;
    protected boolean state;
    protected Consumer<Boolean> onStateChanged;

    protected int enabledColor = 0x4CAF50;
    protected int disabledColor = 0x666666;
    protected int textColor = 0xFFFFFF;

    public GuiToggle(int x, int y, int width, int height, String label, boolean defaultState,
                     Consumer<Boolean> onStateChanged) {
        super(x, y, width, height);
        this.label = label;
        this.state = defaultState;
        this.onStateChanged = onStateChanged;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        int indicatorColor = state ? enabledColor : disabledColor;
        int indicatorX = state ? x + width - 20 : x;

        drawRect(x, y, x + width, y + height, 0x333333);
        drawRect(indicatorX, y + 2, indicatorX + 18, y + height - 2, indicatorColor);

        drawString(label, x + 24, y + (height - 8) / 2, textColor);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!super.mouseClicked(mouseX, mouseY, mouseButton)) return false;
        state = !state;
        if (onStateChanged != null) {
            onStateChanged.accept(state);
        }
        return true;
    }

    public boolean getState() { return state; }
    public void setState(boolean state) { this.state = state; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
