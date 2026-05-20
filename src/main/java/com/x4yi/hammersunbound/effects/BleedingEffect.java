package com.x4yi.hammersunbound.effects;

import com.x4yi.hammersunbound.config.BleedingConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public class BleedingEffect {

    private int level;
    private int ticksUntilDecay;
    private int ticksUntilDamage;

    private int maxLevels;
    private int baseDuration;
    private float damagePerLevel;
    private int tickInterval;
    private int decayTicks;

    public BleedingEffect() {
        this.level = 0;
        this.ticksUntilDecay = 0;
        this.ticksUntilDamage = 0;
        this.maxLevels = 5;
        this.baseDuration = 60;
        this.damagePerLevel = 1.0f;
        this.tickInterval = 20;
        this.decayTicks = 200;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void apply(EntityLivingBase target, BleedingConfig config) {
        if (config == null) return;
        
        if (level < config.maxLevels) {
            level++;
        }
        this.maxLevels = config.maxLevels;
        this.baseDuration = config.baseDuration;
        this.damagePerLevel = config.damagePerLevel;
        this.tickInterval = config.tickInterval;
        this.decayTicks = config.decayTicks;

        // Opción A: Al volver a golpear, se reinicia la duración del nuevo nivel al máximo de su duración escalada.
        this.ticksUntilDecay = getDurationForLevel(level, baseDuration);
        
        // Opción A: Reiniciar el temporizador al nuevo intervalo del nivel superior.
        this.ticksUntilDamage = getTickIntervalForLevel(level, tickInterval);
        
        sync(target);
    }

    public void tick(EntityLivingBase target) {
        if (level <= 0) return;

        ticksUntilDecay--;
        if (ticksUntilDecay <= 0) {
            level--;
            if (level <= 0) {
                level = 0;
                ticksUntilDecay = 0;
                ticksUntilDamage = 0;
                sync(target);
                return;
            }
            
            // Opción A (Downgrade): Reiniciar el temporizador de decay para el nuevo nivel inferior.
            this.ticksUntilDecay = getDurationForLevel(level, baseDuration);
            
            // Opción A (Downgrade): Reiniciar el temporizador de daño al intervalo del nuevo nivel inferior.
            this.ticksUntilDamage = getTickIntervalForLevel(level, tickInterval);
            
            sync(target);
        }

        ticksUntilDamage--;
        if (ticksUntilDamage <= 0) {
            float damage = getDamageForLevel(level, damagePerLevel);
            target.attackEntityFrom(net.minecraft.util.DamageSource.MAGIC, damage);
            ticksUntilDamage = getTickIntervalForLevel(level, tickInterval);
        }
    }

    public float getDamageForLevel(int level, float damagePerLevel) {
        // Nivel 1: 50%, Nivel 2: 75%, Nivel 3: 100%, Nivel 4: 125%, etc.
        return damagePerLevel * (0.5F + 0.25F * (level - 1));
    }

    public int getDurationForLevel(int level, int configBaseDuration) {
        // Menor duración a mayor nivel
        double multiplier = 2.0 - (level - 1) * 0.3;
        if (multiplier < 0.4) {
            multiplier = 0.4;
        }
        return (int) (configBaseDuration * multiplier);
    }

    public int getTickIntervalForLevel(int level, int configTickInterval) {
        // Menor intervalo de daño (más rápido) a mayor nivel
        double multiplier;
        if (level == 1) {
            multiplier = 4.0;
        } else if (level == 2) {
            multiplier = 2.5;
        } else if (level == 3) {
            multiplier = 1.5;
        } else if (level == 4) {
            multiplier = 1.0;
        } else {
            multiplier = Math.max(0.5, 1.0 - (level - 4) * 0.1);
        }
        return (int) Math.max(5, configTickInterval * multiplier);
    }

    public void sync(EntityLivingBase target) {
        if (target != null && !target.world.isRemote) {
            com.x4yi.hammersunbound.network.PacketBleedingSync packet = new com.x4yi.hammersunbound.network.PacketBleedingSync(target.getEntityId(), level);
            com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToAllTracking(packet, target);
            if (target instanceof net.minecraft.entity.player.EntityPlayerMP) {
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packet, (net.minecraft.entity.player.EntityPlayerMP) target);
            }
        }
    }

    public int getLevel() {
        return level;
    }

    public boolean isActive() {
        return level > 0;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("level", level);
        nbt.setInteger("ticksUntilDecay", ticksUntilDecay);
        nbt.setInteger("ticksUntilDamage", ticksUntilDamage);
        nbt.setInteger("maxLevels", maxLevels);
        nbt.setInteger("baseDuration", baseDuration);
        nbt.setFloat("damagePerLevel", damagePerLevel);
        nbt.setInteger("tickInterval", tickInterval);
        nbt.setInteger("decayTicks", decayTicks);
        return nbt;
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        level = nbt.getInteger("level");
        ticksUntilDecay = nbt.getInteger("ticksUntilDecay");
        ticksUntilDamage = nbt.getInteger("ticksUntilDamage");
        maxLevels = nbt.getInteger("maxLevels");
        baseDuration = nbt.getInteger("baseDuration");
        damagePerLevel = nbt.getFloat("damagePerLevel");
        tickInterval = nbt.getInteger("tickInterval");
        decayTicks = nbt.getInteger("decayTicks");
    }
}
