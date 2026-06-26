package com.x4yi.hammersunbound.client.gui.overlay;
import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.item.base.ItemHammer;
import com.x4yi.hammersunbound.capability.IBloodPactCapability;
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
    private static final net.minecraft.util.ResourceLocation VIGNETTE_TEX = new net.minecraft.util.ResourceLocation("textures/misc/vignette.png");

    @SubscribeEvent
    public void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;
        if (mc.player.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
            IBloodPactCapability cap = mc.player.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect() != null && cap.getBloodPactEffect().isActive()) {
                int remainingTicks = cap.getBloodPactEffect().getRemainingTicks();
                if (remainingTicks > 0) {
                    renderBloodVignette(event.getResolution(), remainingTicks);
                }
            }
        }
    }

    private void renderBloodVignette(ScaledResolution sr, int remainingTicks) {
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        float alpha = Math.min(0.5F, (float) remainingTicks / 100.0F);
        if (remainingTicks < 40) {
            float pulse = (float) Math.sin((double) System.currentTimeMillis() / 150.0D) * 0.15F + 0.35F;
            alpha = Math.min(alpha, pulse);
        }
        GlStateManager.color(alpha, 0.0F, 0.0F, alpha);
        Minecraft.getMinecraft().getTextureManager().bindTexture(VIGNETTE_TEX);
        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double)height, -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos((double)width, (double)height, -90.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos((double)width, 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

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

        switch (ClientConfig.uiOverlayPosition) {
            case 0:
                x = screenWidth - size - padding;
                y = screenHeight - size - padding;
                break;
            case 1:
                x = screenWidth / 2 - size / 2;
                y = screenHeight - 60;
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

        int borderColor = cooldown > 0 ? 0xAA222222 : 0xAAFFBB00;
        int bgColor = 0x88000000;
        drawRect(x - 2, y - 2, x + size + 2, y + size + 2, borderColor);
        drawRect(x, y, x + size, y + size, bgColor);

        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.translate(x + 4, y + 4, 0);

        mc.getRenderItem().renderItemIntoGUI(heldItem, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();

        if (cooldown > 0) {
            int overlayHeight = (int) (size * cooldown);
            int overlayY = y + size - overlayHeight;
            drawRect(x, overlayY, x + size, y + size, 0xBB000000);
        }

        if (mc.player.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
            IBloodPactCapability cap = mc.player.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect() != null && cap.getBloodPactEffect().isActive()) {
                int madness = cap.getBloodPactEffect().getMadness();
                int burstTimer = cap.getBloodPactEffect().getBurstTimer();
                float dmg = cap.getBloodPactEffect().getAccumulatedDamage();
                
                String text = net.minecraft.util.text.TextFormatting.RED + "Locura: " + madness;
                String burstText = net.minecraft.util.text.TextFormatting.DARK_RED + "Burst en: " + String.format("%.1fs", burstTimer / 20.0f);
                String dmgText = net.minecraft.util.text.TextFormatting.DARK_RED + "Daño Acum: " + String.format("%.0f", dmg);
                
                mc.fontRenderer.drawStringWithShadow(text, x - 10, y - 32, 0xFFFFFFFF);
                mc.fontRenderer.drawStringWithShadow(burstText, x - 10, y - 22, 0xFFFFFFFF);
                mc.fontRenderer.drawStringWithShadow(dmgText, x - 10, y - 12, 0xFFFFFFFF);

                // Alert when Ping Pong phase 2 is active
                if (cap.getBloodPactEffect().getPingPongPhase() == 2) {
                    String ppAlert = net.minecraft.util.text.TextFormatting.BOLD.toString() + net.minecraft.util.text.TextFormatting.RED.toString() + "¡GOLPEA!";
                    int alertWidth = mc.fontRenderer.getStringWidth(ppAlert);
                    float pulse = (float) Math.sin((double) System.currentTimeMillis() / 80.0D) * 0.15F + 0.85F;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(screenWidth / 2.0F, screenHeight / 2.0F + 15.0F, 0.0F);
                    GlStateManager.scale(pulse, pulse, 1.0F);
                    mc.fontRenderer.drawStringWithShadow(ppAlert, -alertWidth / 2.0F, -4.0F, 0xFFFFFFFF);
                    GlStateManager.popMatrix();
                }
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}