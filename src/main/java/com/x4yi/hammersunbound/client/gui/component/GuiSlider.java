package com.x4yi.hammersunbound.client.gui.component;
import net.minecraft.client.renderer.GlStateManager;
import java.util.function.Consumer;
public class GuiSlider extends GuiComponent {
    protected String label;
    protected float minValue;
    protected float maxValue;
    protected float currentValue;
    protected Consumer<Float> onValueChanged;
    protected boolean isDragging = false;
    protected int bgColor = 0x333333;
    protected int sliderBgColor = 0x666666;
    protected int hoverSliderBgColor = 0x888888;
    protected int textColor = 0xFFFFFF;
    public GuiSlider(int x, int y, int width, int height, String label,
                     float minValue, float maxValue, float defaultValue,
                     Consumer<Float> onValueChanged) {
        super(x, y, width, height);
        this.label = label;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = Math.max(minValue, Math.min(maxValue, defaultValue));
        this.onValueChanged = onValueChanged;
    }
    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        if (!visible) return;
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawRect(x, y + height / 2 - 2, x + width, y + height / 2 + 2, bgColor);
        int sliderWidth = 8;
        float ratio = (currentValue - minValue) / (maxValue - minValue);
        int sliderX = x + (int) (ratio * (width - sliderWidth));
        int sliderY = y + height / 2 - 6;
        boolean isHovered = mouseX >= sliderX && mouseX <= sliderX + sliderWidth &&
                           mouseY >= sliderY && mouseY <= sliderY + 12;
        int sliderColor = isHovered ? hoverSliderBgColor : sliderBgColor;
        drawRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + 12, sliderColor);
        String valueText = String.format("%.1f", currentValue);
        drawString(label + ": " + valueText, x, y - 10, textColor);
    }
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!super.mouseClicked(mouseX, mouseY, mouseButton)) return false;
        isDragging = true;
        updateValue(mouseX);
        return true;
    }
    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        isDragging = false;
    }
    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (isDragging) {
            updateValue(mouseX);
        }
    }
    protected void updateValue(int mouseX) {
        float ratio = (float) (mouseX - x) / width;
        ratio = Math.max(0.0f, Math.min(1.0f, ratio));
        float newValue = minValue + ratio * (maxValue - minValue);
        newValue = Math.round(newValue * 10.0f) / 10.0f;
        if (newValue != currentValue) {
            currentValue = newValue;
            if (onValueChanged != null) {
                onValueChanged.accept(currentValue);
            }
        }
    }
    public float getValue() { return currentValue; }
    public void setValue(float value) {
        this.currentValue = Math.max(minValue, Math.min(maxValue, value));
    }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}