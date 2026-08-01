package com.x4yi.hammersunbound.client.gui;
import com.x4yi.hammersunbound.inventory.ContainerHammerForge;
import com.x4yi.hammersunbound.crafting.HammerForgeRecipe;
import com.x4yi.hammersunbound.crafting.HammerRecipeManager;
import com.x4yi.hammersunbound.network.ModNetworkHandler;
import com.x4yi.hammersunbound.network.PacketForgeHammer;
import com.x4yi.hammersunbound.init.ModItems;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.resources.I18n;
import java.io.IOException;
import java.util.List;
public class GuiHammerForge extends GuiContainer {
    private final EntityPlayer player;
    private List<HammerForgeRecipe> recipes;
    private String currentType = "warhammer";
    private String currentMaterial = "iron";
    private final String[] TYPES = {"warhammer", "spikehammer"};
    private final String[] MATERIALS = {"diamond", "gold", "iron", "stone", "wood"};
    private float animationProgress = 0.0f;
    private long lastTime = 0;
    public GuiHammerForge(EntityPlayer player) {
        super(new ContainerHammerForge(player.inventory));
        this.player = player;
        this.recipes = HammerRecipeManager.getRecipes();
        this.xSize = 210;
        this.ySize = 220;
        autoSelectBestMaterial(this.currentType);
    }
    @Override
    public void initGui() {
        super.initGui();
        this.lastTime = System.currentTimeMillis();
        this.animationProgress = 0.0f;
    }
    private void updateAnimation() {
        long current = System.currentTimeMillis();
        float delta = (current - lastTime) / 1000.0f;
        lastTime = current;
        if (animationProgress < 1.0f) {
            animationProgress += delta * 5.0f;
            if (animationProgress > 1.0f) animationProgress = 1.0f;
        }
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
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
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
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        updateAnimation();
        GlStateManager.pushMatrix();
        if (animationProgress < 1.0f) {
            float scale = 0.9f + (0.1f * animationProgress);
            GlStateManager.translate(width / 2.0f, height / 2.0f, 0);
            GlStateManager.scale(scale, scale, 1.0f);
            GlStateManager.translate(-width / 2.0f, -height / 2.0f, 0);
        }
        int startX = (width - xSize) / 2;
        int startY = (height - ySize) / 2;
        int tabY = startY + 20;
        for (String type : TYPES) {
            boolean active = type.equals(currentType);
            int tabX = startX - 25;
            boolean tabHovered = mouseX >= tabX && mouseX <= startX && mouseY >= tabY && mouseY <= tabY + 28;
            drawRect(tabX, tabY, startX, tabY + 28, active ? 0xFF18181F : (tabHovered ? 0xFF121217 : 0xFF0B0B0D));
            drawBorder(tabX, tabY, startX, tabY + 28, active ? 0xFF00C853 : 0xFF222228);
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(getTypeDisplayItem(type), tabX + 4, tabY + 6);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
            tabY += 32;
        }
        drawRect(startX - 2, startY - 2, startX + xSize + 2, startY + ySize + 2, 0x55000000);
        drawRect(startX, startY, startX + xSize, startY + ySize, 0xFF0B0B0D);
        drawBorder(startX, startY, startX + xSize, startY + ySize, 0xFF222228);
        drawRect(startX, startY, startX + xSize, startY + 16, 0xFF08080A);
        drawRect(startX, startY + 15, startX + xSize, startY + 16, 0xFF222228);
        fontRenderer.drawString("Hammer Forge", startX + 8, startY + 4, 0xFFFFFFFF);
        HammerForgeRecipe currentRecipe = getCurrentRecipe();
        boolean craftable = canCraft(currentRecipe);
        int matW = 20;
        int matX = startX + (xSize / 2) - ((MATERIALS.length * matW) / 2);
        int matY = startY + 20;
        for (String mat : MATERIALS) {
            boolean active = mat.equals(currentMaterial);
            boolean matHovered = mouseX >= matX && mouseX <= matX + matW && mouseY >= matY && mouseY <= matY + matW;
            drawRect(matX, matY, matX + matW, matY + matW, active ? 0xFF18181F : (matHovered ? 0xFF121217 : 0xFF0F0F12));
            drawBorder(matX, matY, matX + matW, matY + matW, active ? 0xFF00C853 : 0xFF222228);
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(getMaterialDisplayItem(mat), matX + 2, matY + 2);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
            matX += matW;
        }
        if (currentRecipe == null) {
            drawCenteredString("Receta no encontrada", startX + xSize / 2, startY + 50, 0xFF5555);
        } else {
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            mc.getRenderItem().renderItemAndEffectIntoGUI(currentRecipe.getResult(), startX + (xSize/2) - 8, startY + 45);
            mc.getRenderItem().renderItemOverlayIntoGUI(fontRenderer, currentRecipe.getResult(), startX + (xSize/2) - 8, startY + 45, null);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
            drawCenteredString(currentRecipe.getResult().getDisplayName(), startX + (xSize/2), startY + 65, 0xFFD700);
            int ingStartX = startX + 15;
            int ingX = ingStartX;
            int ingY = startY + 75;
            int maxRowWidth = startX + xSize - 15;
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
                int textWidth = fontRenderer.getStringWidth(text);
                int elementWidth = 16 + 2 + textWidth + 15;
                if (ingX + elementWidth > maxRowWidth && ingX != ingStartX) {
                    ingX = ingStartX;
                    ingY += 16;
                }
                int color = found >= ic.getCount() ? 0x55FF55 : 0xFF5555;
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(displayStack, ingX, ingY);
                RenderHelper.disableStandardItemLighting();
                fontRenderer.drawString(text, ingX + 18, ingY + 4, color);
                ingX += elementWidth;
            }
            int btnWidth = 100;
            int saveX1 = startX + (xSize / 2) - (btnWidth / 2);
            int saveY1 = startY + 105;
            int saveX2 = saveX1 + btnWidth;
            int saveY2 = saveY1 + 18;
            boolean saveHovered = mouseX >= saveX1 && mouseX <= saveX2 && mouseY >= saveY1 && mouseY <= saveY2;
            if (!craftable) saveHovered = false;
            drawRect(saveX1, saveY1, saveX2, saveY2, craftable ? (saveHovered ? 0xFF00C853 : 0xFF16161E) : 0xFF16161E);
            drawBorder(saveX1, saveY1, saveX2, saveY2, craftable ? (saveHovered ? 0xFFFFFFFF : 0xFF2C2C36) : 0xFF352020);
            String btnText = craftable ? "Forjar" : "Faltan Materiales";
            drawCenteredString(btnText, saveX1 + (btnWidth/2), saveY1 + 5, craftable ? (saveHovered ? 0xFFFFFFFF : 0xFF00C853) : 0xFFFF5555);
        }
        int invStartX = startX + 24;
        int invStartY = startY + 138;
        fontRenderer.drawString(I18n.format("container.inventory"), invStartX, invStartY - 10, 0xFF888892);
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
            int slotY = startY + 196 - 1;
            drawRect(slotX, slotY, slotX + 18, slotY + 18, 0xFF121217);
            drawBorder(slotX, slotY, slotX + 18, slotY + 18, 0xFF222228);
        }
        GlStateManager.popMatrix();
    }
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int startX = (width - xSize) / 2;
        int startY = (height - ySize) / 2;
        int tabY = startY + 20;
        for (String type : TYPES) {
            int tabX = startX - 25;
            if (mouseX >= tabX && mouseX <= startX && mouseY >= tabY && mouseY <= tabY + 28) {
                drawHoveringText(type.substring(0, 1).toUpperCase() + type.substring(1), mouseX - startX, mouseY - startY);
            }
            tabY += 32;
        }
        int matW = 20;
        int matX = startX + (xSize / 2) - ((MATERIALS.length * matW) / 2);
        int matY = startY + 20;
        for (String mat : MATERIALS) {
            if (mouseX >= matX && mouseX <= matX + matW && mouseY >= matY && mouseY <= matY + matW) {
                drawHoveringText(mat.substring(0, 1).toUpperCase() + mat.substring(1), mouseX - startX, mouseY - startY);
            }
            matX += matW;
        }
        HammerForgeRecipe currentRecipe = getCurrentRecipe();
        if (currentRecipe != null) {
            int ingStartX = startX + 15;
            int ingX = ingStartX;
            int ingY = startY + 75;
            int maxRowWidth = startX + xSize - 15;
            for (HammerForgeRecipe.IngredientCount ic : currentRecipe.getIngredients()) {
                ItemStack[] matchingStacks = ic.getIngredient().getMatchingStacks();
                if (matchingStacks.length > 0) {
                    int found = 0;
                    for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                        ItemStack stack = player.inventory.getStackInSlot(i);
                        if (ic.getIngredient().apply(stack)) {
                            found += stack.getCount();
                        }
                    }
                    String text = ic.getCount() + " (" + found + ")";
                    int textWidth = fontRenderer.getStringWidth(text);
                    int elementWidth = 16 + 2 + textWidth + 15;
                    if (ingX + elementWidth > maxRowWidth && ingX != ingStartX) {
                        ingX = ingStartX;
                        ingY += 16;
                    }
                    if (mouseX >= ingX && mouseX < ingX + 16 && mouseY >= ingY && mouseY < ingY + 16) {
                        renderToolTip(matchingStacks[0], mouseX - startX, mouseY - startY);
                    }
                    ingX += elementWidth;
                }
            }
        }
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
        if (mouseButton != 0) return;
        int startX = (width - xSize) / 2;
        int startY = (height - ySize) / 2;
        int tabY = startY + 20;
        for (String type : TYPES) {
            int tabX = startX - 25;
            if (mouseX >= tabX && mouseX <= startX && mouseY >= tabY && mouseY <= tabY + 28) {
                if (!currentType.equals(type)) {
                    currentType = type;
                    autoSelectBestMaterial(currentType);
                    playClickSound();
                }
                return;
            }
            tabY += 32;
        }
        int matW = 20;
        int matX = startX + (xSize / 2) - ((MATERIALS.length * matW) / 2);
        int matY = startY + 20;
        for (String mat : MATERIALS) {
            if (mouseX >= matX && mouseX <= matX + matW && mouseY >= matY && mouseY <= matY + matW) {
                if (!currentMaterial.equals(mat)) {
                    currentMaterial = mat;
                    playClickSound();
                }
                return;
            }
            matX += matW;
        }
        HammerForgeRecipe currentRecipe = getCurrentRecipe();
        if (canCraft(currentRecipe)) {
            int btnWidth = 100;
            int saveX1 = startX + (xSize / 2) - (btnWidth / 2);
            int saveY1 = startY + 105;
            int saveX2 = saveX1 + btnWidth;
            int saveY2 = saveY1 + 18;
            if (mouseX >= saveX1 && mouseX <= saveX2 && mouseY >= saveY1 && mouseY <= saveY2) {
                playClickSound();
                mc.player.playSound(net.minecraft.init.SoundEvents.BLOCK_ANVIL_USE, 1.0f, 1.0f);
                ModNetworkHandler.INSTANCE.sendToServer(new PacketForgeHammer(currentRecipe.getId()));
                return;
            }
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
    public boolean doesGuiPauseGame() {
        return false;
    }
}