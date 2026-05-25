package com.x4yi.hammersunbound.client.gui.overlay;

import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.item.base.ItemHammer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiSkillOverlay extends Gui {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof ItemHammer)) return;

        ItemHammer hammer = (ItemHammer) heldItem.getItem();
        float cooldown = mc.player.getCooldownTracker().getCooldown(hammer, event.getPartialTicks());

        ScaledResolution sr = event.getResolution();
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        int x = 0;
        int y = 0;
        int size = 24;
        int padding = 10;

        // 0: Bottom Right, 1: Bottom Center, 2: Bottom Left, 3: Top Right, 4: Top Left
        switch (ClientConfig.uiOverlayPosition) {
            case 0:
                x = screenWidth - size - padding;
                y = screenHeight - size - padding;
                break;
            case 1:
                x = screenWidth / 2 - size / 2;
                y = screenHeight - 60; // Above hotbar
                break;
            case 2:
                x = padding;
                y = screenHeight - size - padding;
                break;
            case 3:
                x = screenWidth - size - padding;
                y = padding;
                break;
            case 4:
                x = padding;
                y = padding;
                break;
            default:
                x = screenWidth - size - padding;
                y = screenHeight - size - padding;
                break;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        // Draw elegant RPG background border
        int borderColor = cooldown > 0 ? 0xAA222222 : 0xAAFFBB00;
        int bgColor = 0x88000000;
        drawRect(x - 2, y - 2, x + size + 2, y + size + 2, borderColor);
        drawRect(x, y, x + size, y + size, bgColor);

        // Draw item icon
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.translate(x + 4, y + 4, 0);
        // We use RenderItem to draw the icon scaled up a bit
        mc.getRenderItem().renderItemIntoGUI(heldItem, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        // Draw Cooldown radial/linear dark overlay
        if (cooldown > 0) {
            int overlayHeight = (int) (size * cooldown);
            int overlayY = y + size - overlayHeight;
            drawRect(x, overlayY, x + size, y + size, 0xBB000000); // Darker shadow moving down
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
