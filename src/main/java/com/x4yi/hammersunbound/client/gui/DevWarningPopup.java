package com.x4yi.hammersunbound.client.gui;

import com.x4yi.hammersunbound.client.gui.base.GuiBaseScreen;
import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.config.ConfigManager;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DevWarningPopup extends GuiBaseScreen {

    private static final String POPUP_MD_PATH = "/assets/hammersunbound/dev/popup.md";
    private static final String ISSUES_URL = "https://github.com/x4yi/HammersUnbounds/issues";

    private final List<String> contentLines = new ArrayList<String>();
    private String currentLanguage = "en";

    public DevWarningPopup(GuiScreen parent) {
        super(parent, "Hammers Unbound");
    }

    @Override
    protected void initComponents() {
        currentLanguage = mc.getLanguageManager().getCurrentLanguage().getLanguageCode().startsWith("es") ? "es" : "en";
        contentLines.clear();
        contentLines.addAll(loadLocalizedLines(currentLanguage));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        updateAnimation();

        int boxW = 380;
        int boxH = 200;
        int x1 = width / 2 - boxW / 2;
        int y1 = height / 2 - boxH / 2;
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
            if (textY > y2 - 56) {
                break;
            }
        }

        int linkY = y2 - 54;
        boolean linkHovered = isWithin(mouseX, mouseY, x1 + 10, linkY, x2 - 10, linkY + 10);
        String linkText = "GitHub Issues: " + ISSUES_URL;
        fontRenderer.drawString(linkText, x1 + 10, linkY, linkHovered ? 0xFF4FC3F7 : 0xFF90CAF9);

        int btnY1 = y2 - 34;
        int okX1 = x2 - 162;
        int okX2 = x2 - 84;
        int exitX1 = x2 - 78;
        int exitX2 = x2 - 10;
        boolean okHovered = isWithin(mouseX, mouseY, okX1, btnY1, okX2, y2 - 10);
        boolean exitHovered = isWithin(mouseX, mouseY, exitX1, btnY1, exitX2, y2 - 10);

        drawRect(okX1, btnY1, okX2, y2 - 10, okHovered ? 0xFF00C853 : 0xFF16161E);
        drawRect(exitX1, btnY1, exitX2, y2 - 10, exitHovered ? 0xFFE53935 : 0xFF16161E);
        drawCenteredString(fontRenderer, "Ok", okX1 + (okX2 - okX1) / 2, btnY1 + 6, 0xFFFFFFFF);
        drawCenteredString(fontRenderer, "Salir", exitX1 + (exitX2 - exitX1) / 2, btnY1 + 6, 0xFFFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int boxW = 380;
        int boxH = 200;
        int x1 = width / 2 - boxW / 2;
        int y1 = height / 2 - boxH / 2;
        int x2 = x1 + boxW;
        int y2 = y1 + boxH;

        int linkY = y2 - 54;
        if (isWithin(mouseX, mouseY, x1 + 10, linkY, x2 - 10, linkY + 10)) {
            openBrowser(ISSUES_URL);
            return;
        }

        int btnY1 = y2 - 34;
        if (isWithin(mouseX, mouseY, x2 - 162, btnY1, x2 - 84, y2 - 10)) {
            ClientConfig.showDevWarning = false;
            ConfigManager.save();
            closeScreen();
            return;
        }
        if (isWithin(mouseX, mouseY, x2 - 78, btnY1, x2 - 10, y2 - 10)) {
            closeScreen();
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

    private boolean isWithin(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
        return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
    }

    private List<String> loadLocalizedLines(String lang) {
        List<String> lines = new ArrayList<String>();
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
        InputStream in = DevWarningPopup.class.getResourceAsStream(POPUP_MD_PATH);
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
        List<String> wrapped = fontRenderer.listFormattedStringToWidth(text, wrapWidth);
        int currY = y;
        for (String w : wrapped) {
            fontRenderer.drawString(w, x, currY, 0xFFE0E0E6);
            currY += 10;
        }
        return Math.max(10, wrapped.size() * 10);
    }

    private void openBrowser(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ignored) {
        }
    }
}
