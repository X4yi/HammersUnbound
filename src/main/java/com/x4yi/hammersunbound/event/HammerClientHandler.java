package com.x4yi.hammersunbound.event;
import com.x4yi.hammersunbound.item.base.ItemHammer;
import com.x4yi.hammersunbound.client.gui.DevWarningOverlay;
import com.x4yi.hammersunbound.client.gui.GuiChangelogScreen;
import com.x4yi.hammersunbound.config.ClientConfig;
import com.x4yi.hammersunbound.network.PacketBloodPactVisual;
import com.x4yi.hammersunbound.capability.IBleedingCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import java.util.Iterator;
import java.util.List;
public class HammerClientHandler {
    private boolean devWarningShownThisSession = false;
    @SubscribeEvent
    public void onClientConnected(net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent event) {
        com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToServer(new com.x4yi.hammersunbound.network.PacketRequestSyncConfig());
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.mouseHelper != null && !(mc.mouseHelper instanceof com.x4yi.hammersunbound.client.input.StunMouseHelper)) {
            mc.mouseHelper = new com.x4yi.hammersunbound.client.input.StunMouseHelper(mc.mouseHelper);
        }
    }
    @SubscribeEvent
    public void onLivingUpdate(net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || !entity.world.isRemote) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.mouseHelper != null && !(mc.mouseHelper instanceof com.x4yi.hammersunbound.client.input.StunMouseHelper)) {
            mc.mouseHelper = new com.x4yi.hammersunbound.client.input.StunMouseHelper(mc.mouseHelper);
        }
    }
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!ClientConfig.bloodPactEnabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null) return;
        List<PacketBloodPactVisual.BloodPactVisual> visuals = PacketBloodPactVisual.getActiveVisuals(mc.world);
        if (visuals.isEmpty()) return;
        float partialTicks = event.getPartialTicks();
        Entity renderView = mc.getRenderViewEntity();
        if (renderView == null) return;
        double rx = renderView.lastTickPosX + (renderView.posX - renderView.lastTickPosX) * partialTicks;
        double ry = renderView.lastTickPosY + (renderView.posY - renderView.lastTickPosY) * partialTicks;
        double rz = renderView.lastTickPosZ + (renderView.posZ - renderView.lastTickPosZ) * partialTicks;
        for (PacketBloodPactVisual.BloodPactVisual visual : visuals) {
            Vec3d playerPos = visual.getPlayerPos(mc.world);
            if (playerPos == null) continue;
            for (int targetId : visual.targetEntityIds) {
                Vec3d targetPos = visual.getTargetPos(mc.world, targetId);
                if (targetPos == null) continue;
                Vec3d renderPlayerPos = new Vec3d(playerPos.x - rx, playerPos.y - ry, playerPos.z - rz);
                Vec3d renderTargetPos = new Vec3d(targetPos.x - rx, targetPos.y - ry, targetPos.z - rz);
                renderBloodTether(renderPlayerPos, renderTargetPos, partialTicks);
            }
            Entity player = mc.world.getEntityByID(visual.playerEntityId);
            if (player != null && player.ticksExisted % 3 == 0 && !mc.isGamePaused()) {
                double radius = 5.0D;
                int count = 2;
                for (int i = 0; i < count; i++) {
                    double angle = mc.world.rand.nextDouble() * 2.0D * Math.PI;
                    double px = player.posX + Math.cos(angle) * radius;
                    double py = player.posY + mc.world.rand.nextDouble() * 0.4D;
                    double pz = player.posZ + Math.sin(angle) * radius;
                    double mx = (mc.world.rand.nextDouble() - 0.5D) * 0.01D;
                    double my = 0.01D + mc.world.rand.nextDouble() * 0.01D;
                    double mz = (mc.world.rand.nextDouble() - 0.5D) * 0.01D;
                    com.x4yi.hammersunbound.client.particle.ParticleBlood bloodParticle = 
                        new com.x4yi.hammersunbound.client.particle.ParticleBlood(mc.world, px, py, pz, mx, my, mz);
                    mc.effectRenderer.addEffect(bloodParticle);
                }
            }
        }
    }
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof ItemHammer)) return;
        List<String> tooltip = event.getToolTip();
        String mainHandHeader = net.minecraft.client.resources.I18n.format("item.modifiers.mainhand");
        int mainHandIndex = -1;
        for (int i = 0; i < tooltip.size(); i++) {
            String line = TextFormatting.getTextWithoutFormattingCodes(tooltip.get(i));
            if (line != null && (line.equals(mainHandHeader) || line.toLowerCase().contains("main hand") || line.toLowerCase().contains("mainhand"))) {
                mainHandIndex = i;
                break;
            }
        }
        if (mainHandIndex != -1) {
            int headerStart = mainHandIndex;
            if (mainHandIndex > 0 && TextFormatting.getTextWithoutFormattingCodes(tooltip.get(mainHandIndex - 1)).trim().isEmpty()) {
                headerStart = mainHandIndex - 1;
            }
            int removeCount = 0;
            for (int i = headerStart; i < tooltip.size(); i++) {
                String line = TextFormatting.getTextWithoutFormattingCodes(tooltip.get(i)).trim();
                if (i == headerStart || i == mainHandIndex || line.isEmpty() || line.startsWith("+") || line.startsWith("-") || line.contains("Attack Damage") || line.contains("Attack Speed")) {
                    removeCount++;
                } else {
                    break;
                }
            }
            for (int i = 0; i < removeCount; i++) {
                tooltip.remove(headerStart);
            }
        }
    }
    private void renderBloodTether(Vec3d from, Vec3d to, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        int segments = 20;
        float amplitude = 0.3f;
        GlStateManager.color(0.8f, 0.0f, 0.0f, 0.6f);
        GL11.glLineWidth(3.0f);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            double x = from.x + dx * t;
            double y = from.y + dy * t + Math.sin(t * (float) Math.PI * 4.0f) * amplitude;
            double z = from.z + dz * t;
            GL11.glVertex3d(x, y, z);
        }
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (int i = 0; i <= segments; i++) {
            float t = (float) i / segments;
            double x = from.x + dx * t;
            double y = from.y + dy * t + Math.cos(t * (float) Math.PI * 4.0f) * amplitude;
            double z = from.z + dz * t;
            GL11.glVertex3d(x, y, z);
        }
        GL11.glEnd();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
    @SubscribeEvent
    public void onClientDisconnect(net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        PacketBloodPactVisual.getActiveVisuals(null).clear();
    }
    private float lockedYaw = 0.0F;
    private float lockedPitch = 0.0F;
    private boolean wasStunned = false;
    @SubscribeEvent
    public void onInputUpdate(net.minecraftforge.client.event.InputUpdateEvent event) {
        if (com.x4yi.hammersunbound.init.ModPotions.STUN != null && event.getEntityPlayer().isPotionActive(com.x4yi.hammersunbound.init.ModPotions.STUN)) {
            net.minecraft.util.MovementInput input = event.getMovementInput();
            input.moveForward = 0.0F;
            input.moveStrafe = 0.0F;
            input.forwardKeyDown = false;
            input.backKeyDown = false;
            input.leftKeyDown = false;
            input.rightKeyDown = false;
            input.jump = false;
            input.sneak = false;
        }
    }
    @SubscribeEvent
    public void onRenderTick(net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            if (com.x4yi.hammersunbound.init.ModPotions.STUN != null && mc.player.isPotionActive(com.x4yi.hammersunbound.init.ModPotions.STUN)) {
                if (mc.mouseHelper != null) {
                    mc.mouseHelper.deltaX = 0;
                    mc.mouseHelper.deltaY = 0;
                }
                if (!wasStunned) {
                    lockedYaw = mc.player.rotationYaw;
                    lockedPitch = mc.player.rotationPitch;
                    wasStunned = true;
                }
                mc.player.rotationYaw = lockedYaw;
                mc.player.rotationPitch = lockedPitch;
                mc.player.prevRotationYaw = lockedYaw;
                mc.player.prevRotationPitch = lockedPitch;
            } else {
                wasStunned = false;
            }
        }
    }
    @SubscribeEvent
    public void onGuiOpen(net.minecraftforge.client.event.GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu && ClientConfig.showDevWarning && !devWarningShownThisSession) {
            devWarningShownThisSession = true;
            event.setGui(new DevWarningOverlay(event.getGui()));
        }
    }
    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof GuiMainMenu && ClientConfig.showChangelogButton) {
            GuiButton changelogBtn = new GuiButton(9999, 6, 6, 74, 16, "Log") {
                @Override
                public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                    if (!this.visible) return;
                    this.hovered = mouseX >= this.x && mouseY >= this.y
                            && mouseX < this.x + this.width && mouseY < this.y + this.height;
                    int bg = this.hovered ? 0xEE16161E : 0xCC0B0B0D;
                    int border = this.hovered ? 0xFF00C853 : 0xFF2C2C36;
                    int accent = this.hovered ? 0xFF00E676 : 0xFF00A846;
                    int text = this.hovered ? 0xFFFFFFFF : 0xFFE0E0E6;
                    GlStateManager.disableTexture2D();
                    drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, 0x66000000);
                    drawRect(this.x, this.y, this.x + this.width, this.y + this.height, bg);
                    drawRect(this.x, this.y, this.x + 2, this.y + this.height, accent);
                    drawRect(this.x, this.y, this.x + this.width, this.y + 1, border);
                    drawRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, border);
                    drawRect(this.x, this.y, this.x + 1, this.y + this.height, border);
                    drawRect(this.x + this.width - 1, this.y, this.x + this.width, this.y + this.height, border);
                    GlStateManager.enableTexture2D();
                    mc.fontRenderer.drawString("\u00A7lHU", this.x + 7, this.y + 4, accent);
                    mc.fontRenderer.drawString("Log", this.x + 30, this.y + 4, text);
                }
                @Override
                public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                    if (super.mousePressed(mc, mouseX, mouseY)) {
                        mc.displayGuiScreen(new GuiChangelogScreen(event.getGui()));
                        return true;
                    }
                    return false;
                }
            };
            event.getButtonList().add(changelogBtn);
        }
    }

    @SubscribeEvent
    public void onLeftClickAir(net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntityPlayer() == Minecraft.getMinecraft().player) {
            handleClientLeftClick(null);
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntityPlayer() == Minecraft.getMinecraft().player && event.getWorld().isRemote) {
            handleClientLeftClick(null);
        }
    }

    @SubscribeEvent
    public void onAttackEntity(net.minecraftforge.event.entity.player.AttackEntityEvent event) {
        if (event.getEntityPlayer() == Minecraft.getMinecraft().player && event.getEntityPlayer().world.isRemote) {
            handleClientLeftClick(event.getTarget());
        }
    }

    private void handleClientLeftClick(Entity target) {
        net.minecraft.entity.player.EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;
        if (player.hasCapability(com.x4yi.hammersunbound.capability.IBloodPactCapability.CAPABILITY, null)) {
            com.x4yi.hammersunbound.capability.IBloodPactCapability cap = player.getCapability(com.x4yi.hammersunbound.capability.IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect() != null && cap.getBloodPactEffect().isActive()) {
                ItemStack held = player.getHeldItemMainhand();
                if (!held.isEmpty() && held.getItem() instanceof com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem) {
                    int targetId = target != null ? target.getEntityId() : -1;
                    com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToServer(
                        new com.x4yi.hammersunbound.network.PacketSpikeHammerAoE(targetId)
                    );
                }
            }
        }
    }
}