package com.x4yi.hammersunbound.client.gui;
import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.x4ui.client.gui.base.GuiBaseScreen;
import com.x4yi.x4ui.client.gui.component.GuiButton;
import com.x4yi.x4ui.client.gui.component.GuiMarkdown;
import com.x4yi.x4ui.client.gui.component.GuiPanel;
import com.x4yi.x4ui.client.gui.component.GuiScrollPanel;
import com.x4yi.x4ui.client.gui.component.layout.FlexDirection;
import com.x4yi.hammersunbound.util.UpdateChecker;
import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.config.ConfigManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import java.util.List;
public class GuiChangelogScreen extends GuiBaseScreen {
    private static final ResourceLocation FLAG_US = new ResourceLocation("hammersunbound", "textures/gui/flag_us.png");
    private static final ResourceLocation FLAG_MX = new ResourceLocation("hammersunbound", "textures/gui/flag_mx.png");
    private int selectedReleaseIndex = 0;
    private String currentLanguage;
    private float langPillProgress = 0f;
    private int lastCacheSize = -1;
    private boolean wasChecking = false;
    private GuiScrollPanel contentScroll;
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
    public void updateScreen() {
        super.updateScreen();
        int currentSize = 0;
        synchronized (UpdateChecker.cachedReleases) {
            currentSize = UpdateChecker.cachedReleases.size();
        }
        if (currentSize != lastCacheSize || UpdateChecker.isChecking != wasChecking) {
            lastCacheSize = currentSize;
            wasChecking = UpdateChecker.isChecking;
            initComponents();
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
    protected void initComponents() {
        components.clear();
        if (rootPanel != null) rootPanel.clearChildren();
        if (!UpdateChecker.hasChecked) {
            UpdateChecker.check();
        }
        int startX = width / 2 - 240;
        int startY = height / 2 - 130;
        int totalWidth = 480;
        int totalHeight = 260;
        int headerHeight = 22;
        int footerHeight = 26;
        int sidebarWidth = 120;
        int panelWidth = totalWidth - sidebarWidth;
        int panelHeight = totalHeight - headerHeight - footerHeight;
        GuiButton langPill = new GuiButton(startX + totalWidth - 35, startY + 20, 35, 22, "", () -> {
            playClickSound();
            currentLanguage = currentLanguage.equals("es") ? "en" : "es";
            ClientConfig.language = currentLanguage;
            try { ConfigManager.save(); } catch (Exception ignored) {}
            initComponents();
        }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                langPillProgress += (hovered ? 0.15f : -0.15f);
                if (langPillProgress > 1f) langPillProgress = 1f;
                if (langPillProgress < 0f) langPillProgress = 0f;
                int maxExtrusion = width;
                int minExtrusion = 8;
                int currentExtrusion = minExtrusion + (int)(langPillProgress * (maxExtrusion - minExtrusion));
                int rightEdge = startX + totalWidth;
                int pillX = rightEdge + currentExtrusion - maxExtrusion;
                int pillY = getAbsoluteY();
                drawRect(pillX, pillY, pillX + width, pillY + height, hovered ? 0xFF2C2C36 : 0xFF1E1E24);
                drawBorder(pillX, pillY, pillX + width, pillY + height, 0xFF2C2C36);
                boolean isES = currentLanguage.equals("es");
                mc.getTextureManager().bindTexture(isES ? FLAG_MX : FLAG_US);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                drawModalRectWithCustomSizedTexture(pillX + 3, pillY + 4, 0, 0, 29, 14, 32, 22);
            }
            protected void drawBorder(int left, int top, int right, int bottom, int color) {
                drawRect(left, top, right, top + 1, color);
                drawRect(left, bottom - 1, right, bottom, color);
                drawRect(left, top + 1, left + 1, bottom - 1, color);
                drawRect(right - 1, top + 1, right, bottom - 1, color);
            }
            @Override
            public boolean isMouseOver(int mouseX, int mouseY) {
                int rightEdge = startX + totalWidth;
                int maxExtrusion = width;
                int minExtrusion = 8;
                int currentExtrusion = minExtrusion + (int)(langPillProgress * (maxExtrusion - minExtrusion));
                return mouseX >= rightEdge && mouseX <= rightEdge + currentExtrusion &&
                       mouseY >= getAbsoluteY() && mouseY <= getAbsoluteY() + height;
            }
        };
        rootPanel.addChild(langPill);
        GuiPanel windowPanel = new GuiPanel(startX, startY, totalWidth, totalHeight) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                int absX = getAbsoluteX();
                int absY = getAbsoluteY();
                drawRect(absX - 2, absY - 2, absX + width + 2, absY + height + 2, 0x55000000);
                drawRect(absX, absY, absX + width, absY + height, 0xFF0B0B0D);
                drawRect(absX, absY, absX + width, absY + headerHeight, 0xFF08080A);
                drawRect(absX, absY + headerHeight - 1, absX + width, absY + headerHeight, 0xFF222228);
                mc.fontRenderer.drawString("Changelog", absX + 8, absY + 7, 0xFFFFFFFF);
                mc.fontRenderer.drawString(HammersUnbound.VERSION, absX + 110, absY + 7, 0xFF707077);
                drawRect(absX, absY + height - footerHeight, absX + width, absY + height, 0xFF08080A);
                drawRect(absX, absY + height - footerHeight, absX + width, absY + height - footerHeight + 1, 0xFF222228);
                drawRect(absX, absY + headerHeight, absX + sidebarWidth, absY + height - footerHeight, 0xFF0F0F12);
                drawRect(absX + sidebarWidth - 1, absY + headerHeight, absX + sidebarWidth, absY + height - footerHeight, 0xFF222228);
                drawRect(absX + sidebarWidth, absY + headerHeight, absX + width, absY + height - footerHeight, 0xFF070708);
                if (UpdateChecker.isChecking) {
                    mc.fontRenderer.drawString(TextFormatting.YELLOW + "Connecting...", absX + 165, absY + height - footerHeight + 8, 0xFFFFFFFF);
                } else if (!UpdateChecker.checkStatus.isEmpty()) {
                    mc.fontRenderer.drawString(UpdateChecker.checkStatus, absX + 165, absY + height - footerHeight + 8, 0xFFFFFFFF);
                }
                synchronized (UpdateChecker.cachedReleases) {
                    if (UpdateChecker.cachedReleases.isEmpty()) {
                        if (UpdateChecker.isChecking) {
                            mc.fontRenderer.drawString("Fetching from GitHub...", absX + sidebarWidth + 20, absY + headerHeight + 20, 0xFFFFFFFF);
                        } else if (!UpdateChecker.checkStatus.contains("Synced")) {
                            mc.fontRenderer.drawString("Sync failed. Check connection.", absX + sidebarWidth + 20, absY + headerHeight + 20, 0xFFE53935);
                        } else {
                            mc.fontRenderer.drawString("No releases loaded.", absX + sidebarWidth + 20, absY + headerHeight + 20, 0xFF888892);
                        }
                    }
                }
            }
        };
        rootPanel.addChild(windowPanel);
        GuiButton closeBtn = new GuiButton(totalWidth - 18, 4, 12, 12, "x", () -> {
            playClickSound();
            closeScreen();
        }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                mc.fontRenderer.drawString(getText(), getAbsoluteX() + 4, getAbsoluteY() + 2, hovered ? 0xFFFF3D00 : 0xFF888892);
            }
        };
        windowPanel.addChild(closeBtn);
        int btnY = totalHeight - footerHeight + 4;
        GuiButton backBtn = new GuiButton(8, btnY, 70, 18, "Back", () -> { playClickSound(); closeScreen(); }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX(); int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFF00C853 : 0xFF16161E);
                drawBorder(absX, absY, absX + width, absY + height, hovered ? 0xFFFFFFFF : 0xFF2C2C36);
                drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 5, hovered ? 0xFFFFFFFF : 0xFFE0E0E6);
            }
        };
        windowPanel.addChild(backBtn);
        GuiButton refreshBtn = new GuiButton(85, btnY, 70, 18, "Refresh", () -> {
            playClickSound();
            UpdateChecker.hasChecked = false;
            UpdateChecker.check();
        }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX(); int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFF1E88E5 : 0xFF16161E);
                drawBorder(absX, absY, absX + width, absY + height, hovered ? 0xFFFFFFFF : 0xFF2C2C36);
                drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 5, hovered ? 0xFFFFFFFF : 0xFFE0E0E6);
            }
        };
        windowPanel.addChild(refreshBtn);
        GuiButton configBtn = new GuiButton(totalWidth - 78, btnY, 70, 18, "Config", () -> { playClickSound(); mc.displayGuiScreen(new GuiConfigScreen(parentScreen)); }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX(); int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFF1565C0 : 0xFF16161E);
                drawBorder(absX, absY, absX + width, absY + height, hovered ? 0xFFFFFFFF : 0xFF2C2C36);
                drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 5, hovered ? 0xFFFFFFFF : 0xFF90CAF9);
            }
        };
        windowPanel.addChild(configBtn);
        GuiButton guideBtn = new GuiButton(totalWidth - 155, btnY, 70, 18, "Guide", () -> { playClickSound(); mc.displayGuiScreen(new GuiGuideScreen(parentScreen)); }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX(); int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFF00796B : 0xFF16161E);
                drawBorder(absX, absY, absX + width, absY + height, hovered ? 0xFFFFFFFF : 0xFF2C2C36);
                drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 5, hovered ? 0xFFFFFFFF : 0xFF4DB6AC);
            }
        };
        windowPanel.addChild(guideBtn);
        if (UpdateChecker.updateAvailable) {
            GuiButton updateBadge = new GuiButton(totalWidth - 65, totalHeight - footerHeight + 6, 57, 14, "+ " + UpdateChecker.latestVersion, () -> {
                playClickSound();
                new Thread(() -> {
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(UpdateChecker.latestUrl));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }) {
                @Override
                protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                    boolean hovered = isMouseOver(mouseX, mouseY);
                    int absX = getAbsoluteX(); int absY = getAbsoluteY();
                    drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFFFF8F00 : 0xFFFFB300);
                    drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 3, 0xFF000000);
                    if (hovered) {
                        drawCustomTooltip(mouseX, mouseY);
                    }
                }
                private void drawCustomTooltip(int mx, int my) {
                    int x = mx + 12;
                    int y = my - 12;
                    String line1 = TextFormatting.GREEN + "" + TextFormatting.BOLD + "Update Available!";
                    String line2 = TextFormatting.GRAY + "Version: " + TextFormatting.WHITE + UpdateChecker.latestVersion;
                    String line3 = TextFormatting.GRAY + "Type: " + TextFormatting.WHITE + UpdateChecker.latestType;
                    String line4 = TextFormatting.GRAY + "Link: " + TextFormatting.AQUA + "github.com/X4yi/HammersUnbound";
                    String line5 = TextFormatting.YELLOW + "Click to download!";
                    int w1 = mc.fontRenderer.getStringWidth(line1);
                    int w2 = mc.fontRenderer.getStringWidth(line2);
                    int w3 = mc.fontRenderer.getStringWidth(line3);
                    int w4 = mc.fontRenderer.getStringWidth(line4);
                    int w5 = mc.fontRenderer.getStringWidth(line5);
                    int boxWidth = Math.max(Math.max(Math.max(w1, w2), w3), Math.max(w4, w5)) + 12;
                    int boxHeight = 56;
                    if (x + boxWidth > width) x = mx - boxWidth - 12;
                    if (y + boxHeight > height) y = height - boxHeight - 8;
                    if (y < 4) y = 4;
                    drawRect(x - 1, y - 1, x + boxWidth + 1, y + boxHeight + 1, 0xFF2C2C35);
                    drawRect(x, y, x + boxWidth, y + boxHeight, 0xF208080C);
                    mc.fontRenderer.drawString(line1, x + 6, y + 5, 0xFFFFFFFF);
                    mc.fontRenderer.drawString(line2, x + 6, y + 15, 0xFFFFFFFF);
                    mc.fontRenderer.drawString(line3, x + 6, y + 25, 0xFFFFFFFF);
                    mc.fontRenderer.drawString(line4, x + 6, y + 35, 0xFFFFFFFF);
                    mc.fontRenderer.drawString(line5, x + 6, y + 45, 0xFFFFFFFF);
                }
            };
            windowPanel.addChild(updateBadge);
        }
        GuiPanel sidebarPanel = new GuiPanel(0, headerHeight + 6, sidebarWidth, totalHeight - headerHeight - footerHeight - 6);
        sidebarPanel.setFlexDirection(FlexDirection.VERTICAL);
        sidebarPanel.setGap(2);
        windowPanel.addChild(sidebarPanel);
        UpdateChecker.CachedRelease activeRelease = null;
        synchronized (UpdateChecker.cachedReleases) {
            List<UpdateChecker.CachedRelease> cached = UpdateChecker.cachedReleases;
            for (int i = 0; i < cached.size(); i++) {
                UpdateChecker.CachedRelease entry = cached.get(i);
                int index = i;
                boolean active = (i == selectedReleaseIndex);
                if (active) activeRelease = entry;
                GuiButton tabBtn = new GuiButton(6, 0, sidebarWidth - 12, 18, entry.version + (entry.isLocal ? " (Offline)" : ""), () -> {
                    if (!active) {
                        playClickSound();
                        selectedReleaseIndex = index;
                        initComponents();
                    }
                }) {
                    @Override
                    protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                        int absX = getAbsoluteX();
                        int absY = getAbsoluteY();
                        boolean hovered = isMouseOver(mouseX, mouseY);
                        if (active) {
                            drawRect(absX, absY, absX + width, absY + height, 0xFF18181F);
                            drawRect(absX, absY, absX + 2, absY + height, 0xFF00C853);
                            drawBorder(absX, absY, absX + width, absY + height, 0xFF00C853);
                            drawString(getText(), absX + 6, absY + 5, 0xFFFFFFFF);
                        } else {
                            if (hovered) {
                                drawRect(absX, absY, absX + width, absY + height, 0xFF121217);
                            }
                            drawBorder(absX, absY, absX + width, absY + height, 0xFF222228);
                            drawString(getText(), absX + 6, absY + 5, hovered ? 0xFFE0E0E6 : 0xFF888892);
                        }
                    }
                };
                sidebarPanel.addChild(tabBtn);
            }
        }
        contentScroll = new GuiScrollPanel(sidebarWidth, headerHeight, panelWidth, panelHeight);
        windowPanel.addChild(contentScroll);
        if (activeRelease != null) {
            String text = getChangelogText(activeRelease, currentLanguage);
            GuiMarkdown md = new GuiMarkdown(12, 6, panelWidth - 24, text);
            GuiPanel mdWrap = new GuiPanel(0, 0, panelWidth, panelHeight) {
                @Override
                public void update() {
                    super.update();
                    this.height = md.getHeight() + 12;
                }
            };
            mdWrap.setFlexDirection(FlexDirection.ABSOLUTE);
            mdWrap.addChild(md);
            contentScroll.addChild(mdWrap);
        }
    }
    private void drawBorder(int left, int top, int right, int bottom, int color) {
        drawRect(left, top, right, top + 1, color);
        drawRect(left, bottom - 1, right, bottom, color);
        drawRect(left, top + 1, left + 1, bottom - 1, color);
        drawRect(right - 1, top + 1, right, bottom - 1, color);
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
        } else if (body.contains("[EN]") && body.contains("[/EN]")) {
             int startIdx = body.indexOf("[EN]") + 4;
             int endIdx = body.indexOf("[/EN]");
             if (endIdx > startIdx) {
                 body = body.substring(startIdx, endIdx);
             }
        }
        return body;
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
}