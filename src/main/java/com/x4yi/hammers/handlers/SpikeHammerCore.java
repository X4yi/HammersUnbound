package com.x4yi.hammers.handlers;

import com.x4yi.hammers.config.HammerConfig;
import com.x4yi.hammers.items.ItemSpikeHammer;
import com.x4yi.hammers.items.ItemSpikeHammer.MaterialType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;

@Mod.EventBusSubscriber
public class SpikeHammerCore {

    @SideOnly(Side.CLIENT)
    private static TextureAtlasSprite BLOOD_SPRITE;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerParticles(TextureStitchEvent.Pre e) {
        BLOOD_SPRITE = e.getMap().registerSprite(
                new ResourceLocation("hammersunbound", "particle/blood")
        );
    }

    private static final String TAG_ROOT = "SpikeBleed";
    private static final String L = "L";
    private static final String D = "D";
    private static final String I = "I";
    private static final String C = "C";
    private static final String M = "M";

    private static final DamageSource BLEED =
            new DamageSource("spike_bleed")
                    .setDamageBypassesArmor()
                    .setDifficultyScaled();

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent e) {
        if (!(e.getTarget() instanceof EntityLivingBase)) return;
        applyIfValid(e.getEntityPlayer(), (EntityLivingBase) e.getTarget(), false);
    }

    @SubscribeEvent
    public static void onCrit(CriticalHitEvent e) {
        if (!e.isVanillaCritical()) return;
        if (!(e.getTarget() instanceof EntityLivingBase)) return;
        applyIfValid(e.getEntityPlayer(), (EntityLivingBase) e.getTarget(), true);
    }

    private static void applyIfValid(EntityPlayer player, EntityLivingBase target, boolean isCrit) {
        if (player == null) return;
        if (!(player.getHeldItemMainhand().getItem() instanceof ItemSpikeHammer)) return;
        if (!isCrit && !player.isSprinting()) return;

        ItemSpikeHammer hammer = (ItemSpikeHammer) player.getHeldItemMainhand().getItem();
        ItemSpikeHammer.SpikeCfg cfg = hammer.getSpikeCfg();

        NBTTagCompound root = getRoot(target);
        String key = player.getUniqueID().toString();

        NBTTagCompound n = root.hasKey(key)
                ? root.getCompoundTag(key)
                : new NBTTagCompound();

        int lvl = Math.min(n.getInteger(L) + 1, cfg.maxLevel);

        n.setInteger(L, lvl);
        n.setInteger(D, cfg.baseDuration);
        n.setInteger(I, interval(lvl, cfg));
        n.setInteger(C, n.getInteger(I));
        n.setByte(M, (byte) hammer.getMaterialType().ordinal());

        root.setTag(key, n);

        if (player.world.isRemote && HammerConfig.client.BleedParticlesEnabled) {
            spawnImpactParticles(player, target, lvl);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingUpdateEvent e) {
        EntityLivingBase ent = e.getEntityLiving();
        if (!ent.getEntityData().hasKey(TAG_ROOT)) return;

        NBTTagCompound root = ent.getEntityData().getCompoundTag(TAG_ROOT);

        Iterator<String> it = root.getKeySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            NBTTagCompound n = root.getCompoundTag(key);

            int lvl = n.getInteger(L);
            if (lvl <= 0) {
                it.remove();
                continue;
            }

            n.setInteger(D, n.getInteger(D) - 1);
            n.setInteger(C, n.getInteger(C) - 1);

            if (n.getInteger(C) <= 0) {

                if (!ent.world.isRemote) {
                    int prev = ent.hurtResistantTime;
                    ent.hurtResistantTime = 0;
                    ent.attackEntityFrom(BLEED, lvl);
                    ent.hurtResistantTime = prev;
                } else if (HammerConfig.client.BleedParticlesEnabled) {
                    spawnBleedParticles(ent, lvl);
                }

                n.setInteger(C, n.getInteger(I));
            }

            if (n.getInteger(D) <= 0) {
                lvl--;
                if (lvl <= 0) {
                    it.remove();
                } else {
                    n.setInteger(L, lvl);
                    ItemSpikeHammer.SpikeCfg cfg = cfgFromNBT(n);
                    n.setInteger(D, cfg.baseDuration);
                    n.setInteger(I, interval(lvl, cfg));
                    n.setInteger(C, n.getInteger(I));
                }
            }
        }

        if (root.getKeySet().isEmpty()) {
            ent.getEntityData().removeTag(TAG_ROOT);
        }
    }

    private static int interval(int lvl, ItemSpikeHammer.SpikeCfg c) {
        float t = (float) lvl / c.maxLevel;
        int val = MathHelper.floor(
                c.maxInterval + (c.minInterval - c.maxInterval) * t
        );
        return MathHelper.clamp(val, c.minInterval, c.maxInterval);
    }

    private static ItemSpikeHammer.SpikeCfg cfgFromNBT(NBTTagCompound n) {
        MaterialType type = MaterialType.values()[n.getByte(M)];
        switch (type) {
            case STONE:   return new ItemSpikeHammer.SpikeCfg(HammerConfig.spikehammer.STONE);
            case IRON:    return new ItemSpikeHammer.SpikeCfg(HammerConfig.spikehammer.IRON);
            case GOLD:    return new ItemSpikeHammer.SpikeCfg(HammerConfig.spikehammer.GOLD);
            case DIAMOND: return new ItemSpikeHammer.SpikeCfg(HammerConfig.spikehammer.DIAMOND);
            default:      return new ItemSpikeHammer.SpikeCfg(HammerConfig.spikehammer.WOOD);
        }
    }

    private static NBTTagCompound getRoot(EntityLivingBase e) {
        if (!e.getEntityData().hasKey(TAG_ROOT)) {
            e.getEntityData().setTag(TAG_ROOT, new NBTTagCompound());
        }
        return e.getEntityData().getCompoundTag(TAG_ROOT);
    }

    // ================= IMPACT =================

    @SideOnly(Side.CLIENT)
    private static void spawnImpactParticles(EntityPlayer player, EntityLivingBase e, int lvl) {

        if (BLOOD_SPRITE == null) return;

        int count = lvl * HammerConfig.client.BleedParticlesDensity;

        for (int i = 0; i < count; i++) {

            double px = e.posX + (e.world.rand.nextDouble() - 0.5) * 0.4;
            double py = e.posY + e.height * 0.7;
            double pz = e.posZ + (e.world.rand.nextDouble() - 0.5) * 0.4;

            double vx = 0;
            double vz = 0;

            if (player != null) {
                vx = (px - player.posX) * 0.15;
                vz = (pz - player.posZ) * 0.15;
            }

            double vy = 0.25 + e.world.rand.nextDouble() * 0.1;

            Minecraft.getMinecraft().effectRenderer.addEffect(
                    new BloodParticle(e.world, px, py, pz, vx, vy, vz, lvl)
            );
        }
    }

    // ================= BLEED =================

    @SideOnly(Side.CLIENT)
    private static void spawnBleedParticles(EntityLivingBase e, int lvl) {

        if (BLOOD_SPRITE == null) return;

        int count = lvl * HammerConfig.client.BleedParticlesDensity;
        World w = e.world;

        float halfWidth = e.width * 0.5F;

        // Más nivel = más zonas activas alrededor del cuerpo
        int zones = Math.max(1, lvl);

        for (int i = 0; i < count; i++) {

            // Elegimos una zona fija del cuerpo
            int zone = w.rand.nextInt(zones);

            double angle = (2 * Math.PI / zones) * zone;
            double radius = halfWidth * (0.8 + w.rand.nextDouble() * 0.4);

            double px = e.posX + Math.cos(angle) * radius;
            double pz = e.posZ + Math.sin(angle) * radius;

            // Distribuido en toda la altura
            double py = e.posY + w.rand.nextDouble() * e.height;

            // Caída natural
            double vx = (w.rand.nextDouble() - 0.5) * 0.02;
            double vy = -0.05 - w.rand.nextDouble() * 0.08;
            double vz = (w.rand.nextDouble() - 0.5) * 0.02;

            Minecraft.getMinecraft().effectRenderer.addEffect(
                    new BloodParticle(w, px, py, pz, vx, vy, vz, lvl)
            );
        }
    }

    // ================= PARTICLE =================

    @SideOnly(Side.CLIENT)
    private static class BloodParticle extends Particle {

        protected BloodParticle(World w,
                                double x, double y, double z,
                                double vx, double vy, double vz,
                                int lvl) {

            super(w, x, y, z);

            this.motionX = vx;
            this.motionY = vy;
            this.motionZ = vz;

            this.particleGravity = 1.1F;

            float base = 0.4F;
            float scale = MathHelper.clamp(base + lvl * 0.25F, 0.4F, 1.2F);
            this.particleScale = scale + rand.nextFloat() * 0.25F;

            this.particleMaxAge = 12 + rand.nextInt(8);

            this.setParticleTexture(BLOOD_SPRITE);

            this.particleRed = 1.0F;
            this.particleGreen = 0.0F;
            this.particleBlue = 0.0F;
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
            this.motionX *= 0.92;
            this.motionZ *= 0.92;
            this.particleScale *= 0.96F;
        }

        @Override
        public int getFXLayer() {
            return 1;
        }
    }
}
