package com.x4yi.hammersunbound.client.gui;
import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.x4ui.client.gui.base.GuiBaseScreen;
import com.x4yi.x4ui.client.gui.component.GuiButton;
import com.x4yi.x4ui.client.gui.component.GuiMarkdown;
import com.x4yi.hammersunbound.util.UpdateChecker;
import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.config.ConfigManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class GuiChangelogScreen extends GuiBaseScreen {
    private static final ResourceLocation FLAG_US = new ResourceLocation("hammersunbound", "textures/gui/flag_us.png");
    private static final ResourceLocation FLAG_ES = new ResourceLocation("hammersunbound", "textures/gui/flag_es.png");
    private int selectedReleaseIndex = 0;
    private String currentLanguage;
    private float langPillProgress = 0f;
    private float scrollY = 0;
    private float targetScrollY = 0;
    private int maxScrollY = 0;
    private GuiMarkdown markdownPanel;
    public GuiChangelogScreen(GuiScreen parent) {
        super(parent, "Hammers Unbound - Changelog");
        currentLanguage = ClientConfig.language;
        if (currentLanguage == null || (!currentLanguage.equals("es") && !currentLanguage.equals("en"))) {
            currentLanguage = "es";
        }
    }
    @Override
    protected void drawTitle() {
    }
    @Override
    protected void initComponents() {
        components.clear();
        selectedReleaseIndex = 0;
        if (!UpdateChecker.hasChecked) {
            UpdateChecker.check();
        }
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
        int badgeX1 = startX + totalWidth - 65;
        int badgeY1 = startY + totalHeight - footerHeight + 6;
        int badgeX2 = startX + totalWidth - 8;
        int badgeY2 = startY + totalHeight - 6;
        boolean badgeHovered = mouseX >= badgeX1 && mouseX <= badgeX2 && mouseY >= badgeY1 && mouseY <= badgeY2;
        boolean hasUpdate = UpdateChecker.updateAvailable;

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

        boolean isES = currentLanguage.equals("es");
        mc.getTextureManager().bindTexture(isES ? FLAG_ES : FLAG_US);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawModalRectWithCustomSizedTexture(pillX + 3, pillY + 4, 0, 0, 29, 14, 32, 22);

        drawRect(startX - 2, startY - 2, startX + totalWidth + 2, startY + totalHeight + 2, 0x55000000);
        drawRect(startX, startY, startX + totalWidth, startY + totalHeight, 0xFF0B0B0D);
        drawRect(startX, startY, startX + totalWidth, startY + headerHeight, 0xFF08080A);
        drawRect(startX, startY + headerHeight - 1, startX + totalWidth, startY + headerHeight, 0xFF222228);
        fontRenderer.drawString("Changelog", startX + 8, startY + 7, 0xFFFFFFFF);
        fontRenderer.drawString(HammersUnbound.VERSION, startX + 110, startY + 7, 0xFF707077);
        boolean closeHovered = mouseX >= startX + totalWidth - 18 && mouseX <= startX + totalWidth - 6 &&
                              mouseY >= startY + 4 && mouseY <= startY + 16;
        fontRenderer.drawString("x", startX + totalWidth - 14, startY + 5, closeHovered ? 0xFFFF3D00 : 0xFF888892);
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
        int refX1 = startX + 85;
        int refY1 = startY + totalHeight - footerHeight + 4;
        int refX2 = startX + 155;
        int refY2 = startY + totalHeight - 4;
        boolean refHovered = mouseX >= refX1 && mouseX <= refX2 && mouseY >= refY1 && mouseY <= refY2;
        drawRect(refX1, refY1, refX2, refY2, refHovered ? 0xFF1E88E5 : 0xFF16161E);
        drawBorder(refX1, refY1, refX2, refY2, refHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Refresh", refX1 + 35, refY1 + 5, refHovered ? 0xFFFFFFFF : 0xFFE0E0E6);
        if (UpdateChecker.isChecking) {
            fontRenderer.drawString(TextFormatting.YELLOW + "Connecting...", startX + 165, startY + totalHeight - footerHeight + 8, 0xFFFFFFFF);
        } else if (!UpdateChecker.checkStatus.isEmpty()) {
            fontRenderer.drawString(UpdateChecker.checkStatus, startX + 165, startY + totalHeight - footerHeight + 8, 0xFFFFFFFF);
        }
        int cfgX1 = startX + totalWidth - 78;
        int cfgY1 = startY + totalHeight - footerHeight + 4;
        int cfgX2 = startX + totalWidth - 8;
        int cfgY2 = startY + totalHeight - 4;
        boolean cfgHovered = mouseX >= cfgX1 && mouseX <= cfgX2 && mouseY >= cfgY1 && mouseY <= cfgY2;
        drawRect(cfgX1, cfgY1, cfgX2, cfgY2, cfgHovered ? 0xFF1565C0 : 0xFF16161E);
        drawBorder(cfgX1, cfgY1, cfgX2, cfgY2, cfgHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Config", cfgX1 + 35, cfgY1 + 5, cfgHovered ? 0xFFFFFFFF : 0xFF90CAF9);

        int guideX1 = cfgX1 - 77;
        int guideY1 = startY + totalHeight - footerHeight + 4;
        int guideX2 = cfgX1 - 7;
        int guideY2 = startY + totalHeight - 4;
        boolean guideHovered = mouseX >= guideX1 && mouseX <= guideX2 && mouseY >= guideY1 && mouseY <= guideY2;
        drawRect(guideX1, guideY1, guideX2, guideY2, guideHovered ? 0xFF00796B : 0xFF16161E);
        drawBorder(guideX1, guideY1, guideX2, guideY2, guideHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Guide", guideX1 + 35, guideY1 + 5, guideHovered ? 0xFFFFFFFF : 0xFF4DB6AC);
        if (hasUpdate) {
            int badgeColor = badgeHovered ? 0xFFFF8F00 : 0xFFFFB300;
            drawRect(badgeX1, badgeY1, badgeX2, badgeY2, badgeColor);
            drawCenteredString("+ " + UpdateChecker.latestVersion, badgeX1 + 28, badgeY1 + 3, 0xFF000000);
        }
        int sidebarX = startX;
        int sidebarY1 = startY + headerHeight;
        int sidebarHeight = totalHeight - headerHeight - footerHeight;
        drawRect(sidebarX, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF0F0F12);
        drawRect(sidebarX + sidebarWidth - 1, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF222228);
        int verY = sidebarY1 + 6;
        synchronized (UpdateChecker.cachedReleases) {
            List<UpdateChecker.CachedRelease> cached = UpdateChecker.cachedReleases;
            if (cached.isEmpty()) {
                if (UpdateChecker.isChecking) {
                    fontRenderer.drawString("Loading...", sidebarX + 12, verY + 5, 0xFF888892);
                } else {
                    fontRenderer.drawString("No releases.", sidebarX + 12, verY + 5, 0xFF888892);
                }
            } else {
                for (int i = 0; i < cached.size(); i++) {
                    UpdateChecker.CachedRelease entry = cached.get(i);
                    boolean active = i == selectedReleaseIndex;
                    boolean verHovered = mouseX >= sidebarX + 6 && mouseX <= sidebarX + sidebarWidth - 6 &&
                                         mouseY >= verY && mouseY <= verY + 18;
                    int colorBg = active ? 0xFF18181F : (verHovered ? 0xFF121217 : 0x00000000);
                    if (colorBg != 0) {
                        drawRect(sidebarX + 6, verY, sidebarX + sidebarWidth - 6, verY + 18, colorBg);
                    }
                    if (active) {
                        drawRect(sidebarX + 6, verY, sidebarX + 8, verY + 18, 0xFF00C853);
                    }
                    drawBorder(sidebarX + 6, verY, sidebarX + sidebarWidth - 6, verY + 18, active ? 0xFF00C853 : 0xFF222228);
                    String label = entry.version + (entry.isLocal ? " (Offline)" : "");
                    fontRenderer.drawString(label, sidebarX + 12, verY + 5, active ? 0xFFFFFFFF : (verHovered ? 0xFFE0E0E6 : 0xFF888892));
                    verY += 22;
                }
            }
        }
        int drawY = panelY + 6 - (int)scrollY;
        ScaledResolution sr = new ScaledResolution(mc);
        int scale = sr.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            panelX * scale,
            (height - (panelY + panelHeight)) * scale,
            panelWidth * scale,
            panelHeight * scale
        );
        UpdateChecker.CachedRelease activeRelease = null;
        synchronized (UpdateChecker.cachedReleases) {
            List<UpdateChecker.CachedRelease> cached = UpdateChecker.cachedReleases;
            if (selectedReleaseIndex >= 0 && selectedReleaseIndex < cached.size()) {
                activeRelease = cached.get(selectedReleaseIndex);
            }
        }
        if (activeRelease != null) {
            String changelogText = getChangelogText(activeRelease, currentLanguage);
            if (markdownPanel == null || !changelogText.equals(markdownPanel.getRawText())) {
                markdownPanel = new GuiMarkdown(0, 0, panelWidth - 32, changelogText);
            }
            
            markdownPanel.setX(panelX + 12);
            markdownPanel.setY(drawY);
            markdownPanel.drawComponent(mouseX, mouseY, partialTicks);
            
            maxScrollY = Math.max(0, markdownPanel.getHeight() - panelHeight + 12);
        } else {
            markdownPanel = null;
            if (UpdateChecker.isChecking) {
                fontRenderer.drawString("Fetching from GitHub...", panelX + 20, panelY + 20, 0xFFFFFFFF);
            } else if (!UpdateChecker.checkStatus.contains("Synced")) {
                fontRenderer.drawString("Sync failed. Check connection.", panelX + 20, panelY + 20, 0xFFE53935);
            } else {
                fontRenderer.drawString("No releases loaded.", panelX + 20, panelY + 20, 0xFF888892);
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
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
        if (hasUpdate && badgeHovered) {
            drawCustomTooltip(mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.popMatrix();
    }
    private String getChangelogText(UpdateChecker.CachedRelease entry, String lang) {
        if (entry == null || entry.body == null) return "";
        String body = entry.body;
        String startTag = "[" + lang.toUpperCase() + "]";
        String endTag = "[/" + lang.toUpperCase() + "]";
        if (body.contains(startTag) && body.contains(endTag)) {
            int startIdx = body.indexOf(startTag) + startTag.length();
            int endIdx = body.indexOf(endTag);
            if (endIdx > startIdx) {
                body = body.substring(startIdx, endIdx);
            }
        }
        return body;
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
        int sidebarHeight = totalHeight - headerHeight - footerHeight;
        int yellowBadgeX1 = startX + totalWidth - 65;
        int yellowBadgeY1 = startY + totalHeight - footerHeight + 6;
        int yellowBadgeX2 = startX + totalWidth - 8;
        int yellowBadgeY2 = startY + totalHeight - 6;
        if (mouseX >= yellowBadgeX1 && mouseX <= yellowBadgeX2 &&
            mouseY >= yellowBadgeY1 && mouseY <= yellowBadgeY2 &&
            UpdateChecker.updateAvailable) {
            playClickSound();
            new Thread(() -> {
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(UpdateChecker.latestUrl));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            return;
        }
        if (mouseX >= startX + totalWidth - 18 && mouseX <= startX + totalWidth - 6 &&
            mouseY >= startY + 4 && mouseY <= startY + 16) {
            playClickSound();
            closeScreen();
            return;
        }
        int backX1 = startX + 8;
        int backY1 = startY + totalHeight - footerHeight + 4;
        int backX2 = startX + 78;
        int backY2 = startY + totalHeight - 4;
        if (mouseX >= backX1 && mouseX <= backX2 && mouseY >= backY1 && mouseY <= backY2) {
            playClickSound();
            closeScreen();
            return;
        }
        int refX1 = startX + 85;
        int refY1 = startY + totalHeight - footerHeight + 4;
        int refX2 = startX + 155;
        int refY2 = startY + totalHeight - 4;
        if (mouseX >= refX1 && mouseX <= refX2 && mouseY >= refY1 && mouseY <= refY2) {
            playClickSound();
            UpdateChecker.hasChecked = false;
            UpdateChecker.check();
            return;
        }
        int cfgX1 = startX + totalWidth - 78;
        int cfgY1 = startY + totalHeight - footerHeight + 4;
        int cfgX2 = startX + totalWidth - 8;
        int cfgY2 = startY + totalHeight - 4;
        if (mouseX >= cfgX1 && mouseX <= cfgX2 && mouseY >= cfgY1 && mouseY <= cfgY2) {
            playClickSound();
            mc.displayGuiScreen(new GuiConfigScreen(parentScreen));
            return;
        }

        int guideX1 = cfgX1 - 77;
        int guideY1 = startY + totalHeight - footerHeight + 4;
        int guideX2 = cfgX1 - 7;
        int guideY2 = startY + totalHeight - 4;
        if (mouseX >= guideX1 && mouseX <= guideX2 && mouseY >= guideY1 && mouseY <= guideY2) {
            playClickSound();
            mc.displayGuiScreen(new GuiGuideScreen(parentScreen));
            return;
        }
        int verY = sidebarY1 + 6;
        synchronized (UpdateChecker.cachedReleases) {
            List<UpdateChecker.CachedRelease> cached = UpdateChecker.cachedReleases;
            for (int i = 0; i < cached.size(); i++) {
                if (mouseX >= sidebarX + 6 && mouseX <= sidebarX + sidebarWidth - 6 &&
                    mouseY >= verY && mouseY <= verY + 18) {
                    playClickSound();
                    selectedReleaseIndex = i;
                    targetScrollY = 0;
                    scrollY = 0;
                    return;
                }
                verY += 22;
            }
        }
        int pillWidth = 35;
        int pillHeight = 22;
        int minExtrusion = 8;
        int currentExtrusion = minExtrusion + (int)(langPillProgress * (pillWidth - minExtrusion));
        int pillY = startY + 20;

        if (mouseX >= startX + totalWidth && mouseX <= startX + totalWidth + currentExtrusion &&
            mouseY >= pillY && mouseY <= pillY + pillHeight) {
            playClickSound();
            currentLanguage = currentLanguage.equals("es") ? "en" : "es";
            ClientConfig.language = currentLanguage;
            try { com.x4yi.hammersunbound.config.ConfigManager.save(); } catch (Exception ignored) {}
            targetScrollY = 0;
            scrollY = 0;
            return;
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
    private void drawCustomTooltip(int mouseX, int mouseY) {
        int x = mouseX + 12;
        int y = mouseY - 12;
        String line1 = net.minecraft.util.text.TextFormatting.GREEN + "" + net.minecraft.util.text.TextFormatting.BOLD + "Update Available!";
        String line2 = net.minecraft.util.text.TextFormatting.GRAY + "Version: " + net.minecraft.util.text.TextFormatting.WHITE + UpdateChecker.latestVersion;
        String line3 = net.minecraft.util.text.TextFormatting.GRAY + "Type: " + net.minecraft.util.text.TextFormatting.WHITE + UpdateChecker.latestType;
        String line4 = net.minecraft.util.text.TextFormatting.GRAY + "Link: " + net.minecraft.util.text.TextFormatting.AQUA + "github.com/X4yi/HammersUnbound";
        String line5 = net.minecraft.util.text.TextFormatting.YELLOW + "Click to download!";
        int w1 = fontRenderer.getStringWidth(line1);
        int w2 = fontRenderer.getStringWidth(line2);
        int w3 = fontRenderer.getStringWidth(line3);
        int w4 = fontRenderer.getStringWidth(line4);
        int w5 = fontRenderer.getStringWidth(line5);
        int boxWidth = Math.max(Math.max(Math.max(w1, w2), w3), Math.max(w4, w5)) + 12;
        int boxHeight = 56;
        if (x + boxWidth > width) {
            x = mouseX - boxWidth - 12;
        }
        if (y + boxHeight > height) {
            y = height - boxHeight - 8;
        }
        if (y < 4) {
            y = 4;
        }
        drawRect(x - 1, y - 1, x + boxWidth + 1, y + boxHeight + 1, 0xFF2C2C35);
        drawRect(x, y, x + boxWidth, y + boxHeight, 0xF208080C);
        fontRenderer.drawString(line1, x + 6, y + 5, 0xFFFFFFFF);
        fontRenderer.drawString(line2, x + 6, y + 15, 0xFFFFFFFF);
        fontRenderer.drawString(line3, x + 6, y + 25, 0xFFFFFFFF);
        fontRenderer.drawString(line4, x + 6, y + 35, 0xFFFFFFFF);
        fontRenderer.drawString(line5, x + 6, y + 45, 0xFFFFFFFF);
    }
    @Override
    protected void drawBackground(int mouseX, int mouseY, float partialTicks) {
    }
}