package com.x4yi.hammersunbound.client.gui.component;
import net.minecraft.client.renderer.GlStateManager;
public class GuiButton extends GuiComponent {
    protected String text;
    protected Runnable onClick;
    protected int bgColor = 0x555555;
    protected int hoverBgColor = 0x777777;
    protected int textColor = 0xFFFFFF;
    protected boolean isHovered = false;
    public GuiButton(int x, int y, int width, int height, String text, Runnable onClick) {
        super(x, y, width, height);
        this.text = text;
        this.onClick = onClick;
    }
    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        isHovered = isMouseOver(mouseX, mouseY);
        int currentBg = isHovered ? hoverBgColor : bgColor;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawRect(x, y, x + width, y + height, currentBg);
        int borderColor = isHovered ? 0xFFFFFF : 0x999999;
        drawHorizontalLine(x, x + width, y, borderColor);
        drawHorizontalLine(x, x + width, y + height - 1, borderColor);
        drawVerticalLine(x, y, y + height, borderColor);
        drawVerticalLine(x + width - 1, y, y + height, borderColor);
        int textColor = enabled ? this.textColor : 0x999999;
        drawCenteredString(text, x + width / 2, y + (height - 8) / 2, textColor);
    }
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!super.mouseClicked(mouseX, mouseY, mouseButton)) return false;
        if (onClick != null && enabled) {
            onClick.run();
        }
        return true;
    }
    protected void drawHorizontalLine(int startX, int endX, int y, int color) {
        net.minecraft.client.gui.Gui.drawRect(startX, y, endX, y + 1, color);
    }
    protected void drawVerticalLine(int x, int startY, int endY, int color) {
        net.minecraft.client.gui.Gui.drawRect(x, startY, x + 1, endY, color);
    }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public void setOnClick(Runnable onClick) { this.onClick = onClick; }
    public void setBgColor(int color) { this.bgColor = color; }
    public void setHoverBgColor(int color) { this.hoverBgColor = color; }
}