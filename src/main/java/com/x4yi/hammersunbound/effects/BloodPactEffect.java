package com.x4yi.hammersunbound.effects;

import com.x4yi.hammersunbound.config.BloodPactConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class BloodPactEffect {

    private EntityPlayer player;
    private EntityLivingBase target;
    private int targetEntityId = -1;
    private boolean active;
    private int ticksSinceLastDrain;

    private float range;
    private float drainPercent;
    private float tetherBreakDistance;
    private int drainInterval;

    public BloodPactEffect() {
        this.player = null;
        this.target = null;
        this.active = false;
        this.ticksSinceLastDrain = 0;
        this.range = 8.0f;
        this.drainPercent = 0.15f;
        this.tetherBreakDistance = 12.0f;
        this.drainInterval = 10;
    }

    public void activate(EntityPlayer player, EntityLivingBase target, BloodPactConfig config) {
        this.player = player;
        this.target = target;
        if (target != null) {
            this.targetEntityId = target.getEntityId();
        }
        this.active = true;
        this.ticksSinceLastDrain = 0;

        if (config != null) {
            this.range = config.range;
            this.drainPercent = config.drainPercent;
            this.tetherBreakDistance = config.tetherBreakDistance;
            this.drainInterval = config.drainInterval;
        }

        if (player != null && target != null && !player.world.isRemote) {
            com.x4yi.hammersunbound.network.PacketBloodPactVisual packet = new com.x4yi.hammersunbound.network.PacketBloodPactVisual(player.getEntityId(), target.getEntityId(), true);
            com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToAllTracking(packet, player);
            if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packet, (net.minecraft.entity.player.EntityPlayerMP) player);
            }
        }
    }

    public void deactivate() {
        if (active && player != null && target != null && !player.world.isRemote) {
            com.x4yi.hammersunbound.network.PacketBloodPactVisual packet = new com.x4yi.hammersunbound.network.PacketBloodPactVisual(player.getEntityId(), target.getEntityId(), false);
            com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToAllTracking(packet, player);
            if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packet, (net.minecraft.entity.player.EntityPlayerMP) player);
            }
        }
        this.player = null;
        this.target = null;
        this.targetEntityId = -1;
        this.active = false;
        this.ticksSinceLastDrain = 0;
    }

    public void tick(EntityPlayer targetPlayer) {
        if (!active || targetPlayer == null) return;
        this.player = targetPlayer;

        if (target == null && targetPlayer.world != null && targetEntityId != -1) {
            net.minecraft.entity.Entity e = targetPlayer.world.getEntityByID(targetEntityId);
            if (e instanceof EntityLivingBase) {
                target = (EntityLivingBase) e;
            }
        }

        if (target == null) return;

        if (player.isDead || target.isDead) {
            deactivate();
            return;
        }

        double distance = player.getDistance(target);
        if (distance > tetherBreakDistance) {
            deactivate();
            return;
        }

        ticksSinceLastDrain++;
        if (ticksSinceLastDrain >= drainInterval) {
            ticksSinceLastDrain = 0;
            drain();
        }
    }

    public void onHitTarget(float damageDealt) {
        if (!active || target == null || player == null) return;
        float healAmount = damageDealt * drainPercent;
        if (healAmount > 0) {
            player.heal(healAmount);
        }
    }

    private void drain() {
        if (target == null || player == null) return;
        float drainAmount = 1.0f;
        target.attackEntityFrom(net.minecraft.util.DamageSource.MAGIC, drainAmount);
        player.heal(drainAmount * drainPercent);
    }

    public boolean isActive() {
        return active;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public EntityLivingBase getTarget() {
        return target;
    }

    public Vec3d getTargetPosition() {
        if (target == null) return null;
        return new Vec3d(target.posX, target.posY + target.height / 2.0, target.posZ);
    }

    public Vec3d getPlayerPosition() {
        if (player == null) return null;
        return new Vec3d(player.posX, player.posY + player.height / 2.0, player.posZ);
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("active", active);
        nbt.setInteger("ticksSinceLastDrain", ticksSinceLastDrain);
        nbt.setInteger("targetEntityId", targetEntityId);
        nbt.setFloat("range", range);
        nbt.setFloat("drainPercent", drainPercent);
        nbt.setFloat("tetherBreakDistance", tetherBreakDistance);
        nbt.setInteger("drainInterval", drainInterval);
        return nbt;
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        active = nbt.getBoolean("active");
        ticksSinceLastDrain = nbt.getInteger("ticksSinceLastDrain");
        targetEntityId = nbt.hasKey("targetEntityId") ? nbt.getInteger("targetEntityId") : -1;
        range = nbt.hasKey("range") ? nbt.getFloat("range") : 8.0f;
        drainPercent = nbt.hasKey("drainPercent") ? nbt.getFloat("drainPercent") : 0.15f;
        tetherBreakDistance = nbt.hasKey("tetherBreakDistance") ? nbt.getFloat("tetherBreakDistance") : 12.0f;
        drainInterval = nbt.hasKey("drainInterval") ? nbt.getInteger("drainInterval") : 10;
    }
}
