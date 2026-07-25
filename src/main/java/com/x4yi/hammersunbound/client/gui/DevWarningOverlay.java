package com.x4yi.hammersunbound.client.gui;
import com.x4yi.hammersunbound.client.gui.base.GuiBaseScreen;
import com.x4yi.hammersunbound.client.gui.component.GuiButton;
import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.config.ConfigManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
public class DevWarningOverlay extends GuiBaseScreen {
    private static final String POPUP_MD_PATH = "/assets/hammersunbound/dev/popup.md";
    private static final String ISSUES_URL = "https://github.com/x4yi/HammersUnbounds/issues";
    private static final ResourceLocation FLAG_US = new ResourceLocation("hammersunbound", "textures/gui/flag_us.png");
    private static final ResourceLocation FLAG_ES = new ResourceLocation("hammersunbound", "textures/gui/flag_es.png");
    private final List<String> contentLines = new ArrayList<>();
    private String currentLanguage = "en";
    private GuiButton btnOk;
    private GuiButton btnExit;
    public DevWarningOverlay(GuiScreen parent) {
        super(parent, "Hammers Unbound");
    }
    @Override
    protected void initComponents() {
        components.clear();
        currentLanguage = ClientConfig.language;
        if (currentLanguage == null || (!currentLanguage.equals("es") && !currentLanguage.equals("en"))) {
            currentLanguage = mc.getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("es") ? "es" : "en";
            ClientConfig.language = currentLanguage;
        }
        contentLines.clear();
        contentLines.addAll(loadLocalizedLines(currentLanguage));
        int boxW = 380;
        int boxH = 200;
        int x1 = width / 2 - boxW / 2;
        int y1 = height / 2 - boxH / 2;
        int x2 = x1 + boxW;
        int y2 = y1 + boxH;
        int btnY1 = y2 - 34;
        int btnH = 24;
        btnOk = new GuiButton(x2 - 162, btnY1, 78, btnH, "Aceptar", () -> {
            ClientConfig.showDevWarning = false;
            ConfigManager.save();
            closeScreen();
        });
        btnOk.setBgColor(0xFF16161E);
        btnOk.setHoverBgColor(0xFF00C853);
        btnExit = new GuiButton(x2 - 78, btnY1, 68, btnH, "Salir", this::closeScreen);
        btnExit.setBgColor(0xFF16161E);
        btnExit.setHoverBgColor(0xFFE53935);
        addComponent(btnOk);
        addComponent(btnExit);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (parentScreen != null) {
            if (parentScreen.mc == null) {
                parentScreen.setWorldAndResolution(this.mc, this.width, this.height);
            }
            parentScreen.drawScreen(mouseX, mouseY, partialTicks);
        }
        drawRect(0, 0, width, height, 0x88000000);
        updateAnimation();
        int boxW = 380;
        int boxH = 200;
        int x1 = width / 2 - boxW / 2;
        int y1 = height / 2 - boxH / 2 + 10;
        int x2 = x1 + boxW;
        int y2 = y1 + boxH;
        drawRect(x1 - 2, y1 - 2, x2 + 2, y2 + 2, 0x55000000);
        drawRect(x1, y1, x2, y2, 0xFF0B0B0D);
        drawRect(x1, y1, x2, y1 + 24, 0xFF08080A);
        drawRect(x1, y1 + 23, x2, y1 + 24, 0xFF222228);
        fontRenderer.drawString("Development Notice", x1 + 8, y1 + 8, 0xFFFFFFFF);
        int textY = y1 + 34;
        int maxW = boxW - 24;
        for (String line : contentLines) {
            textY += drawWrappedMarkdown(line, x1 + 10, textY, maxW);
            if (textY > y2 - 56) break;
        }
        int linkY = y2 - 54;
        boolean linkHovered = mouseX >= x1 + 10 && mouseX <= x2 - 10 && mouseY >= linkY && mouseY <= linkY + 10;
        String linkText = "GitHub Issues: " + ISSUES_URL;
        fontRenderer.drawString(linkText, x1 + 10, linkY, linkHovered ? 0xFF4FC3F7 : 0xFF90CAF9);
        super.drawScreen(mouseX, mouseY, partialTicks);
        int flagX = x1 + 10;
        int flagY = y2 + 4;
        int flagSize = 16;
        boolean flagHovered = mouseX >= flagX && mouseX <= flagX + 22 && mouseY >= flagY && mouseY <= flagY + flagSize;
        ResourceLocation flagTex = currentLanguage.equals("es") ? FLAG_ES : FLAG_US;
        mc.getTextureManager().bindTexture(flagTex);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        drawModalRectWithCustomSizedTexture(flagX, flagY, 0, 0, 22, flagSize, 32, 22);
        String langLabel = currentLanguage.equals("es") ? "ES" : "EN";
        fontRenderer.drawString(langLabel, flagX + 26, flagY + 4, flagHovered ? 0xFFFFFFFF : 0xFF888892);
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int boxW = 380;
        int boxH = 200;
        int x1 = width / 2 - boxW / 2;
        int y1 = height / 2 - boxH / 2 + 10;
        int x2 = x1 + boxW;
        int y2 = y1 + boxH;
        int linkY = y2 - 54;
        if (mouseX >= x1 + 10 && mouseX <= x2 - 10 && mouseY >= linkY && mouseY <= linkY + 10) {
            openBrowser(ISSUES_URL);
            return;
        }
        int flagX = x1 + 10;
        int flagY = y2 + 4;
        int flagSize = 16;
        if (mouseX >= flagX && mouseX <= flagX + 22 + 30 && mouseY >= flagY && mouseY <= flagY + flagSize) {
            toggleLanguage();
            return;
        }
        if (!isWithin(mouseX, mouseY, x1, y1, x2, y2)) {
            closeScreen();
        }
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            closeScreen();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }
    @Override
    public void closeScreen() {
        mc.displayGuiScreen(parentScreen);
    }
    private void toggleLanguage() {
        currentLanguage = currentLanguage.equals("es") ? "en" : "es";
        ClientConfig.language = currentLanguage;
        ConfigManager.save();
        contentLines.clear();
        contentLines.addAll(loadLocalizedLines(currentLanguage));
        String okText = currentLanguage.equals("es") ? "Aceptar" : "OK";
        String exitText = currentLanguage.equals("es") ? "Salir" : "Exit";
        btnOk.setText(okText);
        btnExit.setText(exitText);
    }
    private boolean isWithin(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }
    private List<String> loadLocalizedLines(String lang) {
        List<String> lines = new ArrayList<>();
        String body = readPopupBody();
        if (body.isEmpty()) return lines;
        String startTag = "[" + lang.toUpperCase() + "]";
        String endTag = "[/" + lang.toUpperCase() + "]";
        if (body.contains(startTag) && body.contains(endTag)) {
            int startIdx = body.indexOf(startTag) + startTag.length();
            int endIdx = body.indexOf(endTag);
            if (endIdx > startIdx) {
                body = body.substring(startIdx, endIdx);
            }
        }
        String[] split = body.split("\\r?\\n");
        for (String s : split) {
            lines.add(s);
        }
        return lines;
    }
    private String readPopupBody() {
        InputStream in = DevWarningOverlay.class.getResourceAsStream(POPUP_MD_PATH);
        if (in == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException ignored) {
            return "";
        }
        return sb.toString();
    }
    private int drawWrappedMarkdown(String line, int x, int y, int wrapWidth) {
        if (line == null || line.trim().isEmpty()) return 4;
        String text = line.startsWith("- ") ? line.substring(2) : line;
        if (line.startsWith("# ")) {
            fontRenderer.drawString(text.substring(2), x, y, 0xFFFFD600);
            return 14;
        }
        List<String> wrapped = fontRenderer.listFormattedStringToWidth(text, wrapWidth);
        int currY = y;
        for (String w : wrapped) {
            fontRenderer.drawString(w, x, currY, 0xFFE0E0E6);
            currY += 10;
        }
        return Math.max(10, wrapped.size() * 10);
    }
    private void openBrowser(String url) {
        new Thread(() -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (Exception ignored) {
            }
        }).start();
    }
}