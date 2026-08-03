package com.x4yi.hammersunbound.client.gui;
import com.x4yi.x4ui.client.gui.base.GuiBaseScreen;
import com.x4yi.x4ui.client.gui.component.GuiButton;
import com.x4yi.x4ui.client.gui.component.GuiMarkdown;
import com.x4yi.x4ui.client.gui.component.GuiPanel;
import com.x4yi.x4ui.client.gui.component.GuiScrollPanel;
import com.x4yi.x4ui.client.gui.component.layout.FlexDirection;
import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.config.ConfigManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
    private static final ResourceLocation FLAG_MX = new ResourceLocation("hammersunbound", "textures/gui/flag_mx.png");
    private float langPillProgress = 0f;
    private GuideSection currentSection = GuideSection.WARHAMMER;
    private final Map<GuideSection, String> cachedMarkdown = new HashMap<>();
    private GuiScrollPanel contentScroll;
    public GuiGuideScreen(GuiScreen parent) {
        super(parent, "Hammers Unbound - Guide");
    }
    private void playClickSound() {
        mc.getSoundHandler().playSound(
            net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord(
                net.minecraft.init.SoundEvents.UI_BUTTON_CLICK, 1.0F
            )
        );
    }
    private String getSectionText(GuideSection section) {
        if (cachedMarkdown.containsKey(section)) {
            return cachedMarkdown.get(section);
        }
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
                } else if (body.contains("[EN]") && body.contains("[/EN]")) {
                     int startIdx = body.indexOf("[EN]") + 4;
                     int endIdx = body.indexOf("[/EN]");
                     if (endIdx > startIdx) {
                         body = body.substring(startIdx, endIdx);
                     }
                }
                cachedMarkdown.put(section, body);
                return body;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error loading guide for " + section.displayName;
    }
    @Override
    protected void drawTitle() {
    }
    @Override
    protected void initComponents() {
        components.clear();
        if (rootPanel != null) rootPanel.clearChildren();
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
            ClientConfig.language = ClientConfig.language.equals("es") ? "en" : "es";
            try { ConfigManager.save(); } catch (Exception ignored) {}
            cachedMarkdown.clear();
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
                boolean isES = ClientConfig.language.equals("es");
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
                mc.fontRenderer.drawString("Hammers Unbound - Guide", absX + 8, absY + 7, 0xFFFFFFFF);
                drawRect(absX, absY + height - footerHeight, absX + width, absY + height, 0xFF08080A);
                drawRect(absX, absY + height - footerHeight, absX + width, absY + height - footerHeight + 1, 0xFF222228);
                drawRect(absX, absY + headerHeight, absX + sidebarWidth, absY + height - footerHeight, 0xFF0F0F12);
                drawRect(absX + sidebarWidth - 1, absY + headerHeight, absX + sidebarWidth, absY + height - footerHeight, 0xFF222228);
                drawRect(absX + sidebarWidth, absY + headerHeight, absX + width, absY + height - footerHeight, 0xFF070708);
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
        GuiButton changelogBtn = new GuiButton(85, btnY, 70, 18, "Changelog", () -> { playClickSound(); mc.displayGuiScreen(new GuiChangelogScreen(parentScreen)); }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean hovered = isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX(); int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, hovered ? 0xFF6A1B9A : 0xFF16161E);
                drawBorder(absX, absY, absX + width, absY + height, hovered ? 0xFFFFFFFF : 0xFF2C2C36);
                drawString(getText(), absX + (width - mc.fontRenderer.getStringWidth(getText())) / 2, absY + 5, hovered ? 0xFFFFFFFF : 0xFFB580D8);
            }
        };
        windowPanel.addChild(changelogBtn);
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
        GuiPanel sidebarPanel = new GuiPanel(0, headerHeight + 6, sidebarWidth, totalHeight - headerHeight - footerHeight - 6);
        sidebarPanel.setFlexDirection(FlexDirection.VERTICAL);
        sidebarPanel.setGap(2);
        windowPanel.addChild(sidebarPanel);
        for (GuideSection section : GuideSection.values()) {
            boolean active = section == currentSection;
            GuiButton tabBtn = new GuiButton(0, 0, sidebarWidth, 22, section.displayName, () -> {
                if (!active) {
                    playClickSound();
                    currentSection = section;
                    initComponents();
                }
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
        contentScroll = new GuiScrollPanel(sidebarWidth, headerHeight, panelWidth, panelHeight);
        windowPanel.addChild(contentScroll);
        String text = getSectionText(currentSection);
        GuiMarkdown md = new GuiMarkdown(15, 10, panelWidth - 30, text);
        GuiPanel mdWrap = new GuiPanel(0, 0, panelWidth, panelHeight) {
            @Override
            public void update() {
                super.update();
                this.height = md.getHeight() + 20;
            }
        };
        mdWrap.setFlexDirection(FlexDirection.ABSOLUTE);
        mdWrap.addChild(md);
        contentScroll.addChild(mdWrap);
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
}