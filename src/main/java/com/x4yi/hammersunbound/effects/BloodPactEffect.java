package com.x4yi.hammersunbound.effects;
import com.x4yi.hammersunbound.config.BloodPactConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class BloodPactEffect {
    private static final UUID BLOOD_PACT_SPEED_UUID = UUID.fromString("6d7f022b-2a71-46ab-a021-e0e56b4685ff");
    private static final UUID BLOOD_PACT_ATTACK_SPEED_UUID = UUID.fromString("a1b6a7b3-c15c-4d57-b088-348f9fa4ea88");
    private static final UUID BLOOD_PACT_REACH_UUID = UUID.fromString("2d7d8e6a-5a91-4d37-88cc-f5e94b26715f");
    private EntityPlayer player;
    private final List<Integer> targetEntityIds = new ArrayList<>();
    private final List<EntityLivingBase> targetEntities = new ArrayList<>();
    private boolean active;
    private int ticksSinceLastDrain;
    private float range;
    private float drainPercent;
    private float tetherBreakDistance;
    private int remainingTicks;
    private int madness;
    private int maxTargets;
    private float fieldRadius;
    private float repulsionForce;
    private float attractionForce;
    private int baseDurationTicks;
    private int hitBonusTicks;
    private int damagePenaltyTicks;
    private int burstTimer;
    private float accumulatedDamage;
    private float aoeAttackSize;
    private int pingPongTargetId = -1;
    private int pingPongTimer = 0;
    private int pingPongPhase = 0;
    private Vec3d pingPongDirection = null;
    public BloodPactEffect() {
        this.player = null;
        this.active = false;
        this.ticksSinceLastDrain = 0;
        this.range = 8.0f;
        this.drainPercent = 0.15f;
        this.tetherBreakDistance = 12.0f;
        this.fieldRadius = 5.0f;
        this.repulsionForce = 0.20f;
        this.attractionForce = 0.03f;
        this.baseDurationTicks = 100;
        this.hitBonusTicks = 40;
        this.damagePenaltyTicks = 40;
        this.remainingTicks = 0;
        this.madness = 0;
        this.maxTargets = 3;
        this.burstTimer = 200;
        this.accumulatedDamage = 0.0f;
        this.aoeAttackSize = 1.5f;
        this.pingPongTargetId = -1;
        this.pingPongTimer = 0;
        this.pingPongPhase = 0;
        this.pingPongDirection = null;
    }
    public void activate(EntityPlayer player, EntityLivingBase target, BloodPactConfig config) {
        this.player = player;
        this.active = true;
        this.targetEntityIds.clear();
        this.targetEntities.clear();
        this.madness = 0;
        this.burstTimer = 200;
        this.accumulatedDamage = 0.0f;
        if (config != null) {
            this.range = config.range;
            this.drainPercent = config.drainPercent;
            this.tetherBreakDistance = config.tetherBreakDistance;
            this.fieldRadius = config.fieldRadius;
            this.repulsionForce = config.repulsionForce;
            this.attractionForce = config.attractionForce;
            this.baseDurationTicks = config.baseDurationTicks;
            this.hitBonusTicks = config.hitBonusTicks;
            this.damagePenaltyTicks = config.damagePenaltyTicks;
            this.aoeAttackSize = config.aoeAttackSize;
        }
        this.remainingTicks = this.baseDurationTicks;
        this.ticksSinceLastDrain = 0;
        if (target != null) {
            this.targetEntityIds.add(target.getEntityId());
            this.targetEntities.add(target);
            if (player != null && !player.world.isRemote && config != null && config.maxTargets > 1) {
                double aoeRadius = 3.0D;
                net.minecraft.util.math.AxisAlignedBB aabb = target.getEntityBoundingBox().grow(aoeRadius);
                java.util.List<EntityLivingBase> nearby = player.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
                nearby.sort((e1, e2) -> Double.compare(player.getDistanceSq(e1), player.getDistanceSq(e2)));
                for (EntityLivingBase entity : nearby) {
                    if (this.targetEntityIds.size() >= config.maxTargets) break;
                    if (entity == null || entity == player || entity == target || entity.isDead) continue;
                    this.targetEntityIds.add(entity.getEntityId());
                    this.targetEntities.add(entity);
                }
            }
        }
        syncToTrackingAndSelf();
    }
    public void deactivate() {
        if (active && player != null && !player.world.isRemote) {
            int[] targetsArr = getTargetEntityIdsArray();
            com.x4yi.hammersunbound.network.PacketBloodPactVisual packet = new com.x4yi.hammersunbound.network.PacketBloodPactVisual(player.getEntityId(), targetsArr, false);
            com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToAllTracking(packet, player);
            if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packet, (net.minecraft.entity.player.EntityPlayerMP) player);
                com.x4yi.hammersunbound.network.PacketBloodPactSync packetSync = new com.x4yi.hammersunbound.network.PacketBloodPactSync(false, new int[0], 0, 0, 200, 0.0f);
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packetSync, (net.minecraft.entity.player.EntityPlayerMP) player);
            }
        }
        removeModifiers();
        cancelPingPong();
        this.player = null;
        this.targetEntities.clear();
        this.targetEntityIds.clear();
        this.active = false;
        this.remainingTicks = 0;
        this.madness = 0;
        this.ticksSinceLastDrain = 0;
        this.burstTimer = 200;
        this.accumulatedDamage = 0.0f;
    }
    public void tick(EntityPlayer targetPlayer) {
        if (!active || targetPlayer == null) return;
        this.player = targetPlayer;
        if (targetEntities.isEmpty() && !targetEntityIds.isEmpty() && player.world != null) {
            for (int id : targetEntityIds) {
                net.minecraft.entity.Entity e = player.world.getEntityByID(id);
                if (e instanceof EntityLivingBase) {
                    targetEntities.add((EntityLivingBase) e);
                }
            }
        }
        if (player.isDead) {
            deactivate();
            return;
        }
        remainingTicks--;
        if (remainingTicks <= 0) {
            deactivate();
            return;
        }
        if (player.ticksExisted % 20 == 0 && !player.world.isRemote) {
            if (madness > 0) {
                madness = Math.max(0, madness - 5);
            }
        }
        if (!player.world.isRemote) {
            targetEntities.removeIf(e -> e.isDead);
            targetEntityIds.clear();
            for (EntityLivingBase e : targetEntities) {
                targetEntityIds.add(e.getEntityId());
            }
            if (targetEntities.isEmpty()) {
                deactivate();
                return;
            }
            boolean tooFar = false;
            double maxDist = tetherBreakDistance * com.x4yi.hammersunbound.config.ServerConfig.spikehammerBloodPactRangeMultiplier;
            for (EntityLivingBase target : targetEntities) {
                if (player.getDistance(target) > maxDist) {
                    tooFar = true;
                    break;
                }
            }
            if (tooFar) {
                deactivate();
                return;
            }
            updateModifiers();
            if (burstTimer > 0) {
                burstTimer--;
            } else {
                executeBurst();
                burstTimer = 200;
            }
            PingPongManager.tickPingPong(this, player);
            BloodPactPhysicsManager.applyRepulsionField(player, targetEntityIds, fieldRadius, repulsionForce);
            BloodPactPhysicsManager.applyAttraction(player, targetEntities, attractionForce);
            if (player.ticksExisted % 5 == 0) {
                syncToTrackingAndSelf();
            }
        }
    }
    public void onHitTarget(float damageDealt) {
        if (!active || targetEntities.isEmpty() || player == null) return;
        float healAmount = damageDealt * drainPercent * com.x4yi.hammersunbound.config.ServerConfig.spikehammerBloodPactDrainMultiplier;
        if (healAmount > 0) {
            player.heal(healAmount);
        }
    }

    private void executeBurst() {
        if (player == null || targetEntities.isEmpty()) return;
        float burstDamage = accumulatedDamage / 3.0F;
        if (burstDamage > 0.1F) {
            for (EntityLivingBase target : targetEntities) {
                if (target != null && !target.isDead) {
                    target.attackEntityFrom(DamageSource.causePlayerDamage(player), burstDamage);
                    target.world.playSound(null, target.posX, target.posY, target.posZ,
                            net.minecraft.init.SoundEvents.ENTITY_GENERIC_EXPLODE,
                            net.minecraft.util.SoundCategory.PLAYERS, 1.0F, 1.2F);
                    if (!player.world.isRemote) {
                        com.x4yi.hammersunbound.network.PacketBleedingParticle particlePacket =
                            new com.x4yi.hammersunbound.network.PacketBleedingParticle(target.getEntityId(), 30);
                        com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToAllTracking(particlePacket, target);
                    }
                }
            }
        }
        accumulatedDamage = 0.0F;
        syncToTrackingAndSelf();
    }
    public void syncClient(boolean active, int[] targetEntityIds, int remainingTicks, int madness, int burstTimer, float accumulatedDamage, int pingPongPhase, int pingPongTargetId) {
        this.active = active;
        this.targetEntityIds.clear();
        if (targetEntityIds != null) {
            for (int id : targetEntityIds) {
                this.targetEntityIds.add(id);
            }
        }
        this.remainingTicks = remainingTicks;
        this.madness = madness;
        this.burstTimer = burstTimer;
        this.accumulatedDamage = accumulatedDamage;
        this.pingPongPhase = pingPongPhase;
        this.pingPongTargetId = pingPongTargetId;
    }
    public void syncToTrackingAndSelf() {
        if (player != null && !player.world.isRemote) {
            int[] targetsArr = getTargetEntityIdsArray();
            com.x4yi.hammersunbound.network.PacketBloodPactVisual packetVisual = new com.x4yi.hammersunbound.network.PacketBloodPactVisual(player.getEntityId(), targetsArr, active);
            com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToAllTracking(packetVisual, player);
            if (player instanceof net.minecraft.entity.player.EntityPlayerMP) {
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packetVisual, (net.minecraft.entity.player.EntityPlayerMP) player);
                com.x4yi.hammersunbound.network.PacketBloodPactSync packetSync = new com.x4yi.hammersunbound.network.PacketBloodPactSync(active, targetsArr, remainingTicks, madness, burstTimer, accumulatedDamage, pingPongPhase, pingPongTargetId);
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packetSync, (net.minecraft.entity.player.EntityPlayerMP) player);
            }
        }
    }
    private void updateModifiers() {
        if (player == null || player.world.isRemote) return;
        IAttributeInstance speedAttr = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        IAttributeInstance attackSpeedAttr = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        IAttributeInstance reachAttr = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
        if (speedAttr != null) {
            speedAttr.removeModifier(BLOOD_PACT_SPEED_UUID);
            if (active && madness > 0) {
                double speedBonus = (madness / 100.0D) * 0.20D;
                speedAttr.applyModifier(new AttributeModifier(BLOOD_PACT_SPEED_UUID, "Blood Pact Speed Buff", speedBonus, 2));
            }
        }
        if (attackSpeedAttr != null) {
            attackSpeedAttr.removeModifier(BLOOD_PACT_ATTACK_SPEED_UUID);
            if (active && madness > 0) {
                double attackSpeedBonus = (madness / 100.0D) * 0.50D;
                attackSpeedAttr.applyModifier(new AttributeModifier(BLOOD_PACT_ATTACK_SPEED_UUID, "Blood Pact Attack Speed Buff", attackSpeedBonus, 2));
            }
        }
        if (reachAttr != null) {
            reachAttr.removeModifier(BLOOD_PACT_REACH_UUID);
            if (active) {
                reachAttr.applyModifier(new AttributeModifier(BLOOD_PACT_REACH_UUID, "Blood Pact Reach Buff", 1.0D, 0));
            }
        }
    }
    private void removeModifiers() {
        if (player == null || player.world.isRemote) return;
        IAttributeInstance speedAttr = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        IAttributeInstance attackSpeedAttr = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        IAttributeInstance reachAttr = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
        if (speedAttr != null) {
            speedAttr.removeModifier(BLOOD_PACT_SPEED_UUID);
        }
        if (attackSpeedAttr != null) {
            attackSpeedAttr.removeModifier(BLOOD_PACT_ATTACK_SPEED_UUID);
        }
        if (reachAttr != null) {
            reachAttr.removeModifier(BLOOD_PACT_REACH_UUID);
        }
    }
    public void startPingPong(EntityLivingBase target) {
        if (target == null || player == null) return;
        this.pingPongTargetId = target.getEntityId();
        this.pingPongDirection = player.getLook(1.0F).normalize();
        this.pingPongPhase = 1;
        this.pingPongTimer = 8;
        if (target instanceof net.minecraft.entity.EntityLiving) {
            ((net.minecraft.entity.EntityLiving) target).getNavigator().clearPath();
        }
        syncToTrackingAndSelf();
    }
    public void cancelPingPong() {
        this.pingPongPhase = 0;
        this.pingPongTargetId = -1;
        this.pingPongTimer = 0;
        this.pingPongDirection = null;
        syncToTrackingAndSelf();
    }
    public void addDurationBonus() {
        this.remainingTicks = Math.min(2000000000, this.remainingTicks + this.hitBonusTicks);
        syncToTrackingAndSelf();
    }
    public void subtractDurationPenalty() {
        this.remainingTicks = Math.max(0, this.remainingTicks - this.damagePenaltyTicks);
        syncToTrackingAndSelf();
    }
    public boolean isActive() {
        return active;
    }
    public EntityPlayer getPlayer() {
        return player;
    }
    public List<EntityLivingBase> getTargetEntities() {
        return targetEntities;
    }
    public List<Integer> getTargetEntityIds() {
        return targetEntityIds;
    }
    public int[] getTargetEntityIdsArray() {
        int[] arr = new int[targetEntityIds.size()];
        for (int i = 0; i < targetEntityIds.size(); i++) {
            arr[i] = targetEntityIds.get(i);
        }
        return arr;
    }
    public int getRemainingTicks() {
        return remainingTicks;
    }
    public void setRemainingTicks(int ticks) {
        this.remainingTicks = ticks;
    }
    public int getMadness() {
        return madness;
    }
    public void setMadness(int madness) {
        this.madness = madness;
    }
    public float getAoeAttackSize() {
        return aoeAttackSize;
    }
    public int getBurstTimer() {
        return burstTimer;
    }
    public void setBurstTimer(int timer) {
        this.burstTimer = timer;
    }
    public float getAccumulatedDamage() {
        return accumulatedDamage;
    }
    public void setAccumulatedDamage(float damage) {
        this.accumulatedDamage = damage;
    }
    public void addAccumulatedDamage(float damage) {
        this.accumulatedDamage += damage;
    }
    public int getPingPongPhase() {
        return pingPongPhase;
    }
    public int getPingPongTargetId() {
        return pingPongTargetId;
    }
    public int getPingPongTimer() {
        return pingPongTimer;
    }
    public void setPingPongTimer(int timer) {
        this.pingPongTimer = timer;
    }
    public void setPingPongPhase(int phase) {
        this.pingPongPhase = phase;
    }
    public Vec3d getPingPongDirection() {
        return pingPongDirection;
    }
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("active", active);
        nbt.setInteger("remainingTicks", remainingTicks);
        nbt.setInteger("madness", madness);
        nbt.setIntArray("targetEntityIds", getTargetEntityIdsArray());
        nbt.setFloat("range", range);
        nbt.setFloat("drainPercent", drainPercent);
        nbt.setFloat("tetherBreakDistance", tetherBreakDistance);
        nbt.setInteger("maxTargets", maxTargets);
        nbt.setFloat("fieldRadius", fieldRadius);
        nbt.setFloat("repulsionForce", repulsionForce);
        nbt.setFloat("attractionForce", attractionForce);
        nbt.setInteger("baseDurationTicks", baseDurationTicks);
        nbt.setInteger("hitBonusTicks", hitBonusTicks);
        nbt.setInteger("damagePenaltyTicks", damagePenaltyTicks);
        nbt.setInteger("burstTimer", burstTimer);
        nbt.setFloat("accumulatedDamage", accumulatedDamage);
        nbt.setFloat("aoeAttackSize", aoeAttackSize);
        nbt.setInteger("pingPongTargetId", pingPongTargetId);
        nbt.setInteger("pingPongTimer", pingPongTimer);
        nbt.setInteger("pingPongPhase", pingPongPhase);
        if (pingPongDirection != null) {
            nbt.setDouble("ppDirX", pingPongDirection.x);
            nbt.setDouble("ppDirY", pingPongDirection.y);
            nbt.setDouble("ppDirZ", pingPongDirection.z);
        }
        return nbt;
    }
    public void deserializeNBT(NBTTagCompound nbt) {
        active = nbt.getBoolean("active");
        remainingTicks = nbt.getInteger("remainingTicks");
        madness = nbt.getInteger("madness");
        targetEntityIds.clear();
        if (nbt.hasKey("targetEntityIds")) {
            for (int id : nbt.getIntArray("targetEntityIds")) {
                targetEntityIds.add(id);
            }
        }
        range = nbt.hasKey("range") ? nbt.getFloat("range") : 8.0f;
        drainPercent = nbt.hasKey("drainPercent") ? nbt.getFloat("drainPercent") : 0.15f;
        tetherBreakDistance = nbt.hasKey("tetherBreakDistance") ? nbt.getFloat("tetherBreakDistance") : 12.0f;
        maxTargets = nbt.hasKey("maxTargets") ? nbt.getInteger("maxTargets") : 3;
        fieldRadius = nbt.hasKey("fieldRadius") ? nbt.getFloat("fieldRadius") : 5.0f;
        repulsionForce = nbt.hasKey("repulsionForce") ? nbt.getFloat("repulsionForce") : 0.20f;
        attractionForce = nbt.hasKey("attractionForce") ? nbt.getFloat("attractionForce") : 0.03f;
        baseDurationTicks = nbt.hasKey("baseDurationTicks") ? nbt.getInteger("baseDurationTicks") : 100;
        hitBonusTicks = nbt.hasKey("hitBonusTicks") ? nbt.getInteger("hitBonusTicks") : 40;
        damagePenaltyTicks = nbt.hasKey("damagePenaltyTicks") ? nbt.getInteger("damagePenaltyTicks") : 40;
        burstTimer = nbt.hasKey("burstTimer") ? nbt.getInteger("burstTimer") : 200;
        accumulatedDamage = nbt.hasKey("accumulatedDamage") ? nbt.getFloat("accumulatedDamage") : 0.0F;
        aoeAttackSize = nbt.hasKey("aoeAttackSize") ? nbt.getFloat("aoeAttackSize") : 1.5f;
        pingPongTargetId = nbt.hasKey("pingPongTargetId") ? nbt.getInteger("pingPongTargetId") : -1;
        pingPongTimer = nbt.hasKey("pingPongTimer") ? nbt.getInteger("pingPongTimer") : 0;
        pingPongPhase = nbt.hasKey("pingPongPhase") ? nbt.getInteger("pingPongPhase") : 0;
        if (nbt.hasKey("ppDirX")) {
            pingPongDirection = new Vec3d(nbt.getDouble("ppDirX"), nbt.getDouble("ppDirY"), nbt.getDouble("ppDirZ"));
        } else {
            pingPongDirection = null;
        }
    }
}