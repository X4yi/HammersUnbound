package com.x4yi.hammersunbound.client.gui;

import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.client.gui.base.GuiBaseScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GuiChangelogScreen extends GuiBaseScreen {

    private static class ReleaseEntry {
        public final String version;
        public final String body;
        public final boolean isLocal;

        public ReleaseEntry(String version, String body, boolean isLocal) {
            this.version = version;
            this.body = body;
            this.isLocal = isLocal;
        }
    }

    private final List<ReleaseEntry> releases = new ArrayList<>();
    private int selectedReleaseIndex = 0;
    private String currentLanguage = "es"; // Default to Spanish as requested

    private int scrollY = 0;
    private int maxScrollY = 0;
    private boolean isLoading = false;
    private String statusMessage = "";

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
        releases.clear();

        // 1. Load local changelog first so it is immediately available (instant offline mode)
        String localChangelogBody = readLocalChangelog();
        if (!localChangelogBody.isEmpty()) {
            releases.add(new ReleaseEntry(HammersUnbound.VERSION, localChangelogBody, true));
        } else {
            // Fallback empty local entry in case resource reading fails
            releases.add(new ReleaseEntry(HammersUnbound.VERSION, "[ES]\n# Registro de Cambios\nCargando datos...\n[/ES]\n[EN]\n# Changelog\nLoading data...\n[/EN]", true));
        }

        selectedReleaseIndex = 0;

        // 2. Load online changelogs from GitHub API asynchronously (prevent client freezing)
        fetchOnlineReleases();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        updateAnimation();
        drawDefaultBackground();

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
        drawRect(startX, startY + totalHeight - footerHeight, startX + totalWidth, startY + totalHeight, 0xFF0E0E12);
        drawRect(startX, startY + totalHeight - footerHeight, startX + totalWidth, startY + totalHeight - footerHeight + 1, 0xFF222228);

        // Footer Back Button
        int backX1 = startX + 8;
        int backY1 = startY + totalHeight - footerHeight + 4;
        int backX2 = startX + 78;
        int backY2 = startY + totalHeight - 4;
        boolean backHovered = mouseX >= backX1 && mouseX <= backX2 && mouseY >= backY1 && mouseY <= backY2;
        drawRect(backX1, backY1, backX2, backY2, backHovered ? 0xFF6A1B9A : 0xFF16161E);
        drawBorder(backX1, backY1, backX2, backY2, backHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Back", backX1 + 35, backY1 + 5, backHovered ? 0xFFFFFFFF : 0xFFB580D8);

        // Footer Refresh Button (Online update)
        int refX1 = startX + 85;
        int refY1 = startY + totalHeight - footerHeight + 4;
        int refX2 = startX + 155;
        int refY2 = startY + totalHeight - 4;
        boolean refHovered = mouseX >= refX1 && mouseX <= refX2 && mouseY >= refY1 && mouseY <= refY2;
        drawRect(refX1, refY1, refX2, refY2, refHovered ? 0xFF00C853 : 0xFF16161E);
        drawBorder(refX1, refY1, refX2, refY2, refHovered ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("Refresh", refX1 + 35, refY1 + 5, refHovered ? 0xFFFFFFFF : 0xFF00C853);

        // Status Label (online connection feedback)
        if (isLoading) {
            fontRenderer.drawString(TextFormatting.YELLOW + "Connecting...", startX + 165, startY + totalHeight - footerHeight + 8, 0xFFFFFFFF);
        } else if (!statusMessage.isEmpty()) {
            fontRenderer.drawString(statusMessage, startX + 165, startY + totalHeight - footerHeight + 8, 0xFFFFFFFF);
        }

        // Yellow Branding Badge in Bottom Right
        int badgeX1 = startX + totalWidth - 65;
        int badgeY1 = startY + totalHeight - footerHeight + 6;
        int badgeX2 = startX + totalWidth - 8;
        int badgeY2 = startY + totalHeight - 6;
        drawRect(badgeX1, badgeY1, badgeX2, badgeY2, 0xFFFFD600);
        drawCenteredString("+ " + HammersUnbound.VERSION, badgeX1 + 28, badgeY1 + 3, 0xFF000000);

        // Sidebar Background
        int sidebarX = startX;
        int sidebarY1 = startY + headerHeight;
        int sidebarHeight = totalHeight - headerHeight - footerHeight;
        drawRect(sidebarX, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF0F0F12);
        drawRect(sidebarX + sidebarWidth - 1, sidebarY1, sidebarX + sidebarWidth, sidebarY1 + sidebarHeight, 0xFF222228);

        // Sidebar: Version Buttons List
        int verY = sidebarY1 + 6;
        synchronized (releases) {
            for (int i = 0; i < releases.size(); i++) {
                ReleaseEntry entry = releases.get(i);
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

        // Sidebar Bottom: Languages Selector (ES / EN)
        int langES_X1 = sidebarX + 15;
        int langES_X2 = sidebarX + 50;
        int langEN_X1 = sidebarX + 70;
        int langEN_X2 = sidebarX + 105;
        int langY1 = sidebarY1 + sidebarHeight - 24;
        int langY2 = sidebarY1 + sidebarHeight - 8;

        boolean langESHovered = mouseX >= langES_X1 && mouseX <= langES_X2 && mouseY >= langY1 && mouseY <= langY2;
        boolean langENHovered = mouseX >= langEN_X1 && mouseX <= langEN_X2 && mouseY >= langY1 && mouseY <= langY2;

        boolean isES = currentLanguage.equals("es");
        boolean isEN = currentLanguage.equals("en");

        // Draw ES Badge
        drawRect(langES_X1, langY1, langES_X2, langY2, isES ? 0xFF00C853 : (langESHovered ? 0xFF2A2A30 : 0xFF1A1A22));
        drawBorder(langES_X1, langY1, langES_X2, langY2, isES ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("ES", langES_X1 + 17, langY1 + 3, isES ? 0xFFFFFFFF : 0xFF888892);

        // Draw EN Badge
        drawRect(langEN_X1, langY1, langEN_X2, langY2, isEN ? 0xFF00C853 : (langENHovered ? 0xFF2A2A30 : 0xFF1A1A22));
        drawBorder(langEN_X1, langY1, langEN_X2, langY2, isEN ? 0xFFFFFFFF : 0xFF2C2C36);
        drawCenteredString("EN", langEN_X1 + 17, langY1 + 3, isEN ? 0xFFFFFFFF : 0xFF888892);

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

        int drawY = panelY + 6 - scrollY;
        ReleaseEntry activeRelease = null;
        synchronized (releases) {
            if (selectedReleaseIndex >= 0 && selectedReleaseIndex < releases.size()) {
                activeRelease = releases.get(selectedReleaseIndex);
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
            fontRenderer.drawString("No release loaded.", panelX + 20, panelY + 20, 0xFF888892);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Scrollbar Indicator
        if (maxScrollY > 0) {
            int trackX = panelX + panelWidth - 5;
            int trackY = panelY + 2;
            int trackHeight = panelHeight - 4;
            drawRect(trackX, trackY, trackX + 2, trackY + trackHeight, 0xFF121215);

            float scrollRatio = (float) scrollY / maxScrollY;
            int totalHeightVal = maxScrollY + panelHeight;
            int thumbHeight = Math.max(20, (int) ((float) panelHeight / totalHeightVal * trackHeight));
            int thumbY = trackY + (int) (scrollRatio * (trackHeight - thumbHeight));
            drawRect(trackX, thumbY, trackX + 2, thumbY + thumbHeight, 0xFF424248);
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

    private List<String> getChangelogLines(ReleaseEntry entry, String lang) {
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

    private String readLocalChangelog() {
        try (InputStream in = HammersUnbound.class.getResourceAsStream("/assets/hammersunbound/changelogs/r1.0b1.md")) {
            if (in != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void fetchOnlineReleases() {
        isLoading = true;
        statusMessage = "";
        new Thread(() -> {
            try {
                URL url = new URL("https://api.github.com/repos/X4yi/HammersUnbound/releases");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "HammersUnboundMod");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    JsonArray array = new JsonParser().parse(sb.toString()).getAsJsonArray();
                    List<ReleaseEntry> fetched = new ArrayList<>();

                    for (JsonElement elem : array) {
                        JsonObject obj = elem.getAsJsonObject();
                        String tag = obj.get("tag_name").getAsString();
                        String body = obj.get("body").getAsString();
                        fetched.add(new ReleaseEntry(tag, body, false));
                    }

                    synchronized (releases) {
                        // Merge online releases with local offline cache
                        for (ReleaseEntry online : fetched) {
                            boolean exists = false;
                            for (int i = 0; i < releases.size(); i++) {
                                if (releases.get(i).version.equalsIgnoreCase(online.version)) {
                                    // Replace local offline draft with parsed full release
                                    releases.set(i, online);
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                releases.add(online);
                            }
                        }
                    }
                    statusMessage = TextFormatting.GREEN + "Synced!";
                } else {
                    statusMessage = TextFormatting.RED + "Sync Error (" + conn.getResponseCode() + ")";
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusMessage = TextFormatting.RED + "Connection Failed";
            } finally {
                isLoading = false;
            }
        }).start();
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
            fetchOnlineReleases();
            return;
        }

        // Sidebar version clicks
        int verY = sidebarY1 + 6;
        synchronized (releases) {
            for (int i = 0; i < releases.size(); i++) {
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

    @Override
    protected void drawBackground(int mouseX, int mouseY, float partialTicks) {
        // No-op to prevent drawing the default missing background texture
    }
}
