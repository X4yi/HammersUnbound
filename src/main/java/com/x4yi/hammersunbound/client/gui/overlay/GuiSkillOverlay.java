package com.x4yi.hammersunbound.client.gui.overlay;
import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.item.base.ItemHammer;
import com.x4yi.hammersunbound.item.warhammer.WarHammerItem;
import com.x4yi.hammersunbound.capability.IBloodPactCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.List;
public class GuiSkillOverlay extends Gui {
    private static final net.minecraft.util.ResourceLocation VIGNETTE_TEX = new net.minecraft.util.ResourceLocation("textures/misc/vignette.png");
    private static final net.minecraft.util.ResourceLocation BLOOD_OVERLAY_TEX = new net.minecraft.util.ResourceLocation("hammersunbound:textures/misc/blood_overlay.png");
    private float currentFade = 0.0f;
    private long lastTime = -1;
    @SubscribeEvent
    public void onRenderOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;
        boolean isActive = false;
        int remainingTicks = 0;
        if (mc.player.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
            IBloodPactCapability cap = mc.player.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect() != null) {
                isActive = cap.getBloodPactEffect().isActive();
                remainingTicks = cap.getBloodPactEffect().getRemainingTicks();
            }
        }
        long now = System.currentTimeMillis();
        if (lastTime == -1) lastTime = now;
        float dt = (now - lastTime) / 1000.0f;
        lastTime = now;
        if (isActive) {
            if (remainingTicks < 20) {
                currentFade -= dt;
            } else {
                currentFade += dt;
            }
        } else {
            currentFade -= dt * 2.0f;
        }
        currentFade = Math.max(0.0f, Math.min(1.0f, currentFade));
        if (currentFade > 0) {
            renderBloodVignette(event.getResolution(), currentFade);
        }
    }
    private void renderBloodVignette(ScaledResolution sr, float fade) {
        Minecraft mc = Minecraft.getMinecraft();
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        float baseAlpha = 0.2F;
        float alpha = baseAlpha * fade;
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha * 0.3F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(BLOOD_OVERLAY_TEX);
        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double)height, -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos((double)width, (double)height, -90.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos((double)width, 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 0.2F, 0.2F, alpha * 1.5F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(VIGNETTE_TEX);
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
    private static class CooldownInfo {
        float percentage;
        int color;
        String label;
        public CooldownInfo(float percentage, int color, String label) {
            this.percentage = percentage;
            this.color = color;
            this.label = label;
        }
    }
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof ItemHammer)) return;
        ItemHammer hammer = (ItemHammer) heldItem.getItem();
        List<CooldownInfo> cooldowns = new ArrayList<>();
        float vanillaCooldown = mc.player.getCooldownTracker().getCooldown(hammer, event.getPartialTicks());
        if (vanillaCooldown > 0) {
            cooldowns.add(new CooldownInfo(vanillaCooldown, 0xBB000000, ""));
        }
        if (hammer instanceof WarHammerItem && heldItem.hasTagCompound()) {
            NBTTagCompound nbt = heldItem.getTagCompound();
            long currentTime = mc.world.getTotalWorldTime();
            long skillEnd = nbt.getLong("SkillCooldown");
            if (skillEnd > currentTime) {
                float pct = (float)(skillEnd - currentTime) / 600.0f;
                cooldowns.add(new CooldownInfo(Math.min(1.0f, pct), 0xAAFF4444, "P"));
            }
            long skyEnd = nbt.getLong("SkybreakerCooldown");
            if (skyEnd > currentTime) {
                float pct = (float)(skyEnd - currentTime) / 600.0f;
                cooldowns.add(new CooldownInfo(Math.min(1.0f, pct), 0xAA44AAFF, "S"));
            }
        }
        if (cooldowns.isEmpty()) {
            cooldowns.add(new CooldownInfo(0.0f, 0x00000000, ""));
        }
        ScaledResolution sr = event.getResolution();
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();
        int size = 24;
        int padding = 10;
        int startX = 0;
        int startY = 0;
        switch (ClientConfig.uiOverlayPosition) {
            case 0:
                startX = screenWidth - padding - (size * cooldowns.size()) - ((cooldowns.size()-1)*4);
                startY = screenHeight - size - padding;
                break;
            case 1:
                startX = screenWidth / 2 - (size * cooldowns.size() + (cooldowns.size()-1)*4) / 2;
                startY = screenHeight - 60;
                break;
            case 2:
                startX = padding;
                startY = screenHeight - size - padding;
                break;
            case 3:
                startX = screenWidth - padding - (size * cooldowns.size()) - ((cooldowns.size()-1)*4);
                startY = padding;
                break;
            case 4:
                startX = padding;
                startY = padding;
                break;
            default:
                startX = screenWidth - padding - (size * cooldowns.size()) - ((cooldowns.size()-1)*4);
                startY = screenHeight - size - padding;
                break;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        for (int i = 0; i < cooldowns.size(); i++) {
            CooldownInfo cd = cooldowns.get(i);
            int x = startX + i * (size + 4);
            int y = startY;
            int borderColor = cd.percentage > 0 ? 0xAA222222 : 0xAAFFBB00;
            int bgColor = 0x88000000;
            drawRect(x - 2, y - 2, x + size + 2, y + size + 2, borderColor);
            drawRect(x, y, x + size, y + size, bgColor);
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.translate(x + 4, y + 4, 0);
            mc.getRenderItem().renderItemIntoGUI(heldItem, 0, 0);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
            if (cd.percentage > 0) {
                int overlayHeight = (int) (size * cd.percentage);
                int overlayY = y + size - overlayHeight;
                drawRect(x, overlayY, x + size, y + size, cd.color);
            }
            if (!cd.label.isEmpty()) {
                mc.fontRenderer.drawStringWithShadow(cd.label, x + 2, y + 2, 0xFFFFFFFF);
            }
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
                mc.fontRenderer.drawStringWithShadow(text, startX - 10, startY - 32, 0xFFFFFFFF);
                mc.fontRenderer.drawStringWithShadow(burstText, startX - 10, startY - 22, 0xFFFFFFFF);
                mc.fontRenderer.drawStringWithShadow(dmgText, startX - 10, startY - 12, 0xFFFFFFFF);
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