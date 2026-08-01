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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.ActiveRenderInfo;
import org.lwjgl.opengl.GL11;
public class HammerClientHandler {
    private boolean devWarningShownThisSession = false;
    private static final List<com.x4yi.hammersunbound.client.particle.ParticleBloodPact> bloodParticles = new ArrayList<>();
    private static final Map<Integer, com.x4yi.hammersunbound.client.render.TetherRenderer> tethers = new HashMap<>();
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
        if (bloodParticles.isEmpty()) return;
        float partialTicks = event.getPartialTicks();
        Entity renderView = mc.getRenderViewEntity();
        if (renderView == null) return;
        double rx = renderView.lastTickPosX + (renderView.posX - renderView.lastTickPosX) * partialTicks;
        double ry = renderView.lastTickPosY + (renderView.posY - renderView.lastTickPosY) * partialTicks;
        double rz = renderView.lastTickPosZ + (renderView.posZ - renderView.lastTickPosZ) * partialTicks;
        float pitch = renderView.prevRotationPitch + (renderView.rotationPitch - renderView.prevRotationPitch) * partialTicks;
        float yaw = renderView.prevRotationYaw + (renderView.rotationYaw - renderView.prevRotationYaw) * partialTicks;
        if (mc.gameSettings.thirdPersonView == 2) {
            yaw += 180.0F;
            pitch = -pitch;
        }
        float rotationX = net.minecraft.util.math.MathHelper.cos(yaw * 0.017453292F);
        float rotationZ = net.minecraft.util.math.MathHelper.sin(yaw * 0.017453292F);
        float rotationYZ = -rotationZ * net.minecraft.util.math.MathHelper.sin(pitch * 0.017453292F);
        float rotationXY = rotationX * net.minecraft.util.math.MathHelper.sin(pitch * 0.017453292F);
        float rotationXZ = net.minecraft.util.math.MathHelper.cos(pitch * 0.017453292F);
        com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosX = rx;
        com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosY = ry;
        com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosZ = rz;
        mc.getTextureManager().bindTexture(new net.minecraft.util.ResourceLocation("hammersunbound:textures/particle/blood.png"));
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        float fCos = net.minecraft.util.math.MathHelper.cos(-pitch * 0.017453292F);
        float fSin = net.minecraft.util.math.MathHelper.sin(-pitch * 0.017453292F);
        float cosYaw = net.minecraft.util.math.MathHelper.cos(yaw * 0.017453292F);
        float sinYaw = net.minecraft.util.math.MathHelper.sin(yaw * 0.017453292F);
        float camX = -sinYaw * fCos;
        float camY = fSin;
        float camZ = cosYaw * fCos;
        for (com.x4yi.hammersunbound.client.particle.ParticleBloodPact p : bloodParticles) {
            if (p.trailPositions.size() < 2) continue;
            int j = p.getBrightnessForRender(partialTicks);
            int lmapX = j >> 16 & 65535;
            int lmapY = j & 65535;
            float baseWidth = 0.3f * p.getScale();
            float totalPoints = (float) com.x4yi.hammersunbound.client.particle.ParticleBloodPact.MAX_TRAIL_LENGTH;
            for (int i = 0; i < p.trailPositions.size() - 1; i++) {
                net.minecraft.util.math.Vec3d p1 = p.trailPositions.get(i);
                net.minecraft.util.math.Vec3d p2 = p.trailPositions.get(i+1);
                float fade1 = 1.0f - (i / totalPoints);
                float fade2 = 1.0f - ((i+1) / totalPoints);
                float w1 = baseWidth * fade1;
                float w2 = baseWidth * fade2;
                float x1 = (float)(p1.x - com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosX);
                float y1 = (float)(p1.y - com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosY);
                float z1 = (float)(p1.z - com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosZ);
                float x2 = (float)(p2.x - com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosX);
                float y2 = (float)(p2.y - com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosY);
                float z2 = (float)(p2.z - com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosZ);
                float camDistFade1 = 1.0f;
                float camDistFade2 = 1.0f;
                if (mc.gameSettings.thirdPersonView == 0) {
                    float dist1 = (float)Math.sqrt(x1*x1 + y1*y1 + z1*z1);
                    float dist2 = (float)Math.sqrt(x2*x2 + y2*y2 + z2*z2);
                    if (dist1 < 2.5f) camDistFade1 = Math.max(0.0f, (dist1 - 1.0f) / 1.5f);
                    if (dist2 < 2.5f) camDistFade2 = Math.max(0.0f, (dist2 - 1.0f) / 1.5f);
                }
                float a1 = p.getAlpha() * fade1 * camDistFade1;
                float a2 = p.getAlpha() * fade2 * camDistFade2;
                float dirX = x2 - x1;
                float dirY = y2 - y1;
                float dirZ = z2 - z1;
                float crossX = camY * dirZ - camZ * dirY;
                float crossY = camZ * dirX - camX * dirZ;
                float crossZ = camX * dirY - camY * dirX;
                float length = (float)Math.sqrt(crossX*crossX + crossY*crossY + crossZ*crossZ);
                if (length > 0.0001f) {
                    crossX /= length; crossY /= length; crossZ /= length;
                } else {
                    crossX = -cosYaw; crossY = 0; crossZ = -sinYaw;
                }
                bufferbuilder.pos(x1 - crossX * w1, y1 - crossY * w1, z1 - crossZ * w1).tex(0, 0).color(p.getRed(), p.getGreen(), p.getBlue(), a1).lightmap(lmapX, lmapY).endVertex();
                bufferbuilder.pos(x1 + crossX * w1, y1 + crossY * w1, z1 + crossZ * w1).tex(1, 0).color(p.getRed(), p.getGreen(), p.getBlue(), a1).lightmap(lmapX, lmapY).endVertex();
                bufferbuilder.pos(x2 + crossX * w2, y2 + crossY * w2, z2 + crossZ * w2).tex(1, 1).color(p.getRed(), p.getGreen(), p.getBlue(), a2).lightmap(lmapX, lmapY).endVertex();
                bufferbuilder.pos(x2 - crossX * w2, y2 - crossY * w2, z2 - crossZ * w2).tex(0, 1).color(p.getRed(), p.getGreen(), p.getBlue(), a2).lightmap(lmapX, lmapY).endVertex();
            }
        }
        tessellator.draw();
        for (Map.Entry<Integer, com.x4yi.hammersunbound.client.render.TetherRenderer> entry : tethers.entrySet()) {
            float maxDist = 12.0f * (float)com.x4yi.hammersunbound.config.ServerConfig.spikehammerBloodPactRangeMultiplier;
            entry.getValue().render(renderView, partialTicks, maxDist, com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosX, com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosY, com.x4yi.hammersunbound.client.particle.ParticleBloodPact.interpPosZ);
        }
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
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
    public void onClientTick(net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent event) {
        if (event.phase != net.minecraftforge.fml.common.gameevent.TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world == null || mc.isGamePaused()) return;
        List<PacketBloodPactVisual.BloodPactVisual> visuals = PacketBloodPactVisual.getActiveVisuals(mc.world);
        for (PacketBloodPactVisual.BloodPactVisual visual : visuals) {
            Entity player = mc.world.getEntityByID(visual.playerEntityId);
            if (player != null && !player.isDead) {
                float radius = 5.0f;
                int madness = 0;
                int burstTimer = 200;
                int burstImpactTimer = 0;
                float accumulatedDamage = 0;
                boolean pingPong = false;
                if (player.hasCapability(com.x4yi.hammersunbound.capability.IBloodPactCapability.CAPABILITY, null)) {
                    com.x4yi.hammersunbound.capability.IBloodPactCapability cap = player.getCapability(com.x4yi.hammersunbound.capability.IBloodPactCapability.CAPABILITY, null);
                    if (cap != null && cap.getBloodPactEffect() != null) {
                        radius = cap.getBloodPactEffect().getFieldRadius();
                        madness = cap.getBloodPactEffect().getMadness();
                        burstTimer = cap.getBloodPactEffect().getBurstTimer();
                        burstImpactTimer = cap.getBloodPactEffect().getBurstImpactTimer();
                        accumulatedDamage = cap.getBloodPactEffect().getAccumulatedDamage();
                        pingPong = cap.getBloodPactEffect().getPingPongPhase() > 0;
                    }
                }
                int maxParticles = (int)(accumulatedDamage / 2.0f);
                int activeForPlayer = 0;
                int burstIndex = 0;
                for (com.x4yi.hammersunbound.client.particle.ParticleBloodPact p : bloodParticles) {
                    if (p.isAlive() && p.getTargetPlayer() == player) {
                        activeForPlayer++;
                        p.setBurstTimer(burstImpactTimer);
                        if (burstImpactTimer > 0 && visual.targetEntityIds != null && visual.targetEntityIds.length > 0) {
                            int targetId = visual.targetEntityIds[burstIndex % visual.targetEntityIds.length];
                            p.setTargetEnemy(mc.world.getEntityByID(targetId));
                            burstIndex++;
                        }
                    }
                }
                if (activeForPlayer < maxParticles) {
                    for (int i = 0; i < 2; i++) {
                        Entity targetEnemy = null;
                        if (visual.targetEntityIds != null && visual.targetEntityIds.length > 0) {
                            int targetId = visual.targetEntityIds[mc.world.rand.nextInt(visual.targetEntityIds.length)];
                            targetEnemy = mc.world.getEntityByID(targetId);
                        }
                        com.x4yi.hammersunbound.client.particle.ParticleBloodPact particle = new com.x4yi.hammersunbound.client.particle.ParticleBloodPact(
                                mc.world, player, targetEnemy, radius, madness, burstImpactTimer, pingPong
                        );
                        bloodParticles.add(particle);
                    }
                }
                float maxDist = 12.0f;
                if (player.hasCapability(com.x4yi.hammersunbound.capability.IBloodPactCapability.CAPABILITY, null)) {
                    com.x4yi.hammersunbound.capability.IBloodPactCapability cap = player.getCapability(com.x4yi.hammersunbound.capability.IBloodPactCapability.CAPABILITY, null);
                    if (cap != null && cap.getBloodPactEffect() != null) {
                        maxDist = cap.getBloodPactEffect().getTetherBreakDistance() * (float)com.x4yi.hammersunbound.config.ServerConfig.spikehammerBloodPactRangeMultiplier;
                    }
                }
                if (visual.targetEntityIds != null) {
                    for (int targetId : visual.targetEntityIds) {
                        Entity targetEnemy = mc.world.getEntityByID(targetId);
                        if (targetEnemy != null && !targetEnemy.isDead) {
                            com.x4yi.hammersunbound.client.render.TetherRenderer tether = tethers.computeIfAbsent(targetId, k -> new com.x4yi.hammersunbound.client.render.TetherRenderer());
                            tether.updatePhysics(player, targetEnemy, maxDist, madness);
                        }
                    }
                }
            }
        }
        Set<Integer> activeTargetIds = new HashSet<>();
        for (PacketBloodPactVisual.BloodPactVisual visual : visuals) {
             if (visual.targetEntityIds != null) {
                 for (int id : visual.targetEntityIds) activeTargetIds.add(id);
             }
        }
        tethers.keySet().retainAll(activeTargetIds);
        Iterator<com.x4yi.hammersunbound.client.particle.ParticleBloodPact> it = bloodParticles.iterator();
        while (it.hasNext()) {
            com.x4yi.hammersunbound.client.particle.ParticleBloodPact p = it.next();
            p.onUpdate();
            if (!p.isAlive()) {
                it.remove();
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
    private static java.lang.reflect.Field thirdPersonDistanceField;
    static {
        try {
            thirdPersonDistanceField = net.minecraft.client.renderer.EntityRenderer.class.getDeclaredField("thirdPersonDistance");
            thirdPersonDistanceField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            try {
                thirdPersonDistanceField = net.minecraft.client.renderer.EntityRenderer.class.getDeclaredField("field_78490_B");
                thirdPersonDistanceField.setAccessible(true);
            } catch (NoSuchFieldException ex) {}
        }
    }
    @SubscribeEvent
    public void onCameraSetup(net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup event) {
        if (ClientConfig.bloodPactEnabled) {
            Entity entity = event.getEntity();
            if (entity instanceof net.minecraft.entity.player.EntityPlayer && entity.hasCapability(com.x4yi.hammersunbound.capability.IBloodPactCapability.CAPABILITY, null)) {
                com.x4yi.hammersunbound.capability.IBloodPactCapability cap = entity.getCapability(com.x4yi.hammersunbound.capability.IBloodPactCapability.CAPABILITY, null);
                if (cap != null && cap.getBloodPactEffect() != null && cap.getBloodPactEffect().isActive()) {
                    if (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
                        if (thirdPersonDistanceField != null) {
                            try {
                                thirdPersonDistanceField.setFloat(Minecraft.getMinecraft().entityRenderer, 15.0f);
                            } catch (Exception e) {}
                        }
                    } else {
                        if (thirdPersonDistanceField != null) {
                            try {
                                if (thirdPersonDistanceField.getFloat(Minecraft.getMinecraft().entityRenderer) == 15.0f) {
                                    thirdPersonDistanceField.setFloat(Minecraft.getMinecraft().entityRenderer, 4.0f);
                                }
                            } catch (Exception e) {}
                        }
                    }
                } else {
                    if (thirdPersonDistanceField != null) {
                        try {
                            if (thirdPersonDistanceField.getFloat(Minecraft.getMinecraft().entityRenderer) == 15.0f) {
                                thirdPersonDistanceField.setFloat(Minecraft.getMinecraft().entityRenderer, 4.0f);
                            }
                        } catch (Exception e) {}
                    }
                }
            }
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