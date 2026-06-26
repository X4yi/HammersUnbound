package com.x4yi.hammersunbound.client.gui.component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import java.io.IOException;
public abstract class GuiComponent {
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final FontRenderer fontRenderer = mc.fontRenderer;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected boolean visible = true;
    protected boolean enabled = true;
    public GuiComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public abstract void drawComponent(int mouseX, int mouseY, float partialTicks);
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return isMouseOver(mouseX, mouseY) && enabled;
    }
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }
    public boolean keyTyped(char typedChar, int keyCode) {
        return false;
    }
    public void handleMouseInput() throws IOException {
    }
    protected boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width &&
               mouseY >= y && mouseY <= y + height;
    }
    protected void drawRect(int left, int top, int right, int bottom, int color) {
        net.minecraft.client.gui.Gui.drawRect(left, top, right, bottom, color);
    }
    protected void drawCenteredString(String text, int x, int y, int color) {
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
    }
    protected void drawString(String text, int x, int y, int color) {
        fontRenderer.drawString(text, x, y, color);
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}