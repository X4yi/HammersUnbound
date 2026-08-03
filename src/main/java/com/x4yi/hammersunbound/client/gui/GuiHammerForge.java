package com.x4yi.hammersunbound.client.gui;
import com.x4yi.hammersunbound.inventory.ContainerHammerForge;
import com.x4yi.hammersunbound.crafting.HammerForgeRecipe;
import com.x4yi.hammersunbound.crafting.HammerRecipeManager;
import com.x4yi.hammersunbound.network.ModNetworkHandler;
import com.x4yi.hammersunbound.network.PacketForgeHammer;
import com.x4yi.hammersunbound.init.ModItems;
import com.x4yi.x4ui.client.gui.base.GuiBaseContainer;
import com.x4yi.x4ui.client.gui.component.GuiButton;
import com.x4yi.x4ui.client.gui.component.GuiPanel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.resources.I18n;
import java.util.List;
public class GuiHammerForge extends GuiBaseContainer {
    private final EntityPlayer player;
    private List<HammerForgeRecipe> recipes;
    private String currentType = "warhammer";
    private String currentMaterial = "iron";
    private final String[] TYPES = {"warhammer", "spikehammer"};
    private final String[] MATERIALS = {"diamond", "gold", "iron", "stone", "wood"};
    private String hoveredTooltip = null;
    private ItemStack hoveredIngredient = null;
    public GuiHammerForge(EntityPlayer player) {
        super(new ContainerHammerForge(player.inventory));
        this.player = player;
        this.recipes = HammerRecipeManager.getRecipes();
        this.xSize = 210;
        this.ySize = 220;
        autoSelectBestMaterial(this.currentType);
    }
    private void autoSelectBestMaterial(String type) {
        for (String mat : MATERIALS) {
            HammerForgeRecipe recipe = getRecipe(type, mat);
            if (canCraft(recipe)) {
                this.currentMaterial = mat;
                return;
            }
        }
        this.currentMaterial = "wood";
    }
    private HammerForgeRecipe getRecipe(String type, String mat) {
        ResourceLocation id = new ResourceLocation("hammersunbound", type + "_" + mat);
        for (HammerForgeRecipe recipe : recipes) {
            if (recipe.getId().equals(id)) {
                return recipe;
            }
        }
        return null;
    }
    private HammerForgeRecipe getCurrentRecipe() {
        return getRecipe(currentType, currentMaterial);
    }
    private boolean canCraft(HammerForgeRecipe recipe) {
        if (recipe == null) return false;
        for (HammerForgeRecipe.IngredientCount ic : recipe.getIngredients()) {
            int found = 0;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (ic.getIngredient().apply(stack)) {
                    found += stack.getCount();
                }
            }
            if (found < ic.getCount()) return false;
        }
        return true;
    }
    private ItemStack getMaterialDisplayItem(String mat) {
        switch(mat) {
            case "diamond": return new ItemStack(Items.DIAMOND);
            case "gold": return new ItemStack(Items.GOLD_INGOT);
            case "iron": return new ItemStack(Items.IRON_INGOT);
            case "stone": return new ItemStack(Blocks.COBBLESTONE);
            case "wood": return new ItemStack(Blocks.LOG);
            default: return new ItemStack(Blocks.BARRIER);
        }
    }
    private ItemStack getTypeDisplayItem(String type) {
        switch(type) {
            case "warhammer": return new ItemStack(ModItems.warhammer_iron);
            case "spikehammer": return new ItemStack(ModItems.spikehammer_iron);
            default: return new ItemStack(Blocks.BARRIER);
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
        int startX = (width - xSize) / 2;
        int startY = (height - ySize) / 2;
        GuiPanel windowPanel = new GuiPanel(startX, startY, xSize, ySize) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                int absX = getAbsoluteX();
                int absY = getAbsoluteY();
                drawRect(absX - 2, absY - 2, absX + width + 2, absY + height + 2, 0x55000000);
                drawRect(absX, absY, absX + width, absY + height, 0xFF0B0B0D);
                drawBorder(absX, absY, absX + width, absY + height, 0xFF222228);
                drawRect(absX, absY, absX + width, absY + 16, 0xFF08080A);
                drawRect(absX, absY + 15, absX + width, absY + 16, 0xFF222228);
                mc.fontRenderer.drawString("Hammer Forge", absX + 8, absY + 4, 0xFFFFFFFF);
                HammerForgeRecipe currentRecipe = getCurrentRecipe();
                if (currentRecipe == null) {
                    drawCenteredString("Receta no encontrada", absX + width / 2, absY + 50, 0xFF5555);
                } else {
                    GlStateManager.pushMatrix();
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(currentRecipe.getResult(), absX + (width/2) - 8, absY + 45);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, currentRecipe.getResult(), absX + (width/2) - 8, absY + 45, null);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popMatrix();
                    drawCenteredString(currentRecipe.getResult().getDisplayName(), absX + (width/2), absY + 65, 0xFFD700);
                    int ingStartX = absX + 15;
                    int ingX = ingStartX;
                    int ingY = absY + 75;
                    int maxRowWidth = absX + width - 15;
                    for (HammerForgeRecipe.IngredientCount ic : currentRecipe.getIngredients()) {
                        ItemStack[] matchingStacks = ic.getIngredient().getMatchingStacks();
                        if (matchingStacks.length == 0) continue;
                        ItemStack displayStack = matchingStacks[0];
                        int found = 0;
                        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                            ItemStack stack = player.inventory.getStackInSlot(i);
                            if (ic.getIngredient().apply(stack)) {
                                found += stack.getCount();
                            }
                        }
                        String text = ic.getCount() + " (" + found + ")";
                        int textWidth = mc.fontRenderer.getStringWidth(text);
                        int elementWidth = 16 + 2 + textWidth + 15;
                        if (ingX + elementWidth > maxRowWidth && ingX != ingStartX) {
                            ingX = ingStartX;
                            ingY += 16;
                        }
                        int color = found >= ic.getCount() ? 0x55FF55 : 0xFF5555;
                        RenderHelper.enableGUIStandardItemLighting();
                        mc.getRenderItem().renderItemAndEffectIntoGUI(displayStack, ingX, ingY);
                        RenderHelper.disableStandardItemLighting();
                        mc.fontRenderer.drawString(text, ingX + 18, ingY + 4, color);
                        if (isMouseOver(mouseX, mouseY) && mouseX >= ingX && mouseX < ingX + 16 && mouseY >= ingY && mouseY < ingY + 16) {
                            hoveredIngredient = displayStack;
                        }
                        ingX += elementWidth;
                    }
                }
                int invStartX = absX + 24;
                int invStartY = absY + 138;
                mc.fontRenderer.drawString(I18n.format("container.inventory"), invStartX, invStartY - 10, 0xFF888892);
                for (int i = 0; i < 3; ++i) {
                    for (int j = 0; j < 9; ++j) {
                        int slotX = invStartX + j * 18 - 1;
                        int slotY = invStartY + i * 18 - 1;
                        drawRect(slotX, slotY, slotX + 18, slotY + 18, 0xFF121217);
                        drawBorder(slotX, slotY, slotX + 18, slotY + 18, 0xFF222228);
                    }
                }
                for (int j = 0; j < 9; ++j) {
                    int slotX = invStartX + j * 18 - 1;
                    int slotY = absY + 196 - 1;
                    drawRect(slotX, slotY, slotX + 18, slotY + 18, 0xFF121217);
                    drawBorder(slotX, slotY, slotX + 18, slotY + 18, 0xFF222228);
                }
            }
            protected void drawCenteredString(String text, int x, int y, int color) {
                mc.fontRenderer.drawString(text, x - mc.fontRenderer.getStringWidth(text) / 2, y, color);
            }
            protected void drawBorder(int left, int top, int right, int bottom, int color) {
                drawRect(left, top, right, top + 1, color);
                drawRect(left, bottom - 1, right, bottom, color);
                drawRect(left, top + 1, left + 1, bottom - 1, color);
                drawRect(right - 1, top + 1, right, bottom - 1, color);
            }
        };
        int tabY = startY + 20;
        for (String type : TYPES) {
            String finalType = type;
            GuiButton tabBtn = new GuiButton(startX - 25, tabY, 25, 28, "", () -> {
                if (!currentType.equals(finalType)) {
                    currentType = finalType;
                    autoSelectBestMaterial(currentType);
                    playClickSound();
                    initComponents();
                }
            }) {
                @Override
                protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                    boolean active = finalType.equals(currentType);
                    boolean hovered = isMouseOver(mouseX, mouseY);
                    int absX = getAbsoluteX();
                    int absY = getAbsoluteY();
                    drawRect(absX, absY, absX + width, absY + height, active ? 0xFF18181F : (hovered ? 0xFF121217 : 0xFF0B0B0D));
                    drawRect(absX, absY, absX + width, absY + 1, active ? 0xFF00C853 : 0xFF222228);
                    drawRect(absX, absY + height - 1, absX + width, absY + height, active ? 0xFF00C853 : 0xFF222228);
                    drawRect(absX, absY + 1, absX + 1, absY + height - 1, active ? 0xFF00C853 : 0xFF222228);
                    GlStateManager.pushMatrix();
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getTypeDisplayItem(finalType), absX + 4, absY + 6);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popMatrix();
                    if (hovered && !active) {
                        hoveredTooltip = finalType.substring(0, 1).toUpperCase() + finalType.substring(1);
                    }
                }
            };
            rootPanel.addChild(tabBtn);
            tabY += 32;
        }
        int matW = 20;
        int matX = (xSize / 2) - ((MATERIALS.length * matW) / 2);
        int matY = 20;
        for (String mat : MATERIALS) {
            String finalMat = mat;
            GuiButton matBtn = new GuiButton(matX, matY, matW, matW, "", () -> {
                if (!currentMaterial.equals(finalMat)) {
                    currentMaterial = finalMat;
                    playClickSound();
                    initComponents();
                }
            }) {
                @Override
                protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                    boolean active = finalMat.equals(currentMaterial);
                    boolean hovered = isMouseOver(mouseX, mouseY);
                    int absX = getAbsoluteX();
                    int absY = getAbsoluteY();
                    drawRect(absX, absY, absX + width, absY + height, active ? 0xFF18181F : (hovered ? 0xFF121217 : 0xFF0F0F12));
                    drawRect(absX, absY, absX + width, absY + 1, active ? 0xFF00C853 : 0xFF222228);
                    drawRect(absX, absY + height - 1, absX + width, absY + height, active ? 0xFF00C853 : 0xFF222228);
                    drawRect(absX, absY + 1, absX + 1, absY + height - 1, active ? 0xFF00C853 : 0xFF222228);
                    drawRect(absX + width - 1, absY + 1, absX + width, absY + height - 1, active ? 0xFF00C853 : 0xFF222228);
                    GlStateManager.pushMatrix();
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(getMaterialDisplayItem(finalMat), absX + 2, absY + 2);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popMatrix();
                    if (hovered && !active) {
                        hoveredTooltip = finalMat.substring(0, 1).toUpperCase() + finalMat.substring(1);
                    }
                }
            };
            windowPanel.addChild(matBtn);
            matX += matW;
        }
        int btnWidth = 100;
        int saveX1 = (xSize / 2) - (btnWidth / 2);
        int saveY1 = 105;
        GuiButton forgeBtn = new GuiButton(saveX1, saveY1, btnWidth, 18, "", () -> {
            HammerForgeRecipe currentRecipe = getCurrentRecipe();
            if (canCraft(currentRecipe)) {
                playClickSound();
                mc.player.playSound(net.minecraft.init.SoundEvents.BLOCK_ANVIL_USE, 1.0f, 1.0f);
                ModNetworkHandler.INSTANCE.sendToServer(new PacketForgeHammer(currentRecipe.getId()));
            }
        }) {
            @Override
            protected void drawSelf(int mouseX, int mouseY, float partialTicks) {
                boolean craftable = canCraft(getCurrentRecipe());
                boolean hovered = craftable && isMouseOver(mouseX, mouseY);
                int absX = getAbsoluteX();
                int absY = getAbsoluteY();
                drawRect(absX, absY, absX + width, absY + height, craftable ? (hovered ? 0xFF00C853 : 0xFF16161E) : 0xFF16161E);
                drawRect(absX, absY, absX + width, absY + 1, craftable ? (hovered ? 0xFFFFFFFF : 0xFF2C2C36) : 0xFF352020);
                drawRect(absX, absY + height - 1, absX + width, absY + height, craftable ? (hovered ? 0xFFFFFFFF : 0xFF2C2C36) : 0xFF352020);
                drawRect(absX, absY + 1, absX + 1, absY + height - 1, craftable ? (hovered ? 0xFFFFFFFF : 0xFF2C2C36) : 0xFF352020);
                drawRect(absX + width - 1, absY + 1, absX + width, absY + height - 1, craftable ? (hovered ? 0xFFFFFFFF : 0xFF2C2C36) : 0xFF352020);
                String btnText = craftable ? "Forjar" : "Faltan Materiales";
                mc.fontRenderer.drawString(btnText, absX + (width - mc.fontRenderer.getStringWidth(btnText)) / 2, absY + 5, craftable ? (hovered ? 0xFFFFFFFF : 0xFF00C853) : 0xFFFF5555);
            }
        };
        windowPanel.addChild(forgeBtn);
        rootPanel.addChild(windowPanel);
    }
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveredTooltip = null;
        this.hoveredIngredient = null;
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hoveredTooltip != null) {
            drawHoveringText(this.hoveredTooltip, mouseX, mouseY);
        } else if (this.hoveredIngredient != null) {
            renderToolTip(this.hoveredIngredient, mouseX, mouseY);
        }
    }
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (rootPanel != null) {
            rootPanel.drawComponent(mouseX, mouseY, partialTicks);
        }
    }
}