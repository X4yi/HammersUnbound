package com.x4yi.hammersunbound.client.gui;

import com.x4yi.x4ui.client.gui.base.GuiBaseScreen;
import com.x4yi.x4ui.client.gui.component.*;
import com.x4yi.x4ui.client.gui.component.layout.FlexDirection;
import com.x4yi.hammersunbound.config.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

public class GuiConfigScreen extends GuiBaseScreen {
    private ConfigSection.Section currentSection = ConfigSection.Section.ITEMS;
    private final List<String> expandedSubSections = new ArrayList<>();
    private GuiScrollPanel contentScroll;
    private float savedScrollY = 0;
    private float savedTargetScrollY = 0;

    public GuiConfigScreen(GuiScreen parent) {
        super(parent, "Hammers Unbound - Configuration");
        ConfigSection.buildSections();
    }

    @Override
    protected void drawTitle() {
        // Disabled, we draw everything in the window panel
    }

    @Override
    protected void initComponents() {
        components.clear();
        if (rootPanel != null) rootPanel.clearChildren();

        int startX = width / 2 - 230;
        int startY = height / 2 - 120;
        int totalWidth = 460;
        int totalHeight = 240;
        int headerHeight = 22;
        int footerHeight = 26;
        int sidebarWidth = 110;
        int panelWidth = totalWidth - sidebarWidth;
        int panelHeight = totalHeight - headerHeight - footerHeight;

        GuiPanel windowPanel = new GuiPanel(startX, startY, totalWidth, totalHeight) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                int absX = getAbsoluteX();
                int absY = getAbsoluteY();
                
                // Shadows and main bg
                drawRect(absX - 2, absY - 2, absX + width + 2, absY + height + 2, 0x55000000);
                drawRect(absX, absY, absX + width, absY + height, 0xFF0B0B0D);
                
                // Header bg
                drawRect(absX, absY, absX + width, absY + headerHeight, 0xFF08080A);
                drawRect(absX, absY + headerHeight - 1, absX + width, absY + headerHeight, 0xFF222228);
                
                // Title
                drawString(net.minecraft.client.resources.I18n.format("gui.hammersunbound.title"), absX + 8, absY + 7, 0xFFFFFFFF);
                drawString(com.x4yi.hammersunbound.HammersUnbound.VERSION, absX + 105, absY + 7, 0xFF707077);
                
                // Footer bg
                drawRect(absX, absY + height - footerHeight, absX + width, absY + height, 0xFF0E0E12);
                drawRect(absX, absY + height - footerHeight, absX + width, absY + height - footerHeight + 1, 0xFF222228);
                
                // Sidebar bg
                drawRect(absX, absY + headerHeight, absX + sidebarWidth, absY + height - footerHeight, 0xFF0F0F12);
                drawRect(absX + sidebarWidth - 1, absY + headerHeight, absX + sidebarWidth, absY + height - footerHeight, 0xFF222228);
                
                // Main content bg
                drawRect(absX + sidebarWidth, absY + headerHeight, absX + width, absY + height - footerHeight, 0xFF070708);
            }
        };
        rootPanel.addChild(windowPanel);

        // Close 'x' button
        GuiButton closeBtn = new GuiButton(totalWidth - 18, 4, 12, 12, "x", () -> closeScreen()) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                int textColor = isMouseOver(mouseX, mouseY) ? 0xFFFF3D00 : 0xFF888892;
                drawString(getText(), getAbsoluteX() + 4, getAbsoluteY() + 2, textColor);
            }
        };
        windowPanel.addChild(closeBtn);

        // Footer Save button
        int saveY = totalHeight - footerHeight + 4;
        GuiButton saveBtn = new GuiButton(8, saveY, 100, 18, net.minecraft.client.resources.I18n.format("gui.hammersunbound.save_close"), () -> saveAndClose()) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX();
                int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFF00C853 : 0xFF16161E);
                drawBorder(absX, absY, absX + width, absY + height, hovered ? 0xFFFFFFFF : 0xFF2C2C36);
                drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 5, hovered ? 0xFFFFFFFF : 0xFF00C853);
            }
        };
        windowPanel.addChild(saveBtn);

        // Footer Cancel button
        GuiButton cancelBtn = new GuiButton(115, saveY, 70, 18, net.minecraft.client.resources.I18n.format("gui.hammersunbound.cancel"), () -> closeScreen()) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX();
                int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFFFF3D00 : 0xFF16161E);
                drawBorder(absX, absY, absX + width, absY + height, hovered ? 0xFFFFFFFF : 0xFF2C2C36);
                drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 5, hovered ? 0xFFFFFFFF : 0xFFFF3D00);
            }
        };
        windowPanel.addChild(cancelBtn);

        // Sidebar Tabs container
        GuiPanel sidebarPanel = new GuiPanel(0, headerHeight + 6, sidebarWidth, totalHeight - headerHeight - footerHeight - 6);
        sidebarPanel.setFlexDirection(FlexDirection.VERTICAL);
        sidebarPanel.setGap(2);
        windowPanel.addChild(sidebarPanel);

        for (ConfigSection.Section section : ConfigSection.Section.values()) {
            boolean active = section == currentSection;
            GuiButton tabBtn = new GuiButton(0, 0, sidebarWidth, 22, net.minecraft.client.resources.I18n.format("gui.hammersunbound.section." + section.name().toLowerCase()), () -> {
                playClickSound();
                currentSection = section;
                expandedSubSections.clear();
                savedScrollY = 0;
                savedTargetScrollY = 0;
                initComponents();
            }) {
                @Override
                protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                    int absX = getAbsoluteX();
                    int absY = getAbsoluteY();
                    boolean hovered = isMouseOver(mouseX, mouseY);
                    if (active) {
                        drawRect(absX, absY, absX + width - 1, absY + height, 0xFF18181F);
                        drawRect(absX, absY, absX + 2, absY + height, 0xFF00C853);
                        drawString(getText(), absX + 12, absY + 7, 0xFFFFFFFF);
                    } else {
                        if (hovered) {
                            drawRect(absX, absY, absX + width - 1, absY + height, 0xFF121217);
                        }
                        drawString(getText(), absX + 12, absY + 7, hovered ? 0xFFE0E0E6 : 0xFF888892);
                    }
                }
            };
            sidebarPanel.addChild(tabBtn);
        }

        // Sidebar Guide / Changelog
        int sidebarAreaHeight = totalHeight - headerHeight - footerHeight;
        GuiButton guideBtn = new GuiButton(8, headerHeight + sidebarAreaHeight - 46, sidebarWidth - 16, 18, "Guide", () -> { playClickSound(); mc.displayGuiScreen(new GuiGuideScreen(parentScreen)); }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX();
                int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFF00796B : 0xFF16161E);
                drawBorder(absX, absY, absX + width, absY + height, hovered ? 0xFFFFFFFF : 0xFF2C2C36);
                drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 5, hovered ? 0xFFFFFFFF : 0xFF4DB6AC);
            }
        };
        windowPanel.addChild(guideBtn);

        GuiButton changeBtn = new GuiButton(8, headerHeight + sidebarAreaHeight - 24, sidebarWidth - 16, 18, net.minecraft.client.resources.I18n.format("gui.hammersunbound.changelog"), () -> { playClickSound(); mc.displayGuiScreen(new GuiChangelogScreen(parentScreen)); }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX();
                int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFF6A1B9A : 0xFF16161E);
                drawBorder(absX, absY, absX + width, absY + height, hovered ? 0xFFFFFFFF : 0xFF2C2C36);
                drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 5, hovered ? 0xFFFFFFFF : 0xFFB580D8);
            }
        };
        windowPanel.addChild(changeBtn);

        // Content Scroll Panel
        contentScroll = new GuiScrollPanel(sidebarWidth, headerHeight, panelWidth, panelHeight);
        contentScroll.setScrollY(savedScrollY);
        contentScroll.setTargetScrollY(savedTargetScrollY);
        windowPanel.addChild(contentScroll);

        contentScroll.getContentPanel().setGap(2);

        for (ConfigSection.SubSection sub : ConfigSection.getSubSections(currentSection)) {
            boolean expanded = expandedSubSections.contains(sub.getId());

            GuiPanel subHeader = new GuiPanel(0, 0, panelWidth, 20) {
                @Override
                protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                    boolean hovered = isMouseOver(mouseX, mouseY);
                    int absX = getAbsoluteX();
                    int absY = getAbsoluteY();
                    drawRect(absX + 4, absY, absX + width - 8, absY + height, hovered ? 0xFF202027 : 0xFF16161C);
                    drawBorder(absX + 4, absY, absX + width - 8, absY + height, 0xFF222228);
                    drawString(expanded ? "v" : ">", absX + 12, absY + 6, 0xFFE0E0E6);
                    drawString(sub.getDisplayName(), absX + 24, absY + 6, 0xFFE0E0E6);
                }

                @Override
                public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
                    if (super.mouseClicked(mouseX, mouseY, mouseButton)) return true; // reset button intercepted
                    if (isMouseOver(mouseX, mouseY) && mouseX >= getAbsoluteX() + 4 && mouseX <= getAbsoluteX() + width - 8) {
                        playClickSound();
                        if (expanded) expandedSubSections.remove(sub.getId());
                        else expandedSubSections.add(sub.getId());
                        if (contentScroll != null) {
                            savedScrollY = contentScroll.getScrollY();
                            savedTargetScrollY = contentScroll.getTargetScrollY();
                        }
                        initComponents();
                        return true;
                    }
                    return false;
                }
            };
            
            GuiButton subReset = new GuiButton(panelWidth - 25, 5, 12, 10, "R", () -> {
                playClickSound();
                for (ConfigSection.ConfigField field : sub.getFields()) field.resetToDefault();
                if (contentScroll != null) {
                    savedScrollY = contentScroll.getScrollY();
                    savedTargetScrollY = contentScroll.getTargetScrollY();
                }
                initComponents();
            }) {
                @Override
                protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                    boolean hovered = isMouseOver(mouseX, mouseY);
                    int absX = getAbsoluteX();
                    int absY = getAbsoluteY();
                    drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFFFF3D00 : 0xFF2A2A35);
                    drawString("R", absX + 3, absY + 1, 0xFFFFFFFF);
                }
            };
            subHeader.addChild(subReset);
            contentScroll.addChild(subHeader);

            if (expanded) {
                for (ConfigSection.ConfigField field : sub.getFields()) {
                    GuiPanel fieldRow = new GuiPanel(0, 0, panelWidth, 20) {
                        @Override
                        protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                            if (!field.getLabel().isEmpty() && field.getType() == ConfigSection.ConfigField.Type.BOOLEAN) {
                                // Boolean label is handled internally by toggle or custom draw
                            }
                        }
                    };

                    GuiButton fReset = new GuiButton(panelWidth - 15, 4, 10, 10, "R", () -> {
                        playClickSound();
                        field.resetToDefault();
                        if (contentScroll != null) {
                            savedScrollY = contentScroll.getScrollY();
                            savedTargetScrollY = contentScroll.getTargetScrollY();
                        }
                        initComponents();
                    }) {
                        @Override
                        protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                            boolean hovered = isMouseOver(mouseX, mouseY);
                            int absX = getAbsoluteX();
                            int absY = getAbsoluteY();
                            drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFFFF3D00 : 0xFF202027);
                            drawString("R", absX + 2, absY + 1, 0xFFFFFFFF);
                        }
                    };
                    fieldRow.addChild(fReset);

                    if (field.getType() == ConfigSection.ConfigField.Type.BOOLEAN) {
                        GuiToggle toggle = new GuiToggle(24, 4, panelWidth - 52 - 24, 12, field.getLabel(), field.getBoolValue(), (val) -> {
                            playClickSound();
                            field.setBoolValue(val);
                        }) {
                            @Override
                            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                                int absX = getAbsoluteX();
                                int absY = getAbsoluteY();
                                boolean hovered = isMouseOver(mouseX, mouseY);
                                drawString(label, absX, absY + 2, hovered ? 0xFFFFFFFF : 0xFFB0B0BB);
                                
                                int badgeW = 24;
                                int badgeX = absX + width - badgeW;
                                if (state) {
                                    drawRect(badgeX, absY, badgeX + badgeW, absY + height, 0xFF00C853);
                                    drawString("ON", badgeX + 6, absY + 2, 0xFFFFFFFF);
                                } else {
                                    drawRect(badgeX, absY, badgeX + badgeW, absY + height, 0xFF424248);
                                    drawString("OFF", badgeX + 4, absY + 2, 0xFFA0A0A5);
                                }
                            }
                        };
                        fieldRow.addChild(toggle);
                    } else {
                        GuiSlider slider = new GuiSlider(24, 2, panelWidth - 45, 16, field.getLabel(), field.getMinValue(), field.getMaxValue(), field.getFloatValue(), field.getType() == ConfigSection.ConfigField.Type.INT, (val) -> {
                            field.setFloatValue(val);
                        });
                        fieldRow.addChild(slider);
                    }
                    contentScroll.addChild(fieldRow);
                }
            }
        }
    }

    private void drawBorder(int left, int top, int right, int bottom, int color) {
        drawRect(left, top, right, top + 1, color);
        drawRect(left, bottom - 1, right, bottom, color);
        drawRect(left, top + 1, left + 1, bottom - 1, color);
        drawRect(right - 1, top + 1, right, bottom - 1, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        updateAnimation();

        GlStateManager.pushMatrix();
        if (animationProgress < 1.0f) {
            float scale = 0.9f + (0.1f * animationProgress);
            GlStateManager.translate(width / 2.0f, height / 2.0f, 0);
            GlStateManager.scale(scale, scale, 1.0f);
            GlStateManager.translate(-width / 2.0f, -height / 2.0f, 0);
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        GlStateManager.popMatrix();
    }

    private void playClickSound() {
        mc.getSoundHandler().playSound(
            net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord(
                net.minecraft.init.SoundEvents.UI_BUTTON_CLICK, 1.0F
            )
        );
    }

    private void saveAndClose() {
        new Thread(() -> {
            ConfigManager.save();
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                ConfigManager.reload();
            }
        }).start();
        closeScreen();
    }
}