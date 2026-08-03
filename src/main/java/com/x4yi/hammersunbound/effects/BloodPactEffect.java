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
    private int burstImpactTimer;
    private float storedBurstDamage;
    private float accumulatedDamage;
    private float aoeAttackSize;
    private int pingPongTargetId = -1;
    private int pingPongTimer = 0;
    private int pingPongPhase = 0;
    private Vec3d pingPongDirection = null;
    private int prevMadness = -1;
    private int prevBurstTimer = -1;
    private int prevBurstImpactTimer = -1;
    private float prevAccumulatedDamage = -1;
    private int prevPingPongPhase = -1;
    private String prevTargetIdsStr = "";
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
        this.burstImpactTimer = 0;
        this.storedBurstDamage = 0.0f;
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
        this.burstImpactTimer = 0;
        this.storedBurstDamage = 0.0f;
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
        com.x4yi.hammersunbound.event.HammerCombatHandler.activeBloodPactPlayers.add(player);
        syncToTrackingAndSelf();
    }
    public void deactivate() {
        this.active = false;
        if (player != null) {
            com.x4yi.hammersunbound.event.HammerCombatHandler.activeBloodPactPlayers.remove(player);
            if (!player.world.isRemote) {
                BloodPactNetworkManager.sendDeactivation(player);
            }
        }
        removeModifiers();
        cancelPingPong();
        this.player = null;
        this.targetEntities.clear();
        this.targetEntityIds.clear();
        this.remainingTicks = 0;
        this.madness = 0;
        this.ticksSinceLastDrain = 0;
        this.burstTimer = 200;
        this.burstImpactTimer = 0;
        this.storedBurstDamage = 0.0f;
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
        if (player.ticksExisted % 40 == 0 && !player.world.isRemote) {
            player.heal(1.0F);
            if (madness > 0) {
                madness = Math.max(0, madness - 5);
            }
        }
        if (!player.world.isRemote) {
            boolean removed = targetEntities.removeIf(e -> e.isDead || e.getHealth() <= 0.0f || player.world.getEntityByID(e.getEntityId()) == null);
            double maxDist = tetherBreakDistance * com.x4yi.hammersunbound.config.ServerConfig.spikehammerBloodPactRangeMultiplier;
            removed |= targetEntities.removeIf(e -> player.getDistance(e) > maxDist);
            if (targetEntities.isEmpty()) {
                if (accumulatedDamage > 0.0F || burstImpactTimer > 0) {
                    if (accumulatedDamage > 0.0F) {
                        storedBurstDamage = accumulatedDamage;
                        accumulatedDamage = 0.0F;
                        burstImpactTimer = 20;
                        syncToTrackingAndSelf();
                    }
                } else {
                    deactivate();
                    return;
                }
            }
            if (removed) {
                targetEntityIds.clear();
                for (EntityLivingBase e : targetEntities) {
                    targetEntityIds.add(e.getEntityId());
                }
                syncToTrackingAndSelf();
            }
            updateModifiers();
            if (burstImpactTimer > 0) {
                burstImpactTimer--;
                if (burstImpactTimer <= 0) {
                    executeBurstImpact();
                    if (!active) return;
                }
            }
            if (burstTimer > 0) {
                burstTimer--;
            } else {
                if (burstImpactTimer <= 0) {
                    storedBurstDamage = accumulatedDamage;
                    accumulatedDamage = 0.0F;
                    burstImpactTimer = 20;
                    burstTimer = 200;
                    syncToTrackingAndSelf();
                }
            }
            PingPongManager.tickPingPong(this, player);
            BloodPactPhysicsManager.applyRepulsionField(player, targetEntityIds, fieldRadius, repulsionForce);
            BloodPactPhysicsManager.applyAttraction(player, targetEntities, attractionForce);
            checkAndDeltaSync();
        }
    }
    private void checkAndDeltaSync() {
        boolean changed = false;
        if (madness != prevMadness) { changed = true; prevMadness = madness; }
        if (burstTimer != prevBurstTimer) { changed = true; prevBurstTimer = burstTimer; }
        if (burstImpactTimer != prevBurstImpactTimer) { changed = true; prevBurstImpactTimer = burstImpactTimer; }
        if (Math.abs(accumulatedDamage - prevAccumulatedDamage) > 0.1f) { changed = true; prevAccumulatedDamage = accumulatedDamage; }
        if (pingPongPhase != prevPingPongPhase) { changed = true; prevPingPongPhase = pingPongPhase; }
        String currTargets = targetEntityIds.toString();
        if (!currTargets.equals(prevTargetIdsStr)) { changed = true; prevTargetIdsStr = currTargets; }
        if (changed) {
            syncToTrackingAndSelf();
        }
    }
    public void onHitTarget(float damageDealt) {
        if (!active || targetEntities.isEmpty() || player == null) return;
        float healAmount = damageDealt * drainPercent * com.x4yi.hammersunbound.config.ServerConfig.spikehammerBloodPactDrainMultiplier;
        if (healAmount > 0) {
            player.heal(healAmount);
        }
    }
    private void executeBurstImpact() {
        if (player == null) return;
        if (targetEntities.isEmpty()) {
            if (storedBurstDamage > 0.1F) {
                player.heal(storedBurstDamage);
            }
            storedBurstDamage = 0.0F;
            deactivate();
            return;
        }
        float burstDamage = storedBurstDamage / 3.0F;
        float totalDealt = 0.0F;
        if (burstDamage > 0.1F) {
            for (EntityLivingBase target : targetEntities) {
                if (target != null && !target.isDead) {
                    target.attackEntityFrom(DamageSource.causePlayerDamage(player), burstDamage);
                    totalDealt += burstDamage;
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
            if (totalDealt > 0) {
                player.heal(totalDealt / 3.0F);
            }
        }
        storedBurstDamage = 0.0F;
        syncToTrackingAndSelf();
    }
    public void syncClient(boolean active, int[] targetEntityIds, int remainingTicks, int madness, int burstTimer, float accumulatedDamage, int pingPongPhase, int pingPongTargetId, int burstImpactTimer) {
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
        this.burstImpactTimer = burstImpactTimer;
    }
    public void syncToTrackingAndSelf() {
        BloodPactNetworkManager.syncToTrackingAndSelf(this, player);
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
    public int getBurstImpactTimer() {
        return burstImpactTimer;
    }
    public void setBurstImpactTimer(int timer) {
        this.burstImpactTimer = timer;
    }
    public float getAccumulatedDamage() {
        return accumulatedDamage;
    }
    public void setAccumulatedDamage(float damage) {
        this.accumulatedDamage = damage;
    }
    public float getStoredBurstDamage() {
        return storedBurstDamage;
    }
    public void setStoredBurstDamage(float damage) {
        this.storedBurstDamage = damage;
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
        return BloodPactNBTManager.serializeNBT(this);
    }
    public void deserializeNBT(NBTTagCompound nbt) {
        BloodPactNBTManager.deserializeNBT(this, nbt);
    }
    public void setActive(boolean active) { this.active = active; }
    public float getRange() { return range; }
    public void setRange(float range) { this.range = range; }
    public float getDrainPercent() { return drainPercent; }
    public void setDrainPercent(float dp) { this.drainPercent = dp; }
    public float getTetherBreakDistance() { return tetherBreakDistance; }
    public void setTetherBreakDistance(float tbd) { this.tetherBreakDistance = tbd; }
    public int getMaxTargets() { return maxTargets; }
    public void setMaxTargets(int mt) { this.maxTargets = mt; }
    public float getFieldRadius() { return fieldRadius; }
    public void setFieldRadius(float fr) { this.fieldRadius = fr; }
    public float getRepulsionForce() { return repulsionForce; }
    public void setRepulsionForce(float rf) { this.repulsionForce = rf; }
    public float getAttractionForce() { return attractionForce; }
    public void setAttractionForce(float af) { this.attractionForce = af; }
    public int getBaseDurationTicks() { return baseDurationTicks; }
    public void setBaseDurationTicks(int bdt) { this.baseDurationTicks = bdt; }
    public int getHitBonusTicks() { return hitBonusTicks; }
    public void setHitBonusTicks(int hbt) { this.hitBonusTicks = hbt; }
    public int getDamagePenaltyTicks() { return damagePenaltyTicks; }
    public void setDamagePenaltyTicks(int dpt) { this.damagePenaltyTicks = dpt; }
    public void setPingPongTargetId(int id) { this.pingPongTargetId = id; }
    public void setPingPongDirection(Vec3d dir) { this.pingPongDirection = dir; }
    public void setAoeAttackSize(float size) { this.aoeAttackSize = size; }
}