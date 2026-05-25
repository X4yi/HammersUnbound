package com.x4yi.hammersunbound.client.gui;

import com.x4yi.hammersunbound.client.gui.base.GuiBaseScreen;
import com.x4yi.hammersunbound.client.gui.component.GuiButton;
import com.x4yi.hammersunbound.config.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiConfigScreen extends GuiBaseScreen {

    private ConfigSection.Section currentSection = ConfigSection.Section.ITEMS;

    private float scrollY = 0;
    private float targetScrollY = 0;
    private int maxScrollY = 0;
    private final List<String> expandedSubSections = new ArrayList<>();

    private ConfigSection.ConfigField activeDraggingField = null;
    private ConfigSection.SubSection activeDraggingSubSection = null;

    public GuiConfigScreen(GuiScreen parent) {
        super(parent, "Hammers Unbound - Configuration");
        ConfigSection.buildSections();
    }

    @Override
    protected void drawTitle() {
        // No-op to prevent the default centered title from drawing
    }

    @Override
    protected void initComponents() {
        components.clear();
        
        // Auto-expand the first subsection of the current section to welcome the user
        if (expandedSubSections.isEmpty()) {
            List<ConfigSection.SubSection> subs = ConfigSection.getSubSections(currentSection);
            if (!subs.isEmpty()) {
                expandedSubSections.add(subs.get(0).getId());
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        updateAnimation();
        
        // Smooth scroll
        scrollY += (targetScrollY - scrollY) * 0.2f;
        if (Math.abs(targetScrollY - scrollY) < 0.5f) {
            scrollY = targetScrollY;
        }

        GlStateManager.pushMatrix();
        if (animationProgress < 1.0f) {
            float scale = 0.9f + (0.1f * animationProgress);
            GlStateManager.translate(width / 2.0f, height / 2.0f, 0);
            GlStateManager.scale(scale, scale, 1.0f);
            GlStateManager.translate(-width / 2.0f, -height / 2.0f, 0);
            
            // Fading alpha logic can be simulated by coloring, but standard UI draws mostly opaque rects.
            // A simple scale pop is very effective and smooth.
        }

        int startX = width / 2 - 230;
        int startY = height / 2 - 120;
        int totalWidth = 460;
        int totalHeight = 240;

        int headerHeight = 22;
        int footerHeight = 26;
        int sidebarWidth = 110;

        int panelX = startX + sidebarWidth;
        int panelY = startY + headerHeight;
        int panelWidth = totalWidth - sidebarWidth;
        int panelHeight = totalHeight - headerHeight - footerHeight;

        // Calculate max scroll dynamically
        int currY = panelY + 4;
        for (ConfigSection.SubSection sub : ConfigSection.getSubSections(currentSection)) {
            currY += 22;
            if (expandedSubSections.contains(sub.getId())) {
                for (ConfigSection.ConfigField field : sub.getFields()) {
                    currY += 20;
                }
            }
        }
        int totalHeightContent = currY - (panelY + 4);
        maxScrollY = Math.max(0, totalHeightContent - panelHeight + 8);
        if (targetScrollY > maxScrollY) {
            targetScrollY = maxScrollY;
        }
        if (targetScrollY < 0) {
            targetScrollY = 0;
        }

        // Draw main card body shadow and background
        drawRect(startX - 2, startY - 2, startX + totalWidth + 2, startY + totalHeight + 2, 0x55000000);
        drawRect(startX, startY, startX + totalWidth, startY + totalHeight, 0xFF0B0B0D);

        // Header Background & Borders
        drawRect(startX, startY, startX + totalWidth, startY + headerHeight, 0xFF08080A);
        drawRect(startX, startY + headerHeight - 1, startX + totalWidth, startY + headerHeight, 0xFF222228);

        // Header Text: Premium Branding
        fontRenderer.drawString("Hammers Unbound", startX + 8, startY + 7, 0xFFFFFFFF);
        fontRenderer.drawString(com.x4yi.hammersunbound.HammersUnbound.VERSION, startX + 105, startY + 7, 0xFF707077);

        // Close Button 'X'
        boolean closeHovered = mouseX >= startX + totalWidth - 18 && mouseX <= startX + totalWidth - 6 &&
                              mouseY >= startY + 4 && mouseY <= startY + 16;
        fontRenderer.drawString("x", startX + totalWidth - 14, startY + 5, closeHovered ? 0xFFFF3D00 : 0xFF888892);

        // Footer Background & Border
        drawRect(startX, startY + totalHeight - footerHeight, startX + totalWidth, startY + totalHeight, 0xFF0E0E12);
        drawRect(startX, startY + totalHeight - footerHeight, startX + totalWidth, startY + totalHeight - footerHeight + 1, 0xFF222228);

        // Save & Close Button (Styled)
        int saveX1 = startX + 8;
        int saveY1 = startY + totalHeight - footerHeight + 4;
        int saveX2 = startX + 108;
        int saveY2 = startY + totalHeight - 4;
        boolean saveHovered = mouseX >= saveX1 && mouseX <= saveX2 && mouseY >= saveY1 && mouseY <= saveY2;
        drawRect(saveX1, saveY1, saveX2, saveY2, saveHovered ? 0xFF00C853 : 0xFF16161E);
        drawBorder(saveX1, saveY1, saveX2, saveY2, saveHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Save & Close", saveX1 + 50, saveY1 + 5, saveHovered ? 0xFFFFFFFF : 0xFF00C853);

        // Cancel Button (Styled)
        int cancelX1 = startX + 115;
        int cancelY1 = startY + totalHeight - footerHeight + 4;
        int cancelX2 = startX + 185;
        int cancelY2 = startY + totalHeight - 4;
        boolean cancelHovered = mouseX >= cancelX1 && mouseX <= cancelX2 && mouseY >= cancelY1 && mouseY <= cancelY2;
        drawRect(cancelX1, cancelY1, cancelX2, cancelY2, cancelHovered ? 0xFFFF3D00 : 0xFF16161E);
        drawBorder(cancelX1, cancelY1, cancelX2, cancelY2, cancelHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Cancel", cancelX1 + 35, cancelY1 + 5, cancelHovered ? 0xFFFFFFFF : 0xFFFF3D00);



        // Left Sidebar: Clean Flat Category Tabs
        int sidebarX = startX;
        int sidebarY1 = startY + headerHeight;
        int sidebarHeight = totalHeight - headerHeight - footerHeight;
        drawRect(sidebarX, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF0F0F12);
        drawRect(sidebarX + sidebarWidth - 1, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF222228);

        int tabY = sidebarY1 + 6;
        for (ConfigSection.Section section : ConfigSection.Section.values()) {
            boolean active = section == currentSection;
            boolean tabHovered = mouseX >= sidebarX && mouseX <= sidebarX + sidebarWidth &&
                                 mouseY >= tabY && mouseY <= tabY + 22;

            if (active) {
                drawRect(sidebarX, tabY, sidebarX + sidebarWidth - 1, tabY + 22, 0xFF18181F);
                drawRect(sidebarX, tabY, sidebarX + 2, tabY + 22, 0xFF00C853);
                fontRenderer.drawString(section.getDisplayName(), sidebarX + 12, tabY + 7, 0xFFFFFFFF);
            } else {
                if (tabHovered) {
                    drawRect(sidebarX, tabY, sidebarX + sidebarWidth - 1, tabY + 22, 0xFF121217);
                }
                fontRenderer.drawString(section.getDisplayName(), sidebarX + 12, tabY + 7, tabHovered ? 0xFFE0E0E6 : 0xFF888892);
            }
            tabY += 24;
        }

        // Changelog Button in Sidebar
        int changelogX1 = sidebarX + 8;
        int changelogY1 = sidebarY1 + sidebarHeight - 24;
        int changelogX2 = sidebarX + sidebarWidth - 8;
        int changelogY2 = sidebarY1 + sidebarHeight - 6;
        boolean changelogHovered = mouseX >= changelogX1 && mouseX <= changelogX2 &&
                                   mouseY >= changelogY1 && mouseY <= changelogY2;
        drawRect(changelogX1, changelogY1, changelogX2, changelogY2, changelogHovered ? 0xFF6A1B9A : 0xFF16161E);
        drawBorder(changelogX1, changelogY1, changelogX2, changelogY2, changelogHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Changelog", changelogX1 + (changelogX2 - changelogX1) / 2, changelogY1 + 4, changelogHovered ? 0xFFFFFFFF : 0xFFB580D8);

        // Right Content Panel (Viewport with scissor test)
        drawRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF070708);

        ScaledResolution sr = new ScaledResolution(mc);
        int scale = sr.getScaleFactor();
        int scissorX = panelX * scale;
        int scissorY = mc.displayHeight - (panelY + panelHeight) * scale;
        int scissorW = panelWidth * scale;
        int scissorH = panelHeight * scale;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);

        int drawY = panelY + 4 - (int)scrollY;
        for (ConfigSection.SubSection sub : ConfigSection.getSubSections(currentSection)) {
            boolean headerHovered = mouseX >= panelX + 4 && mouseX <= panelX + panelWidth - 8 &&
                                    mouseY >= drawY && mouseY <= drawY + 20;
            boolean expanded = expandedSubSections.contains(sub.getId());

            // Header block background
            drawRect(panelX + 4, drawY, panelX + panelWidth - 8, drawY + 20, headerHovered ? 0xFF202027 : 0xFF16161C);
            drawBorder(panelX + 4, drawY, panelX + panelWidth - 8, drawY + 20, 0xFF222228);

            // Caret indicator
            fontRenderer.drawString(expanded ? "v" : ">", panelX + 12, drawY + 6, 0xFFE0E0E6);
            // Title
            fontRenderer.drawString(sub.getDisplayName(), panelX + 24, drawY + 6, 0xFFE0E0E6);

            int headerYMid = drawY + 20;
            drawY += 22;

            if (expanded) {
                int fieldsStartY = drawY;
                int fieldsEndY = drawY;
                List<ConfigSection.ConfigField> fields = sub.getFields();

                for (ConfigSection.ConfigField field : fields) {
                    boolean fieldHovered = mouseX >= panelX + 24 && mouseX <= panelX + panelWidth - 12 &&
                                           mouseY >= drawY && mouseY <= drawY + 18;
                    int labelColor = fieldHovered ? 0xFFFFFFFF : 0xFFB0B0BB;

                    if (field.getType() == ConfigSection.ConfigField.Type.BOOLEAN) {
                        // Boolean toggle field
                        fontRenderer.drawString(field.getLabel(), panelX + 24, drawY + 5, labelColor);

                        boolean val = getBooleanValue(sub, field);
                        int tBadgeX1 = panelX + panelWidth - 36;
                        int tBadgeY1 = drawY + 3;
                        int tBadgeX2 = panelX + panelWidth - 12;
                        int tBadgeY2 = drawY + 15;

                        if (val) {
                            drawRect(tBadgeX1, tBadgeY1, tBadgeX2, tBadgeY2, 0xFF00C853);
                            drawCenteredString("ON", tBadgeX1 + 12, tBadgeY1 + 2, 0xFFFFFFFF);
                        } else {
                            drawRect(tBadgeX1, tBadgeY1, tBadgeX2, tBadgeY2, 0xFF424248);
                            drawCenteredString("OFF", tBadgeX1 + 12, tBadgeY1 + 2, 0xFFA0A0A5);
                        }
                    } else {
                        // Slider field
                        float val = getFloatValue(sub, field);
                        String formatStr = field.getType() == ConfigSection.ConfigField.Type.INT ? "%.0f" : "%.1f";
                        String displayStr = field.getLabel() + ": " + String.format(formatStr, val);
                        if (field.getLabel().contains("Multiplier")) {
                            displayStr += "x";
                        }
                        fontRenderer.drawString(displayStr, panelX + 24, drawY + 5, labelColor);

                        int sliderX1 = panelX + panelWidth - 85;
                        int sliderY1 = drawY + 7;
                        int sliderX2 = panelX + panelWidth - 15;
                        int sliderY2 = drawY + 10;

                        // Draw slider track
                        drawRect(sliderX1, sliderY1, sliderX2, sliderY2, 0xFF25252B);

                        float ratio = (val - field.getMinValue()) / (field.getMaxValue() - field.getMinValue());
                        ratio = Math.max(0.0f, Math.min(1.0f, ratio));
                        int fillWidth = (int) (ratio * 70);

                        // Draw colored fill
                        drawRect(sliderX1, sliderY1, sliderX1 + fillWidth, sliderY2, 0xFF00C853);
                        // Draw thumb/handle
                        drawRect(sliderX1 + fillWidth - 1, drawY + 4, sliderX1 + fillWidth + 1, drawY + 13, 0xFFFFFFFF);
                    }
                    drawY += 20;
                    fieldsEndY = drawY - 2;
                }

                // Render beautiful vertical tree guide line to align fields under expanded subsection
                if (fieldsEndY > fieldsStartY) {
                    drawRect(panelX + 14, headerYMid, panelX + 15, fieldsEndY - 10, 0xFF303038);
                }
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Draw a neat scroll indicator if content overflows
        if (maxScrollY > 0) {
            int trackX = panelX + panelWidth - 5;
            int trackY = panelY + 2;
            int trackHeight = panelHeight - 4;
            drawRect(trackX, trackY, trackX + 2, trackY + trackHeight, 0xFF121215);

            float scrollRatio = (float) scrollY / maxScrollY;
            int thumbHeight = Math.max(20, (int) ((float) panelHeight / totalHeightContent * trackHeight));
            int thumbY = trackY + (int) (scrollRatio * (trackHeight - thumbHeight));
            drawRect(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xFF424248);
        }



        super.drawScreen(mouseX, mouseY, partialTicks);
        
        GlStateManager.popMatrix();
    }

    private void drawCenteredString(String text, int x, int y, int color) {
        fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
    }

    private void drawBorder(int left, int top, int right, int bottom, int color) {
        drawRect(left, top, right, top + 1, color);
        drawRect(left, bottom - 1, right, bottom, color);
        drawRect(left, top + 1, left + 1, bottom - 1, color);
        drawRect(right - 1, top + 1, right, bottom - 1, color);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0) return;

        int startX = width / 2 - 230;
        int startY = height / 2 - 120;
        int totalWidth = 460;
        int totalHeight = 240;

        int headerHeight = 22;
        int footerHeight = 26;



        int sidebarWidth = 110;
        int panelX = startX + sidebarWidth;
        int panelY = startY + headerHeight;
        int panelWidth = totalWidth - sidebarWidth;
        int panelHeight = totalHeight - headerHeight - footerHeight;

        // Category Tab clicks
        int tabY = startY + headerHeight + 6;
        for (ConfigSection.Section section : ConfigSection.Section.values()) {
            if (mouseX >= startX && mouseX <= startX + sidebarWidth &&
                mouseY >= tabY && mouseY <= tabY + 22) {
                currentSection = section;
                targetScrollY = 0;
                scrollY = 0;
                expandedSubSections.clear();
                playClickSound();
                initComponents();
                return;
            }
            tabY += 24;
        }

        // Changelog Button click
        int sidebarX = startX;
        int sidebarHeight = totalHeight - headerHeight - footerHeight;
        int changelogX1 = sidebarX + 8;
        int changelogY1 = startY + headerHeight + sidebarHeight - 24;
        int changelogX2 = sidebarX + sidebarWidth - 8;
        int changelogY2 = startY + headerHeight + sidebarHeight - 6;
        if (mouseX >= changelogX1 && mouseX <= changelogX2 &&
            mouseY >= changelogY1 && mouseY <= changelogY2) {
            playClickSound();
            mc.displayGuiScreen(new GuiChangelogScreen(this));
            return;
        }

        // Close Button click
        if (mouseX >= startX + totalWidth - 18 && mouseX <= startX + totalWidth - 6 &&
            mouseY >= startY + 4 && mouseY <= startY + 16) {
            playClickSound();
            closeScreen();
            return;
        }

        // Save & Close Button click
        int saveX1 = startX + 8;
        int saveY1 = startY + totalHeight - footerHeight + 4;
        int saveX2 = startX + 108;
        int saveY2 = startY + totalHeight - 4;
        if (mouseX >= saveX1 && mouseX <= saveX2 && mouseY >= saveY1 && mouseY <= saveY2) {
            playClickSound();
            saveAndClose();
            return;
        }

        // Cancel Button click
        int cancelX1 = startX + 115;
        int cancelY1 = startY + totalHeight - footerHeight + 4;
        int cancelX2 = startX + 185;
        int cancelY2 = startY + totalHeight - 4;
        if (mouseX >= cancelX1 && mouseX <= cancelX2 && mouseY >= cancelY1 && mouseY <= cancelY2) {
            playClickSound();
            closeScreen();
            return;
        }

        // Content Area Interactions
        if (mouseX >= panelX && mouseX <= panelX + panelWidth &&
            mouseY >= panelY && mouseY <= panelY + panelHeight) {

            int drawY = panelY + 4 - (int)scrollY;
            for (ConfigSection.SubSection sub : ConfigSection.getSubSections(currentSection)) {
                // Check header click
                if (mouseX >= panelX + 4 && mouseX <= panelX + panelWidth - 8 &&
                    mouseY >= drawY && mouseY <= drawY + 20) {
                    playClickSound();
                    String id = sub.getId();
                    if (expandedSubSections.contains(id)) {
                        expandedSubSections.remove(id);
                    } else {
                        expandedSubSections.add(id);
                    }
                    return;
                }

                boolean expanded = expandedSubSections.contains(sub.getId());
                drawY += 22;

                if (expanded) {
                    for (ConfigSection.ConfigField field : sub.getFields()) {
                        if (field.getType() == ConfigSection.ConfigField.Type.BOOLEAN) {
                            // Toggle button hit detection
                            int badgeX1 = panelX + panelWidth - 36;
                            int badgeY1 = drawY + 3;
                            int badgeX2 = panelX + panelWidth - 12;
                            int badgeY2 = drawY + 15;

                            if (mouseX >= badgeX1 && mouseX <= badgeX2 &&
                                mouseY >= badgeY1 && mouseY <= badgeY2) {
                                playClickSound();
                                boolean currentVal = getBooleanValue(sub, field);
                                setBooleanValue(sub, field, !currentVal);
                                return;
                            }
                        } else {
                            // Slider hit detection
                            int sliderX1 = panelX + panelWidth - 85;
                            int sliderY1 = drawY + 2;
                            int sliderX2 = panelX + panelWidth - 15;
                            int sliderY2 = drawY + 16;

                            if (mouseX >= sliderX1 && mouseX <= sliderX2 &&
                                mouseY >= sliderY1 && mouseY <= sliderY2) {
                                activeDraggingField = field;
                                activeDraggingSubSection = sub;
                                float ratio = (float)(mouseX - (panelX + panelWidth - 85)) / 70f;
                                ratio = Math.max(0.0f, Math.min(1.0f, ratio));
                                float val = field.getMinValue() + ratio * (field.getMaxValue() - field.getMinValue());
                                if (field.getType() == ConfigSection.ConfigField.Type.INT) {
                                    val = Math.round(val);
                                } else {
                                    val = Math.round(val * 10f) / 10f;
                                }
                                setFloatValue(sub, field, val);
                                return;
                            }
                        }
                        drawY += 20;
                    }
                }
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (clickedMouseButton == 0 && activeDraggingField != null && activeDraggingSubSection != null) {
            int startX = width / 2 - 230;
            int totalWidth = 460;
            int sliderX = startX + 110 + (totalWidth - 110) - 85;

            float ratio = (float) (mouseX - sliderX) / 70f;
            ratio = Math.max(0.0f, Math.min(1.0f, ratio));
            float val = activeDraggingField.getMinValue() + ratio * (activeDraggingField.getMaxValue() - activeDraggingField.getMinValue());
            
            if (activeDraggingField.getType() == ConfigSection.ConfigField.Type.INT) {
                val = Math.round(val);
            } else {
                val = Math.round(val * 10f) / 10f;
            }
            setFloatValue(activeDraggingSubSection, activeDraggingField, val);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            activeDraggingField = null;
            activeDraggingSubSection = null;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = org.lwjgl.input.Mouse.getEventDWheel();
        if (wheel != 0) {
            int direction = wheel > 0 ? -1 : 1;
            targetScrollY += direction * 22;
            if (targetScrollY < 0) targetScrollY = 0;
            if (targetScrollY > maxScrollY) targetScrollY = maxScrollY;
        }
    }

    private void playClickSound() {
        mc.getSoundHandler().playSound(
            net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord(
                net.minecraft.init.SoundEvents.UI_BUTTON_CLICK, 1.0F
            )
        );
    }

    private float getFloatValue(ConfigSection.SubSection sub, ConfigSection.ConfigField field) {
        if (sub == null) return 0;
        String key = field.getKey();

        if (sub.getId().startsWith("warhammer_")) {
            String material = sub.getId().substring("warhammer_".length());
            WarHammerConfig.WarHammerMaterialEntry entry = WarHammerConfig.getMaterial(material);
            if (entry == null) return 0;

            switch (key) {
                case "damage": return entry.data.baseDamage;
                case "speed": return entry.data.attackSpeed;
                case "durability": return entry.data.durability;
                case "skillCooldown": return entry.data.skillCooldown;
                case "aoeRadius": return entry.abilities.aoeRadius;
                case "aoeDamage": return entry.abilities.aoeDamage;
                case "stunDuration": return entry.abilities.stunDuration;
            }
        } else if (sub.getId().startsWith("spikehammer_")) {
            String material = sub.getId().substring("spikehammer_".length());
            SpikeHammerConfig.SpikeHammerMaterialEntry entry = SpikeHammerConfig.getMaterial(material);
            if (entry == null) return 0;

            switch (key) {
                case "damage": return entry.data.baseDamage;
                case "speed": return entry.data.attackSpeed;
                case "durability": return entry.data.durability;
                case "skillCooldown": return entry.data.skillCooldown;
                case "bleedDamage": return entry.bleeding.damagePerLevel;
                case "bleedDuration": return entry.bleeding.baseDuration;
                case "bloodPactRange": return entry.bloodPact.range;
            }
        } else if (sub.getId().startsWith("server_")) {
            switch (key) {
                case "stunDurationMult": return ServerConfig.warhammerStunDurationMultiplier;
                case "bleedDamageMult": return ServerConfig.spikehammerBleedingDamageMultiplier;
                case "bleedDurationMult": return ServerConfig.spikehammerBleedingDurationMultiplier;
                case "bloodPactRangeMult": return ServerConfig.spikehammerBloodPactRangeMultiplier;
                case "bloodPactDrainMult": return ServerConfig.spikehammerBloodPactDrainMultiplier;
            }
        } else if (sub.getId().equals("client_particles")) {
            switch (key) {
                case "aoeCountMultiplier": return ClientConfig.aoeParticleCountMultiplier;
                case "aoeDensityMultiplier": return ClientConfig.aoeParticleDensityMultiplier;
                case "aoeHeightMultiplier": return ClientConfig.aoeParticleHeightMultiplier;
                case "uiOverlayPosition": return ClientConfig.uiOverlayPosition;
            }
        }

        return 0;
    }

    private boolean getBooleanValue(ConfigSection.SubSection sub, ConfigSection.ConfigField field) {
        if (sub == null) return false;
        String key = field.getKey();

        if (sub.getId().startsWith("server_warhammer")) {
            switch (key) {
                case "enableAOE": return ServerConfig.warhammerEnableAOE;
                case "enableStun": return ServerConfig.warhammerEnableStun;
            }
        } else if (sub.getId().startsWith("server_spikehammer")) {
            switch (key) {
                case "enableBleeding": return ServerConfig.spikehammerEnableBleeding;
                case "enableBloodPact": return ServerConfig.spikehammerEnableBloodPact;
            }
        } else if (sub.getId().equals("client_particles")) {
            switch (key) {
                case "aoeEnabled": return ClientConfig.aoeEnabled;
                case "bloodPactEnabled": return ClientConfig.bloodPactEnabled;
                case "bleedingParticles": return ClientConfig.bleedingParticleEnabled;
            }
        }

        return false;
    }

    private void setFloatValue(ConfigSection.SubSection sub, ConfigSection.ConfigField field, float value) {
        if (sub == null) return;
        String key = field.getKey();

        if (sub.getId().startsWith("warhammer_")) {
            String material = sub.getId().substring("warhammer_".length());
            WarHammerConfig.WarHammerMaterialEntry entry = WarHammerConfig.getMaterial(material);
            if (entry == null) return;

            switch (key) {
                case "damage": entry.data.baseDamage = value; break;
                case "speed": entry.data.attackSpeed = value; break;
                case "durability": entry.data.durability = (int) value; break;
                case "skillCooldown": entry.data.skillCooldown = (int) value; break;
                case "aoeRadius": entry.abilities.aoeRadius = value; break;
                case "aoeDamage": entry.abilities.aoeDamage = value; break;
                case "stunDuration": entry.abilities.stunDuration = (int) value; break;
            }
        } else if (sub.getId().startsWith("spikehammer_")) {
            String material = sub.getId().substring("spikehammer_".length());
            SpikeHammerConfig.SpikeHammerMaterialEntry entry = SpikeHammerConfig.getMaterial(material);
            if (entry == null) return;

            switch (key) {
                case "damage": entry.data.baseDamage = value; break;
                case "speed": entry.data.attackSpeed = value; break;
                case "durability": entry.data.durability = (int) value; break;
                case "skillCooldown": entry.data.skillCooldown = (int) value; break;
                case "bleedDamage": entry.bleeding.damagePerLevel = value; break;
                case "bleedDuration": entry.bleeding.baseDuration = (int) value; break;
                case "bloodPactRange": entry.bloodPact.range = value; break;
            }
        } else if (sub.getId().startsWith("server_")) {
            switch (key) {
                case "stunDurationMult": ServerConfig.warhammerStunDurationMultiplier = value; break;
                case "bleedDamageMult": ServerConfig.spikehammerBleedingDamageMultiplier = value; break;
                case "bleedDurationMult": ServerConfig.spikehammerBleedingDurationMultiplier = value; break;
                case "bloodPactRangeMult": ServerConfig.spikehammerBloodPactRangeMultiplier = value; break;
                case "bloodPactDrainMult": ServerConfig.spikehammerBloodPactDrainMultiplier = value; break;
            }
        } else if (sub.getId().equals("client_particles")) {
            switch (key) {
                case "aoeCountMultiplier": ClientConfig.aoeParticleCountMultiplier = value; break;
                case "aoeDensityMultiplier": ClientConfig.aoeParticleDensityMultiplier = value; break;
                case "aoeHeightMultiplier": ClientConfig.aoeParticleHeightMultiplier = value; break;
                case "uiOverlayPosition": ClientConfig.uiOverlayPosition = (int) value; break;
            }
        }
    }

    private void setBooleanValue(ConfigSection.SubSection sub, ConfigSection.ConfigField field, boolean value) {
        if (sub == null) return;
        String key = field.getKey();

        if (sub.getId().startsWith("server_warhammer")) {
            switch (key) {
                case "enableAOE": ServerConfig.warhammerEnableAOE = value; break;
                case "enableStun": ServerConfig.warhammerEnableStun = value; break;
            }
        } else if (sub.getId().startsWith("server_spikehammer")) {
            switch (key) {
                case "enableBleeding": ServerConfig.spikehammerEnableBleeding = value; break;
                case "enableBloodPact": ServerConfig.spikehammerEnableBloodPact = value; break;
            }
        } else if (sub.getId().equals("client_particles")) {
            switch (key) {
                case "aoeEnabled": ClientConfig.aoeEnabled = value; break;
                case "bloodPactEnabled": ClientConfig.bloodPactEnabled = value; break;
                case "bleedingParticles": ClientConfig.bleedingParticleEnabled = value; break;
            }
        }
    }

    private void saveAndClose() {
        ConfigManager.save();
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            ConfigManager.reload();
        }
        closeScreen();
    }

    @Override
    protected void drawBackground(int mouseX, int mouseY, float partialTicks) {
        // No-op to prevent drawing the default missing texture background
    }
}
