package com.x4yi.hammers.client.gui;

import com.x4yi.hammers.config.HammersUnboundClient;
import com.x4yi.hammers.config.HammersUnboundItems;
import com.x4yi.hammers.config.HammersUnboundServer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiHammersUnboundConfig extends GuiScreen {

    public String VersionMod = "beta-3-WarHammerImprove";

    private final GuiScreen parent;

    private int guiX;
    private int guiY;
    private int guiW;
    private int leftW, rightX;
    private int contentTop, contentBottom;

    private int leftScroll, rightScroll;
    private int maxLeftScroll, maxRightScroll;

    private final List<CategoryEntry> categories = new ArrayList<>();
    private final List<FieldEntry> fields = new ArrayList<>();

    private CategoryEntry selected;

    private GuiButton applyBtn;
    private GuiButton resetBtn;

    public GuiHammersUnboundConfig(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {

        buttonList.clear();
        categories.clear();
        fields.clear();

        leftScroll = 0;
        rightScroll = 0;

        guiW = Math.min((int) (width * 0.85f), width - 20);
        int guiH = Math.min((int) (height * 0.85f), height - 20);

        guiX = (width - guiW) / 2;
        guiY = (height - guiH) / 2;

        leftW = guiW / 3;
        rightX = guiX + leftW + 12;

        contentTop = guiY + 35;
        contentBottom = guiY + guiH - 45;

        buildCategories();

        if (!categories.isEmpty())
            select(categories.get(0));

        applyBtn = new GuiButton(1, guiX + guiW - 300, guiY + guiH - 25, 90, 20, "Apply");
        resetBtn = new GuiButton(2, guiX + guiW - 200, guiY + guiH - 25, 90, 20, "Reset");
        GuiButton cancelBtn = new GuiButton(3, guiX + guiW - 100, guiY + guiH - 25, 90, 20, "Cancel");

        buttonList.add(applyBtn);
        buttonList.add(resetBtn);
        buttonList.add(cancelBtn);

    }
    private void buildCategories() {

        categories.add(new CategoryEntry("Client", HammersUnboundClient.client));
        categories.add(new CategoryEntry("Server", HammersUnboundServer.server));
        CategoryEntry war = new CategoryEntry("WarHammer", null);
        addChildren(war, HammersUnboundItems.warhammer);
        categories.add(war);

        CategoryEntry spike = new CategoryEntry("SpikeHammer", null);
        addChildren(spike, HammersUnboundItems.spikehammer);
        categories.add(spike);
    }

    private void addChildren(CategoryEntry parent, Object obj) {
        for (Field f : obj.getClass().getFields()) {
            try {
                parent.children.add(new CategoryEntry(f.getName(), f.get(obj)));
            } catch (Exception ignored) {
            }
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        drawDefaultBackground();

        drawCenteredString(fontRenderer,
                "HammersUnbound Config - " + VersionMod ,
                width / 2,
                guiY + 12,
                0xFFFFFF);

        drawPanels(mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawPanels(int mouseX, int mouseY) {

        drawRect(guiX, contentTop - 5, guiX + leftW, contentBottom + 5, 0x44000000);
        drawRect(rightX - 6, contentTop - 5, guiX + guiW, contentBottom + 5, 0x44000000);
        drawRect(guiX + leftW + 4, contentTop - 5,
                guiX + leftW + 5, contentBottom + 5, 0xFF555555);

        drawLeft(mouseX, mouseY);
        drawRight(mouseX, mouseY);
    }

    private void drawLeft(int mouseX, int mouseY) {

        enableScissor(guiX, contentTop, leftW, contentBottom - contentTop);

        int y = contentTop - leftScroll;
        int total = 0;

        for (CategoryEntry cat : categories) {

            total += drawCategory(cat, guiX + 12, y, mouseX, mouseY);
            y += 22;

            if (cat.expanded) {
                for (CategoryEntry child : cat.children) {
                    total += drawCategory(child, guiX + 26, y, mouseX, mouseY);
                    y += 20;
                }
            }
        }

        disableScissor();

        maxLeftScroll = Math.max(0, total - (contentBottom - contentTop));
        drawScrollbar(guiX + leftW - 6, leftScroll, maxLeftScroll);
    }

    private int drawCategory(CategoryEntry entry, int x, int y, int mouseX, int mouseY) {

        if (y < contentTop - 20 || y > contentBottom)
            return 22;

        boolean hovered =
                mouseX >= guiX &&
                        mouseX <= guiX + leftW &&
                        mouseY >= y &&
                        mouseY <= y + 18;

        if (hovered)
            drawRect(guiX, y, guiX + leftW, y + 18, 0x22000000);

        if (entry == selected)
            fontRenderer.drawString("▶", x - 10, y + 5, 0xAAAAAA);

        fontRenderer.drawString(entry.name, x, y + 5,
                hovered ? 0xFFFF55 : 0xFFFFFF);

        return 22;
    }

    private void drawRight(int mouseX, int mouseY) {

        int rightWidth = (guiX + guiW) - rightX - 10;
        if (rightWidth <= 0) return;

        enableScissor(rightX, contentTop, rightWidth, contentBottom - contentTop);

        int y = contentTop - rightScroll + 6;
        int totalHeight = fields.size() * 28;

        FieldEntry hovered = null;

        for (FieldEntry f : fields) {

            if (y + 24 >= contentTop && y <= contentBottom) {
                f.draw(mouseX, mouseY, y);
                if (f.isMouseOver(mouseX, mouseY))
                    hovered = f;
            }

            y += 28;
        }

        disableScissor();

        maxRightScroll = Math.max(0,
                totalHeight + 6 - (contentBottom - contentTop));

        drawScrollbar(guiX + guiW - 6, rightScroll, maxRightScroll);

        if (hovered != null)
            hovered.drawTooltip(mouseX, mouseY);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        for (FieldEntry f : fields)
            if (f.text != null)
                f.text.updateCursorCounter();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        if (mouseX >= guiX && mouseX <= guiX + leftW)
            handleLeftClick(mouseY);

        for (FieldEntry f : fields)
            f.mouseClicked(mouseX, mouseY, mouseButton);
        refreshButtons();

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char c, int key) throws IOException {

        for (FieldEntry f : fields)
            f.keyTyped(c, key);
        refreshButtons();
        super.keyTyped(c, key);
    }

    @Override
    protected void actionPerformed(GuiButton button) {

        if (button.id == 1 && applyBtn.enabled) {

            for (FieldEntry f : fields)
                f.apply();

            ConfigManager.sync("hammersunbound", Config.Type.INSTANCE);

            if (mc.isSingleplayer()) {
                MinecraftForge.EVENT_BUS.post(
                        new ConfigChangedEvent.OnConfigChangedEvent(
                                "hammersunbound", null, false, false));
            }

            mc.displayGuiScreen(parent);
        }

        if (button.id == 2)
            initGui();

        if (button.id == 3)
            mc.displayGuiScreen(parent);
    }

    @Override
    public void handleMouseInput() throws IOException {

        super.handleMouseInput();

        int wheel = Mouse.getEventDWheel();
        if (wheel == 0) return;

        int mouseX = Mouse.getEventX() * width / mc.displayWidth;

        if (mouseX >= guiX && mouseX <= guiX + leftW)
            leftScroll = clamp(leftScroll - Integer.signum(wheel) * 12, maxLeftScroll);
        else
            rightScroll = clamp(rightScroll - Integer.signum(wheel) * 12, maxRightScroll);
    }

    private void handleLeftClick(int mouseY) {

        if (mouseY < contentTop || mouseY > contentBottom)
            return;

        int y = contentTop - leftScroll;

        for (CategoryEntry cat : categories) {

            if (mouseY >= y && mouseY <= y + 18) {

                if (!cat.children.isEmpty())
                    cat.expanded = !cat.expanded;
                else
                    select(cat);

                return;
            }

            y += 22;

            if (cat.expanded) {
                for (CategoryEntry child : cat.children) {
                    if (mouseY >= y && mouseY <= y + 18) {
                        select(child);
                        return;
                    }
                    y += 20;
                }
            }
        }
    }
    private void select(CategoryEntry entry) {

        selected = entry;
        fields.clear();
        rightScroll = 0;

        if (entry.target == null)
            return;

        Class<?> clazz;
        Object instance = null;

        if (entry.target instanceof Class) {
            clazz = (Class<?>) entry.target;
        } else {
            clazz = entry.target.getClass();
            instance = entry.target;
        }

        for (Field f : clazz.getFields()) {

            if (entry.target instanceof Class)
                fields.add(new FieldEntry(f, null));
            else
                fields.add(new FieldEntry(f, instance));
        }

        refreshButtons();
    }
    private void refreshButtons() {

        if (applyBtn == null || resetBtn == null)
            return;

        boolean modified = false;
        boolean errors = false;

        for (FieldEntry f : fields) {
            if (f.isModified()) modified = true;
            if (!f.isValid()) errors = true;
        }

        applyBtn.enabled = modified && !errors;
        resetBtn.enabled = modified;
    }

    private int clamp(int v, int max) {
        return Math.max(0, Math.min(max, v));
    }

    private void enableScissor(int x, int y, int w, int h) {

        ScaledResolution sr = new ScaledResolution(mc);
        int scale = sr.getScaleFactor();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                x * scale,
                mc.displayHeight - (y + h) * scale,
                w * scale,
                h * scale
        );
    }

    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void drawScrollbar(int x, int scroll, int max) {

        if (max <= 0) return;

        int visible = contentBottom - contentTop;
        int thumb = Math.max(20, visible * visible / (visible + max));
        int range = visible - thumb;

        int y = contentTop + (scroll * range / max);

        drawRect(x, contentTop, x + 4, contentBottom, 0x33000000);
        drawRect(x + 1, y, x + 3, y + thumb, 0xFFAAAAAA);
    }
    private static class CategoryEntry {
        String name;
        Object target;
        boolean expanded;
        List<CategoryEntry> children = new ArrayList<>();

        CategoryEntry(String n, Object t) {
            name = n;
            target = t;
        }
    }

    private class FieldEntry {

        Field field;
        Object target;

        GuiTextField text;

        Object original;
        boolean tempBoolean;

        int drawY;

        Integer minInt, maxInt;
        Double minDouble, maxDouble;
        String[] comment;

        boolean isMouseOver(int mouseX, int mouseY) {

            int top = drawY;
            int bottom = drawY + 20;

            return mouseX >= rightX &&
                    mouseX <= guiX + guiW &&
                    mouseY >= top &&
                    mouseY <= bottom;
        }

        FieldEntry(Field f, Object t) {

            field = f;
            target = t;
            field.setAccessible(true);

            try {
                original = field.get(t);
            } catch (Exception ignored) {
            }

            if (f.getType() == boolean.class)
                tempBoolean = (boolean) original;

            Config.RangeInt ri = f.getAnnotation(Config.RangeInt.class);
            if (ri != null) {
                minInt = ri.min();
                maxInt = ri.max();
            }

            Config.RangeDouble rd = f.getAnnotation(Config.RangeDouble.class);
            if (rd != null) {
                minDouble = rd.min();
                maxDouble = rd.max();
            }

            Config.Comment c = f.getAnnotation(Config.Comment.class);
            if (c != null) comment = c.value();

            if (f.getType() != boolean.class) {
                text = new GuiTextField(0, fontRenderer, rightX + 150, 0, 90, 18);
                text.setText(String.valueOf(original));

            }
        }

        void draw(int mouseX, int mouseY, int by) {

            drawY = by; // ← guardamos posición

            boolean modified = isModified();
            boolean valid = isValid();

            int color = valid ? (modified ? 0xFFCC55 : 0xFFFFFF) : 0xFF5555;

            fontRenderer.drawString(field.getName(), rightX, by + 5, color);

            if (text != null) {
                text.y = by;
                text.drawTextBox();
            }

            if (field.getType() == boolean.class) {

                int bx = rightX + 150;

                boolean hover =
                        mouseX >= bx && mouseX <= bx + 60 &&
                                mouseY >= by && mouseY <= by + 18;

                drawRect(bx, by, bx + 60, by + 18,
                        hover ? 0xFF777777 : 0xFF555555);

                drawCenteredString(fontRenderer,
                        tempBoolean ? "ON" : "OFF",
                        bx + 30, by + 5,
                        tempBoolean ? 0x55FF55 : 0xFF5555);
            }
        }

        void mouseClicked(int mouseX, int mouseY, int mouseButton) {

            if (text != null)
                text.mouseClicked(mouseX, mouseY, mouseButton);

            if (field.getType() == boolean.class) {

                int bx = rightX + 150;
                int by = drawY;

                if (mouseX >= bx && mouseX <= bx + 60 &&
                        mouseY >= by && mouseY <= by + 18) {

                    tempBoolean = !tempBoolean;
                }
            }
        }

        void keyTyped(char c, int key) {
            if (text != null && text.isFocused())
                text.textboxKeyTyped(c, key);
        }

        boolean isModified() {

            try {
                if (text != null)
                    return !text.getText().equals(String.valueOf(original));

                if (field.getType() == boolean.class)
                    return tempBoolean != (boolean) original;

            } catch (Exception ignored) {
            }

            return false;
        }

        boolean isValid() {

            if (text == null) return true;

            try {

                if (field.getType() == int.class) {
                    int v = Integer.parseInt(text.getText());
                    if (minInt != null && (v < minInt || v > maxInt))
                        return false;
                }

                if (field.getType() == float.class) {
                    float v = Float.parseFloat(text.getText());
                    if (minDouble != null && (v < minDouble || v > maxDouble))
                        return false;
                }

            } catch (Exception e) {
                return false;
            }

            return true;
        }
        void apply() {

            try {

                if (field.getType() == boolean.class) {
                    field.setBoolean(target, tempBoolean);
                    original = tempBoolean;
                }

                if (text != null) {

                    if (field.getType() == int.class) {
                        int v = Integer.parseInt(text.getText());
                        field.setInt(target, v);
                        original = v;
                    }

                    if (field.getType() == float.class) {
                        float v = Float.parseFloat(text.getText());
                        field.setFloat(target, v);
                        original = v;
                    }
                }

            } catch (Exception ignored) {
            }
        }

        void drawTooltip(int mouseX, int mouseY) {

            List<String> lines = new ArrayList<>();

            if (comment != null)
                lines.addAll(Arrays.asList(comment));

            if (minInt != null)
                lines.add("Range: " + minInt + " - " + maxInt);

            if (minDouble != null)
                lines.add("Range: " + minDouble + " - " + maxDouble);

            if (!lines.isEmpty())
                drawHoveringText(lines, mouseX, mouseY);
        }
    }

}
