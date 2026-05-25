package com.x4yi.hammersunbound.client.gui.base;

import com.x4yi.hammersunbound.client.gui.component.GuiComponent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GuiBaseScreen extends GuiScreen {

    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("hammersunbound", "textures/gui/config_background.png");

    protected final GuiScreen parentScreen;
    protected final String title;
    protected final List<GuiComponent> components = new ArrayList<>();

    protected int titleX;
    protected int titleY;

    protected float animationProgress = 0.0f;
    protected boolean isAnimating = true;
    protected static final float ANIMATION_SPEED = 0.15f;

    public GuiBaseScreen(GuiScreen parent, String title) {
        this.parentScreen = parent;
        this.title = title;
    }

    @Override
    public void initGui() {
        super.initGui();
        titleX = width / 2;
        titleY = 15;
        animationProgress = 0.0f;
        isAnimating = true;
        initComponents();
    }

    protected abstract void initComponents();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        updateAnimation();
        drawTitle();

        for (GuiComponent component : components) {
            component.drawComponent(mouseX, mouseY, partialTicks);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void drawBackground(int mouseX, int mouseY, float partialTicks) {
        int centerX = width / 2;
        int centerY = height / 2;
        int panelWidth = 300;
        int panelHeight = 200;

        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawModalRectWithCustomSizedTexture(
                centerX - panelWidth / 2,
                centerY - panelHeight / 2 + 10,
                0, 0,
                panelWidth, panelHeight,
                panelWidth, panelHeight
        );
    }

    protected void drawTitle() {
        drawCenteredString(fontRenderer, title, titleX, titleY, 0xFFFFFF);
    }

    protected void updateAnimation() {
        if (isAnimating) {
            animationProgress += ANIMATION_SPEED;
            if (animationProgress >= 1.0f) {
                animationProgress = 1.0f;
                isAnimating = false;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (GuiComponent component : components) {
            if (component.mouseClicked(mouseX, mouseY, mouseButton)) {
                break;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (GuiComponent component : components) {
            component.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (GuiComponent component : components) {
            component.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (GuiComponent component : components) {
            if (component.keyTyped(typedChar, keyCode)) {
                return;
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        for (GuiComponent component : components) {
            component.handleMouseInput();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    protected void addComponent(GuiComponent component) {
        components.add(component);
    }

    protected void removeComponent(GuiComponent component) {
        components.remove(component);
    }

    public void closeScreen() {
        mc.displayGuiScreen(parentScreen);
    }
}
