package com.x4yi.hammersunbound.client.gui.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.text.TextFormatting;

public class MarkdownRenderer {

    public static int drawWrappedMarkdown(FontRenderer fontRenderer, String line, int x, int y, int wrapWidth, boolean isBullet) {
        if (line.trim().isEmpty()) return 6;
        int startX = x;
        int currX = x;
        int currY = y;
        int lineHeight = 10;
        
        if (line.startsWith("# ")) {
            String text = line.substring(2);
            fontRenderer.drawString(TextFormatting.BOLD + text, currX, currY, 0xFF00E5FF);
            return 14;
        } else if (line.startsWith("## ")) {
            String text = line.substring(3);
            fontRenderer.drawString(TextFormatting.BOLD + text, currX, currY, 0xFF00E676);
            return 12;
        } else if (line.startsWith("### ")) {
            String text = line.substring(4);
            fontRenderer.drawString(TextFormatting.BOLD + text, currX, currY, 0xFFFFD600);
            return 10;
        }
        
        boolean bullet = isBullet || line.startsWith("- ") || line.startsWith("* ");
        String text = line;
        if (line.startsWith("- ") || line.startsWith("* ")) {
            text = line.substring(2);
        }
        
        if (bullet) {
            Gui.drawRect(currX, currY + 3, currX + 4, currY + 7, 0xFF00C853);
            currX += 10;
            startX += 10;
        }
        
        String[] parts = text.split("(?=\\*\\*)|(?<=\\*\\*)");
        boolean bold = false;
        
        for (String part : parts) {
            if (part.equals("**")) {
                bold = !bold;
                continue;
            }
            String[] tokens = part.split("(?<= )|(?= )");
            for (String token : tokens) {
                if (token.isEmpty()) continue;
                String formattedToken = (bold ? TextFormatting.BOLD : "") + token;
                int tokenWidth = fontRenderer.getStringWidth(formattedToken);
                if (currX + tokenWidth > x + wrapWidth && !token.equals(" ")) {
                    currX = startX;
                    currY += lineHeight;
                }
                fontRenderer.drawString(formattedToken, currX, currY, 0xFFE0E0E6);
                currX += tokenWidth;
            }
        }
        return (currY - y) + lineHeight + 2;
    }
}
