package com.x4yi.hammersunbound.client.gui;

import com.x4yi.x4ui.client.gui.base.GuiBaseScreen;
import com.x4yi.hammersunbound.client.gui.util.MarkdownRenderer;
import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.config.ConfigManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiGuideScreen extends GuiBaseScreen {

    private enum GuideSection {
        WARHAMMER("WarHammer", "warhammer"),
        SPIKEHAMMER("SpikeHammer", "spikehammer"),
        CONFIGURATION("Configuration", "configuration");

        public final String displayName;
        public final String fileId;

        GuideSection(String displayName, String fileId) {
            this.displayName = displayName;
            this.fileId = fileId;
        }
    }

    private static final ResourceLocation FLAG_US = new ResourceLocation("hammersunbound", "textures/gui/flag_us.png");
    private static final ResourceLocation FLAG_ES = new ResourceLocation("hammersunbound", "textures/gui/flag_es.png");
    private float langPillProgress = 0f;

    private GuideSection currentSection = GuideSection.WARHAMMER;
    private final Map<GuideSection, List<String>> cachedLines = new HashMap<>();
    
    private float scrollY = 0;
    private float targetScrollY = 0;
    private int maxScrollY = 0;

    public GuiGuideScreen(GuiScreen parent) {
        super(parent, "Hammers Unbound - Guide");
    }

    @Override
    protected void drawTitle() {
        // Title drawn manually in drawScreen
    }

    @Override
    protected void initComponents() {
        components.clear();
        loadSection(currentSection);
    }

    private void loadSection(GuideSection section) {
        if (cachedLines.containsKey(section)) return;
        
        List<String> lines = new ArrayList<>();
        String lang = ClientConfig.language.toUpperCase();
        String path = "assets/hammersunbound/guide/" + section.fileId + ".md";
        
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                
                String body = sb.toString();
                String startTag = "[" + lang + "]";
                String endTag = "[/" + lang + "]";
                
                if (body.contains(startTag) && body.contains(endTag)) {
                    int startIdx = body.indexOf(startTag) + startTag.length();
                    int endIdx = body.indexOf(endTag);
                    if (endIdx > startIdx) {
                        body = body.substring(startIdx, endIdx);
                    }
                } else if (body.contains("[EN]") && body.contains("[/EN]")) { // Fallback to English
                     int startIdx = body.indexOf("[EN]") + 4;
                     int endIdx = body.indexOf("[/EN]");
                     if (endIdx > startIdx) {
                         body = body.substring(startIdx, endIdx);
                     }
                }
                
                String[] split = body.split("\r?\n");
                for (String s : split) {
                    lines.add(s);
                }
            } else {
                lines.add("Error: Could not find guide file for " + section.displayName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            lines.add("Error loading guide: " + e.getMessage());
        }
        
        cachedLines.put(section, lines);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        updateAnimation();

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
        }

        int startX = width / 2 - 240;
        int startY = height / 2 - 130;
        int totalWidth = 480;
        int totalHeight = 260;
        int headerHeight = 22;
        int footerHeight = 26;
        int sidebarWidth = 120;

        int panelX = startX + sidebarWidth;
        int panelY = startY + headerHeight;
        int panelWidth = totalWidth - sidebarWidth;
        int panelHeight = totalHeight - headerHeight - footerHeight;

        int pillWidth = 35;
        int pillHeight = 22;
        int maxExtrusion = pillWidth;
        int minExtrusion = 8;
        int currentExtrusion = minExtrusion + (int)(langPillProgress * (maxExtrusion - minExtrusion));
        int pillX = startX + totalWidth + currentExtrusion - pillWidth;
        int pillY = startY + 20;

        boolean langHovered = mouseX >= startX + totalWidth && mouseX <= startX + totalWidth + currentExtrusion &&
                              mouseY >= pillY && mouseY <= pillY + pillHeight;

        langPillProgress += (langHovered ? 0.15f : -0.15f);
        if (langPillProgress > 1f) langPillProgress = 1f;
        if (langPillProgress < 0f) langPillProgress = 0f;

        drawRect(pillX, pillY, pillX + pillWidth, pillY + pillHeight, langHovered ? 0xFF2C2C36 : 0xFF1E1E24);
        drawBorder(pillX, pillY, pillX + pillWidth, pillY + pillHeight, 0xFF2C2C36);

        boolean isES = ClientConfig.language.equals("es");
        mc.getTextureManager().bindTexture(isES ? FLAG_ES : FLAG_US);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawModalRectWithCustomSizedTexture(pillX + 3, pillY + 4, 0, 0, 29, 14, 32, 22);

        // Backgrounds
        drawRect(startX - 2, startY - 2, startX + totalWidth + 2, startY + totalHeight + 2, 0x55000000);
        drawRect(startX, startY, startX + totalWidth, startY + totalHeight, 0xFF0B0B0D);
        drawRect(startX, startY, startX + totalWidth, startY + headerHeight, 0xFF08080A);
        drawRect(startX, startY + headerHeight - 1, startX + totalWidth, startY + headerHeight, 0xFF222228);

        // Header
        fontRenderer.drawString("Hammers Unbound - Guide", startX + 8, startY + 7, 0xFFFFFFFF);
        
        boolean closeHovered = mouseX >= startX + totalWidth - 18 && mouseX <= startX + totalWidth - 6 &&
                              mouseY >= startY + 4 && mouseY <= startY + 16;
        fontRenderer.drawString("x", startX + totalWidth - 14, startY + 5, closeHovered ? 0xFFFF3D00 : 0xFF888892);

        // Footer
        drawRect(startX, startY + totalHeight - footerHeight, startX + totalWidth, startY + totalHeight, 0xFF08080A);
        drawRect(startX, startY + totalHeight - footerHeight, startX + totalWidth, startY + totalHeight - footerHeight + 1, 0xFF222228);

        int backX1 = startX + 8;
        int backY1 = startY + totalHeight - footerHeight + 4;
        int backX2 = startX + 78;
        int backY2 = startY + totalHeight - 4;
        boolean backHovered = mouseX >= backX1 && mouseX <= backX2 && mouseY >= backY1 && mouseY <= backY2;
        drawRect(backX1, backY1, backX2, backY2, backHovered ? 0xFF00C853 : 0xFF16161E);
        drawBorder(backX1, backY1, backX2, backY2, backHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Back", backX1 + 35, backY1 + 5, backHovered ? 0xFFFFFFFF : 0xFFE0E0E6);

        int clX1 = startX + 85;
        int clY1 = startY + totalHeight - footerHeight + 4;
        int clX2 = startX + 155;
        int clY2 = startY + totalHeight - 4;
        boolean clHovered = mouseX >= clX1 && mouseX <= clX2 && mouseY >= clY1 && mouseY <= clY2;
        drawRect(clX1, clY1, clX2, clY2, clHovered ? 0xFF6A1B9A : 0xFF16161E);
        drawBorder(clX1, clY1, clX2, clY2, clHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Changelog", clX1 + 35, clY1 + 5, clHovered ? 0xFFFFFFFF : 0xFFB580D8);

        int cfgX1 = startX + totalWidth - 78;
        int cfgY1 = startY + totalHeight - footerHeight + 4;
        int cfgX2 = startX + totalWidth - 8;
        int cfgY2 = startY + totalHeight - 4;
        boolean cfgHovered = mouseX >= cfgX1 && mouseX <= cfgX2 && mouseY >= cfgY1 && mouseY <= cfgY2;
        drawRect(cfgX1, cfgY1, cfgX2, cfgY2, cfgHovered ? 0xFF1565C0 : 0xFF16161E);
        drawBorder(cfgX1, cfgY1, cfgX2, cfgY2, cfgHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Config", cfgX1 + 35, cfgY1 + 5, cfgHovered ? 0xFFFFFFFF : 0xFF90CAF9);

        // Sidebar
        int sidebarX = startX;
        int sidebarY1 = startY + headerHeight;
        int sidebarHeight = totalHeight - headerHeight - footerHeight;
        
        drawRect(sidebarX, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF0F0F12);
        drawRect(sidebarX + sidebarWidth - 1, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF222228);

        int secY = sidebarY1 + 6;
        for (GuideSection section : GuideSection.values()) {
            boolean active = section == currentSection;
            boolean secHovered = mouseX >= sidebarX && mouseX <= sidebarX + sidebarWidth &&
                                 mouseY >= secY && mouseY <= secY + 22;
            
            if (active) {
                drawRect(sidebarX, secY, sidebarX + sidebarWidth - 1, secY + 22, 0xFF18181F);
                drawRect(sidebarX, secY, sidebarX + 2, secY + 22, 0xFF00C853);
                fontRenderer.drawString(section.displayName, sidebarX + 12, secY + 7, 0xFFFFFFFF);
            } else {
                if (secHovered) {
                    drawRect(sidebarX, secY, sidebarX + sidebarWidth - 1, secY + 22, 0xFF121217);
                }
                fontRenderer.drawString(section.displayName, sidebarX + 12, secY + 7, secHovered ? 0xFFE0E0E6 : 0xFF888892);
            }
            secY += 24;
        }

        // Main Panel (Scissor)
        drawRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF070708);
        
        ScaledResolution sr = new ScaledResolution(mc);
        int scale = sr.getScaleFactor();
        int scissorX = panelX * scale;
        int scissorY = mc.displayHeight - (panelY + panelHeight) * scale;
        int scissorW = panelWidth * scale;
        int scissorH = panelHeight * scale;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);

        List<String> lines = cachedLines.get(currentSection);
        if (lines != null && !lines.isEmpty()) {
            int drawY = panelY + 10 - (int) scrollY;
            int drawX = panelX + 15;
            int wrapWidth = panelWidth - 30;

            for (String line : lines) {
                int heightDrawn = MarkdownRenderer.drawWrappedMarkdown(fontRenderer, line, drawX, drawY, wrapWidth, 0xFFE0E0E6);
                drawY += heightDrawn;
            }
            int totalHeightContent = drawY - (panelY + 10 - (int) scrollY);
            maxScrollY = Math.max(0, totalHeightContent - panelHeight + 12);
        } else {
            fontRenderer.drawString("Loading...", panelX + 20, panelY + 20, 0xFF888892);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Scrollbar
        if (maxScrollY > 0) {
            int trackX = panelX + panelWidth - 5;
            int trackY = panelY + 2;
            int trackHeight = panelHeight - 4;
            
            drawRect(trackX, trackY, trackX + 2, trackY + trackHeight, 0xFF121215);
            
            float scrollRatio = (float) scrollY / maxScrollY;
            int thumbHeight = Math.max(20, (int) ((float) panelHeight / (maxScrollY + panelHeight) * trackHeight));
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

        int startX = width / 2 - 240;
        int startY = height / 2 - 130;
        int totalWidth = 480;
        int totalHeight = 260;
        int headerHeight = 22;
        int footerHeight = 26;
        int sidebarWidth = 120;
        
        int sidebarX = startX;
        int sidebarY1 = startY + headerHeight;

        int pillWidth = 35;
        int pillHeight = 22;
        int minExtrusion = 8;
        int currentExtrusion = minExtrusion + (int)(langPillProgress * (pillWidth - minExtrusion));
        int pillY = startY + 20;

        if (mouseX >= startX + totalWidth && mouseX <= startX + totalWidth + currentExtrusion &&
            mouseY >= pillY && mouseY <= pillY + pillHeight) {
            playClickSound();
            ClientConfig.language = ClientConfig.language.equals("es") ? "en" : "es";
            try { ConfigManager.save(); } catch (Exception ignored) {}
            cachedLines.clear();
            loadSection(currentSection);
            return;
        }

        // Close button
        if (mouseX >= startX + totalWidth - 18 && mouseX <= startX + totalWidth - 6 &&
            mouseY >= startY + 4 && mouseY <= startY + 16) {
            playClickSound();
            closeScreen();
            return;
        }

        // Back button
        int backX1 = startX + 8;
        int backY1 = startY + totalHeight - footerHeight + 4;
        int backX2 = startX + 78;
        int backY2 = startY + totalHeight - 4;
        if (mouseX >= backX1 && mouseX <= backX2 && mouseY >= backY1 && mouseY <= backY2) {
            playClickSound();
            closeScreen();
            return;
        }

        // Changelog button
        int clX1 = startX + 85;
        int clY1 = startY + totalHeight - footerHeight + 4;
        int clX2 = startX + 155;
        int clY2 = startY + totalHeight - 4;
        if (mouseX >= clX1 && mouseX <= clX2 && mouseY >= clY1 && mouseY <= clY2) {
            playClickSound();
            mc.displayGuiScreen(new GuiChangelogScreen(parentScreen));
            return;
        }

        // Config button
        int cfgX1 = startX + totalWidth - 78;
        int cfgY1 = startY + totalHeight - footerHeight + 4;
        int cfgX2 = startX + totalWidth - 8;
        int cfgY2 = startY + totalHeight - 4;
        if (mouseX >= cfgX1 && mouseX <= cfgX2 && mouseY >= cfgY1 && mouseY <= cfgY2) {
            playClickSound();
            mc.displayGuiScreen(new GuiConfigScreen(parentScreen));
            return;
        }

        // Sidebar clicks
        int secY = sidebarY1 + 6;
        for (GuideSection section : GuideSection.values()) {
            if (mouseX >= sidebarX && mouseX <= sidebarX + sidebarWidth &&
                mouseY >= secY && mouseY <= secY + 22) {
                if (currentSection != section) {
                    playClickSound();
                    currentSection = section;
                    scrollY = 0;
                    targetScrollY = 0;
                    loadSection(section);
                }
                return;
            }
            secY += 24;
        }
    }

    private void playClickSound() {
        mc.getSoundHandler().playSound(net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord(net.minecraft.init.SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int direction = wheel > 0 ? -1 : 1;
            targetScrollY += direction * 30;
            if (targetScrollY < 0) targetScrollY = 0;
            if (targetScrollY > maxScrollY) targetScrollY = maxScrollY;
        }
    }
}
