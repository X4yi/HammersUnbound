package com.x4yi.hammersunbound.effects;

import com.x4yi.hammersunbound.config.BleedingConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

        this.ticksUntilDecay = config.baseDuration;
        if (this.ticksUntilDamage <= 0) {
            this.ticksUntilDamage = config.tickInterval;
        }
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
            ticksUntilDecay = baseDuration;
            sync(target);
        }

        ticksUntilDamage--;
        if (ticksUntilDamage <= 0) {
            float damage = damagePerLevel * level;
            target.attackEntityFrom(net.minecraft.util.DamageSource.MAGIC, damage);
            ticksUntilDamage = tickInterval;
        }
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
