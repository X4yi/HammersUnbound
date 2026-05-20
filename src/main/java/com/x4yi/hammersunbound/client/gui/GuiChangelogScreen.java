package com.x4yi.hammersunbound.client.gui;

import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.client.gui.base.GuiBaseScreen;
import com.x4yi.hammersunbound.util.UpdateChecker;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiChangelogScreen extends GuiBaseScreen {

    private int selectedReleaseIndex = 0;
    private String currentLanguage = "es"; // Default to Spanish as requested

    private int scrollY = 0;
    private int maxScrollY = 0;

    public GuiChangelogScreen(GuiScreen parent) {
        super(parent, "Hammers Unbound - Changelog");
    }

    @Override
    protected void drawTitle() {
        // No-op to prevent the default centered title from drawing
    }

    @Override
    protected void initComponents() {
        components.clear();
        selectedReleaseIndex = 0;

        // Fetch online changelogs from GitHub API asynchronously on load
        if (!UpdateChecker.hasChecked) {
            UpdateChecker.check();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        updateAnimation();

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

        // Precompute badge hover and update state for drawing and tooltip
        int badgeX1 = startX + totalWidth - 65;
        int badgeY1 = startY + totalHeight - footerHeight + 6;
        int badgeX2 = startX + totalWidth - 8;
        int badgeY2 = startY + totalHeight - 6;
        boolean badgeHovered = mouseX >= badgeX1 && mouseX <= badgeX2 && mouseY >= badgeY1 && mouseY <= badgeY2;
        boolean hasUpdate = UpdateChecker.updateAvailable;

        // Draw main body background
        drawRect(startX - 2, startY - 2, startX + totalWidth + 2, startY + totalHeight + 2, 0x55000000);
        drawRect(startX, startY, startX + totalWidth, startY + totalHeight, 0xFF0B0B0D);

        // Header Background & Border
        drawRect(startX, startY, startX + totalWidth, startY + headerHeight, 0xFF08080A);
        drawRect(startX, startY + headerHeight - 1, startX + totalWidth, startY + headerHeight, 0xFF222228);

        // Header branding
        fontRenderer.drawString("Changelog Reader", startX + 8, startY + 7, 0xFFFFFFFF);
        fontRenderer.drawString(HammersUnbound.VERSION, startX + 110, startY + 7, 0xFF707077);

        // Close Button 'X'
        boolean closeHovered = mouseX >= startX + totalWidth - 18 && mouseX <= startX + totalWidth - 6 &&
                              mouseY >= startY + 4 && mouseY <= startY + 16;
        fontRenderer.drawString("x", startX + totalWidth - 14, startY + 5, closeHovered ? 0xFFFF3D00 : 0xFF888892);

        // Footer Background & Border
        drawRect(startX, startY + totalHeight - footerHeight, startX + totalWidth, startY + totalHeight, 0xFF08080A);
        drawRect(startX, startY + totalHeight - footerHeight, startX + totalWidth, startY + totalHeight - footerHeight + 1, 0xFF222228);

        // Footer Buttons: Back Button
        int backX1 = startX + 8;
        int backY1 = startY + totalHeight - footerHeight + 4;
        int backX2 = startX + 78;
        int backY2 = startY + totalHeight - 4;
        boolean backHovered = mouseX >= backX1 && mouseX <= backX2 && mouseY >= backY1 && mouseY <= backY2;
        drawRect(backX1, backY1, backX2, backY2, backHovered ? 0xFF00C853 : 0xFF16161E);
        drawBorder(backX1, backY1, backX2, backY2, backHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Back", backX1 + 35, backY1 + 5, backHovered ? 0xFFFFFFFF : 0xFFE0E0E6);

        // Footer Buttons: Refresh Button
        int refX1 = startX + 85;
        int refY1 = startY + totalHeight - footerHeight + 4;
        int refX2 = startX + 155;
        int refY2 = startY + totalHeight - 4;
        boolean refHovered = mouseX >= refX1 && mouseX <= refX2 && mouseY >= refY1 && mouseY <= refY2;
        drawRect(refX1, refY1, refX2, refY2, refHovered ? 0xFF1E88E5 : 0xFF16161E);
        drawBorder(refX1, refY1, refX2, refY2, refHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Refresh", refX1 + 35, refY1 + 5, refHovered ? 0xFFFFFFFF : 0xFFE0E0E6);

        // Status Label (online connection feedback)
        if (UpdateChecker.isChecking) {
            fontRenderer.drawString(TextFormatting.YELLOW + "Connecting...", startX + 165, startY + totalHeight - footerHeight + 8, 0xFFFFFFFF);
        } else if (!UpdateChecker.checkStatus.isEmpty()) {
            fontRenderer.drawString(UpdateChecker.checkStatus, startX + 165, startY + totalHeight - footerHeight + 8, 0xFFFFFFFF);
        }

        // Yellow Branding Badge in Bottom Right (only draw if there is actually a new update available!)
        if (hasUpdate) {
            int badgeColor = badgeHovered ? 0xFFFF8F00 : 0xFFFFB300;
            drawRect(badgeX1, badgeY1, badgeX2, badgeY2, badgeColor);
            drawCenteredString("+ " + UpdateChecker.latestVersion, badgeX1 + 28, badgeY1 + 3, 0xFF000000);
        }

        // Sidebar Background
        int sidebarX = startX;
        int sidebarY1 = startY + headerHeight;
        int sidebarHeight = totalHeight - headerHeight - footerHeight;
        drawRect(sidebarX, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF0F0F12);
        drawRect(sidebarX + sidebarWidth - 1, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF222228);

        // Sidebar: Version Buttons List
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

        // Sidebar: Language selection badges at the bottom of the sidebar
        int langES_X1 = sidebarX + 15;
        int langES_X2 = sidebarX + 50;
        int langEN_X1 = sidebarX + 70;
        int langEN_X2 = sidebarX + 105;
        int langY1 = sidebarY1 + sidebarHeight - 24;
        int langY2 = sidebarY1 + sidebarHeight - 8;

        boolean langESHovered = mouseX >= langES_X1 && mouseX <= langES_X2 && mouseY >= langY1 && mouseY <= langY2;
        boolean langENHovered = mouseX >= langEN_X1 && mouseX <= langEN_X2 && mouseY >= langY1 && mouseY <= langY2;

        boolean isES = currentLanguage.equals("es");
        drawRect(langES_X1, langY1, langES_X2, langY2, isES ? 0xFF00C853 : (langESHovered ? 0xFF2C2C36 : 0xFF1E1E24));
        drawBorder(langES_X1, langY1, langES_X2, langY2, isES ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("ES", langES_X1 + 17, langY1 + 5, isES ? 0xFF000000 : 0xFFE0E0E6);

        boolean isEN = currentLanguage.equals("en");
        drawRect(langEN_X1, langY1, langEN_X2, langY2, isEN ? 0xFF00C853 : (langENHovered ? 0xFF2C2C36 : 0xFF1E1E24));
        drawBorder(langEN_X1, langY1, langEN_X2, langY2, isEN ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("EN", langEN_X1 + 17, langY1 + 5, isEN ? 0xFF000000 : 0xFFE0E0E6);


        // Right Panel: Scrollable viewport using scissor test
        int drawY = panelY + 6 - scrollY;

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
            List<String> changelogLines = getChangelogLines(activeRelease, currentLanguage);
            for (String line : changelogLines) {
                int heightDrawn = drawWrappedMarkdown(line, panelX + 12, drawY, panelWidth - 32, false);
                drawY += heightDrawn;
            }
            int totalHeightContent = drawY - (panelY + 6 - scrollY);
            maxScrollY = Math.max(0, totalHeightContent - panelHeight + 12);
        } else {
            if (UpdateChecker.isChecking) {
                fontRenderer.drawString("Fetching from GitHub...", panelX + 20, panelY + 20, 0xFFFFFFFF);
            } else if (!UpdateChecker.checkStatus.contains("Synced")) {
                fontRenderer.drawString("Sync failed. Check connection.", panelX + 20, panelY + 20, 0xFFE53935);
            } else {
                fontRenderer.drawString("No releases loaded.", panelX + 20, panelY + 20, 0xFF888892);
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
            int thumbHeight = Math.max(20, (int) ((float) panelHeight / (maxScrollY + panelHeight) * trackHeight));
            int thumbY = trackY + (int) (scrollRatio * (trackHeight - thumbHeight));
            drawRect(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xFF424248);
        }

        // Custom tooltip for version badge when update is available and hovered (custom-drawn ClickGUI style)
        if (hasUpdate && badgeHovered) {
            drawCustomTooltip(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private int drawWrappedMarkdown(String line, int x, int y, int wrapWidth, boolean isBullet) {
        if (line.trim().isEmpty()) return 6;

        int startX = x;
        int currX = x;
        int currY = y;
        int lineHeight = 10;

        // Custom curated header styles & colors
        if (line.startsWith("# ")) {
            String text = line.substring(2);
            fontRenderer.drawString(TextFormatting.BOLD + text, currX, currY, 0xFF00E5FF); // Vibrant Cyan
            return 14;
        } else if (line.startsWith("## ")) {
            String text = line.substring(3);
            fontRenderer.drawString(TextFormatting.BOLD + text, currX, currY, 0xFF00E676); // Lime Green
            return 12;
        } else if (line.startsWith("### ")) {
            String text = line.substring(4);
            fontRenderer.drawString(TextFormatting.BOLD + text, currX, currY, 0xFFFFD600); // Yellow
            return 10;
        }

        boolean bullet = isBullet || line.startsWith("- ") || line.startsWith("* ");
        String text = line;
        if (line.startsWith("- ") || line.startsWith("* ")) {
            text = line.substring(2);
        }

        if (bullet) {
            // Draw a cute colored square bullet point
            drawRect(currX, currY + 3, currX + 4, currY + 7, 0xFF00C853);
            currX += 10;
            startX += 10;
        }

        // Inline Bold parser split
        String[] parts = text.split("(?=\\*\\*)|(?<=\\*\\*)");
        boolean bold = false;

        for (String part : parts) {
            if (part.equals("**")) {
                bold = !bold;
                continue;
            }

            // Word wrap words to preserve layout
            String[] words = part.split(" ");
            for (int i = 0; i < words.length; i++) {
                String word = words[i] + (i < words.length - 1 ? " " : "");
                String formattedWord = (bold ? TextFormatting.BOLD : "") + word;
                int wordWidth = fontRenderer.getStringWidth(formattedWord);

                if (currX + wordWidth > x + wrapWidth) {
                    currX = startX;
                    currY += lineHeight;
                }

                fontRenderer.drawString(formattedWord, currX, currY, 0xFFE0E0E6);
                currX += wordWidth;
            }
        }

        return (currY - y) + lineHeight + 2;
    }

    private List<String> getChangelogLines(UpdateChecker.CachedRelease entry, String lang) {
        List<String> lines = new ArrayList<>();
        if (entry == null || entry.body == null) return lines;

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

        String[] split = body.split("\r?\n");
        for (String s : split) {
            lines.add(s);
        }
        return lines;
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

        // Yellow Badge click for updates
        int yellowBadgeX1 = startX + totalWidth - 65;
        int yellowBadgeY1 = startY + totalHeight - footerHeight + 6;
        int yellowBadgeX2 = startX + totalWidth - 8;
        int yellowBadgeY2 = startY + totalHeight - 6;
        if (mouseX >= yellowBadgeX1 && mouseX <= yellowBadgeX2 &&
            mouseY >= yellowBadgeY1 && mouseY <= yellowBadgeY2 &&
            UpdateChecker.updateAvailable) {
            playClickSound();
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(UpdateChecker.latestUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // Close button 'X' click
        if (mouseX >= startX + totalWidth - 18 && mouseX <= startX + totalWidth - 6 &&
            mouseY >= startY + 4 && mouseY <= startY + 16) {
            playClickSound();
            closeScreen();
            return;
        }

        // Back button click
        int backX1 = startX + 8;
        int backY1 = startY + totalHeight - footerHeight + 4;
        int backX2 = startX + 78;
        int backY2 = startY + totalHeight - 4;
        if (mouseX >= backX1 && mouseX <= backX2 && mouseY >= backY1 && mouseY <= backY2) {
            playClickSound();
            closeScreen();
            return;
        }

        // Refresh button click
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

        // Sidebar version clicks
        int verY = sidebarY1 + 6;
        synchronized (UpdateChecker.cachedReleases) {
            List<UpdateChecker.CachedRelease> cached = UpdateChecker.cachedReleases;
            for (int i = 0; i < cached.size(); i++) {
                if (mouseX >= sidebarX + 6 && mouseX <= sidebarX + sidebarWidth - 6 &&
                    mouseY >= verY && mouseY <= verY + 18) {
                    playClickSound();
                    selectedReleaseIndex = i;
                    scrollY = 0;
                    return;
                }
                verY += 22;
            }
        }

        // Sidebar Language badges clicks
        int langES_X1 = sidebarX + 15;
        int langES_X2 = sidebarX + 50;
        int langEN_X1 = sidebarX + 70;
        int langEN_X2 = sidebarX + 105;
        int langY1 = sidebarY1 + sidebarHeight - 24;
        int langY2 = sidebarY1 + sidebarHeight - 8;

        if (mouseX >= langES_X1 && mouseX <= langES_X2 && mouseY >= langY1 && mouseY <= langY2) {
            if (!currentLanguage.equals("es")) {
                playClickSound();
                currentLanguage = "es";
                scrollY = 0;
            }
            return;
        }

        if (mouseX >= langEN_X1 && mouseX <= langEN_X2 && mouseY >= langY1 && mouseY <= langY2) {
            if (!currentLanguage.equals("en")) {
                playClickSound();
                currentLanguage = "en";
                scrollY = 0;
            }
            return;
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = org.lwjgl.input.Mouse.getEventDWheel();
        if (wheel != 0) {
            int direction = wheel > 0 ? -1 : 1;
            scrollY += direction * 14;
            if (scrollY < 0) scrollY = 0;
            if (scrollY > maxScrollY) scrollY = maxScrollY;
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
        // No-op to prevent drawing the default missing background texture
    }
}
