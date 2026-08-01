package com.x4yi.hammersunbound.client.gui.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders Markdown-formatted text inside Minecraft GUI screens.
 *
 * <p>Supported syntax:
 * <ul>
 *   <li>{@code # Heading 1}  — cyan, bold</li>
 *   <li>{@code ## Heading 2} — green, bold</li>
 *   <li>{@code ### Heading 3} — yellow, bold</li>
 *   <li>{@code - item} / {@code * item} — bulleted list (top-level)</li>
 *   <li>{@code   - item} — nested bullet (2 spaces per indent level)</li>
 *   <li>{@code **text**} — bold inline</li>
 *   <li>{@code _text_} — italic inline</li>
 *   <li>{@code **_text_**} / {@code _**text**_} — bold + italic inline</li>
 *   <li>{@code ---} / {@code ***} / {@code ___} — horizontal separator</li>
 * </ul>
 *
 * <p>Word-wrap respects the {@code wrapWidth} parameter. The body text color
 * is controlled by the caller via {@code textColor}.
 */
public class MarkdownRenderer {

    // ─── Heading colors ────────────────────────────────────────────────────────
    private static final int COLOR_H1 = 0xFF00E5FF; // cyan
    private static final int COLOR_H2 = 0xFF00E676; // green
    private static final int COLOR_H3 = 0xFFFFD600; // yellow

    // ─── Separator color ───────────────────────────────────────────────────────
    private static final int COLOR_SEP = 0xFF444450;

    // ─── Bullet colors ─────────────────────────────────────────────────────────
    private static final int COLOR_BULLET_LVL0 = 0xFF00C853;
    private static final int COLOR_BULLET_LVL1 = 0xFF888892;
    private static final int COLOR_BULLET_LVL2 = 0xFF555560;

    // ─── Layout constants ──────────────────────────────────────────────────────
    private static final int LINE_HEIGHT  = 10;
    private static final int INDENT_WIDTH = 10;
    private static final int BULLET_ADVANCE = 10;

    // ──────────────────────────────────────────────────────────────────────────
    // Internal styled-segment model
    // ──────────────────────────────────────────────────────────────────────────

    private static final class Segment {
        final String text;
        final boolean bold;
        final boolean italic;

        Segment(String text, boolean bold, boolean italic) {
            this.text   = text;
            this.bold   = bold;
            this.italic = italic;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Public API
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Draws one Markdown line (with optional word-wrap) and returns the total
     * height consumed so the caller can advance {@code drawY} accordingly.
     *
     * @param fontRenderer the active font renderer
     * @param line         the raw Markdown line to render
     * @param x            left X of the content area
     * @param y            top Y of this line
     * @param wrapWidth    maximum pixel width before wrapping
     * @param textColor    ARGB color for normal body text
     * @return height in pixels used by this line (including any wrapped rows)
     */
    public static int drawWrappedMarkdown(FontRenderer fontRenderer,
                                          String line,
                                          int x, int y,
                                          int wrapWidth,
                                          int textColor) {
        // ── Empty line → small gap ─────────────────────────────────────────
        if (line.trim().isEmpty()) return 6;

        // ── Headings (longest prefix first to avoid false matches) ────────
        // BUG FIX: previously # matched before ## and ###
        if (line.startsWith("### ")) {
            fontRenderer.drawString(TextFormatting.BOLD + line.substring(4), x, y, COLOR_H3);
            return LINE_HEIGHT + 4;
        }
        if (line.startsWith("## ")) {
            fontRenderer.drawString(TextFormatting.BOLD + line.substring(3), x, y, COLOR_H2);
            return LINE_HEIGHT + 4;
        }
        if (line.startsWith("# ")) {
            fontRenderer.drawString(TextFormatting.BOLD + line.substring(2), x, y, COLOR_H1);
            return LINE_HEIGHT + 6;
        }

        // ── Horizontal separator ──────────────────────────────────────────
        String trimmed = line.trim();
        if (trimmed.equals("---") || trimmed.equals("***") || trimmed.equals("___")) {
            Gui.drawRect(x, y + 4, x + wrapWidth, y + 5, COLOR_SEP);
            return LINE_HEIGHT;
        }

        // ── Detect bullet indent level ────────────────────────────────────
        // Each indent level = 2 leading spaces before a "- " or "* " marker.
        int indentLevel = 0;
        String rest = line;
        while (rest.length() >= 2
                && rest.charAt(0) == ' ' && rest.charAt(1) == ' ') {
            String candidate = rest.substring(2).replaceFirst("^\\s+", "");
            if (candidate.startsWith("- ") || candidate.startsWith("* ")) {
                indentLevel++;
                rest = rest.substring(2);
            } else {
                break;
            }
        }

        // ── Bullet detection ──────────────────────────────────────────────
        boolean bullet = rest.startsWith("- ") || rest.startsWith("* ");
        String bodyText = bullet ? rest.substring(2) : rest;

        // startX: left margin for wrapped continuation lines
        int startX = x + indentLevel * INDENT_WIDTH;
        int currX  = startX;
        int currY  = y;

        if (bullet) {
            drawBullet(indentLevel, currX, currY);
            currX  += BULLET_ADVANCE;
            startX += BULLET_ADVANCE;
        }

        // ── Parse inline styles and render with word-wrap ─────────────────
        List<Segment> segments = parseInlineStyles(bodyText);

        for (Segment seg : segments) {
            // Split preserving spaces so spacing is kept intact.
            // The regex splits AFTER a space or BEFORE a space,
            // keeping each space as its own token.
            String[] tokens = seg.text.split("(?<= )|(?= )");

            for (String token : tokens) {
                if (token.isEmpty()) continue;

                String formatted  = applyFormat(token, seg.bold, seg.italic);
                // BUG FIX: measure with format codes so bold width is accurate,
                // but skip wrapping on whitespace-only tokens.
                int tokenWidth = fontRenderer.getStringWidth(formatted);

                if (!token.trim().isEmpty() && currX + tokenWidth > x + wrapWidth) {
                    currX  = startX;
                    currY += LINE_HEIGHT;
                }

                fontRenderer.drawString(formatted, currX, currY, textColor);
                currX += tokenWidth;
            }
        }

        return (currY - y) + LINE_HEIGHT + 2;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ──────────────────────────────────────────────────────────────────────────

    /** Draws the bullet glyph for the given indent level. */
    private static void drawBullet(int indentLevel, int x, int y) {
        int color = indentLevel == 0 ? COLOR_BULLET_LVL0
                  : indentLevel == 1 ? COLOR_BULLET_LVL1
                  : COLOR_BULLET_LVL2;

        if (indentLevel == 0) {
            // Solid square (4×4 px)
            Gui.drawRect(x, y + 3, x + 4, y + 7, color);
        } else {
            // Smaller hollow square (2×2 px center)
            Gui.drawRect(x + 1, y + 4, x + 3, y + 6, color);
        }
    }

    /** Wraps the token string with the appropriate TextFormatting codes. */
    private static String applyFormat(String token, boolean bold, boolean italic) {
        if (bold && italic) return "" + TextFormatting.BOLD + TextFormatting.ITALIC + token + TextFormatting.RESET;
        if (bold)           return "" + TextFormatting.BOLD   + token + TextFormatting.RESET;
        if (italic)         return "" + TextFormatting.ITALIC + token + TextFormatting.RESET;
        return token;
    }

    /**
     * Parses a raw string containing {@code **bold**} and {@code _italic_}
     * markers into a list of {@link Segment}s, each with clean text and
     * boolean style flags.
     *
     * <p>Nesting is supported: {@code **_text_**} yields bold+italic.
     */
    private static List<Segment> parseInlineStyles(String text) {
        List<Segment> result = new ArrayList<>();
        boolean bold   = false;
        boolean italic = false;
        StringBuilder current = new StringBuilder();

        int i = 0;
        while (i < text.length()) {
            char c = text.charAt(i);

            // ── Bold marker "**" ──────────────────────────────────────────
            if (c == '*' && i + 1 < text.length() && text.charAt(i + 1) == '*') {
                flushSegment(result, current, bold, italic);
                bold = !bold;
                i += 2;

            // ── Italic marker "_" ─────────────────────────────────────────
            } else if (c == '_') {
                flushSegment(result, current, bold, italic);
                italic = !italic;
                i++;

            // ── Regular character ─────────────────────────────────────────
            } else {
                current.append(c);
                i++;
            }
        }

        // Flush any remaining text (handles unclosed markers gracefully)
        flushSegment(result, current, bold, italic);

        return result;
    }

    /** Adds the current buffer as a Segment if non-empty, then clears it. */
    private static void flushSegment(List<Segment> result,
                                     StringBuilder current,
                                     boolean bold, boolean italic) {
        if (current.length() > 0) {
            result.add(new Segment(current.toString(), bold, italic));
            current.setLength(0);
        }
    }
}