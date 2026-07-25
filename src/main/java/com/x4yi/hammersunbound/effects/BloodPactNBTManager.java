package com.x4yi.hammersunbound.effects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class BloodPactNBTManager {

    public static NBTTagCompound serializeNBT(BloodPactEffect effect) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("active", effect.isActive());
        nbt.setInteger("remainingTicks", effect.getRemainingTicks());
        nbt.setInteger("madness", effect.getMadness());
        nbt.setIntArray("targetEntityIds", effect.getTargetEntityIdsArray());
        nbt.setFloat("range", effect.getRange());
        nbt.setFloat("drainPercent", effect.getDrainPercent());
        nbt.setFloat("tetherBreakDistance", effect.getTetherBreakDistance());
        nbt.setInteger("maxTargets", effect.getMaxTargets());
        nbt.setFloat("fieldRadius", effect.getFieldRadius());
        nbt.setFloat("repulsionForce", effect.getRepulsionForce());
        nbt.setFloat("attractionForce", effect.getAttractionForce());
        nbt.setInteger("baseDurationTicks", effect.getBaseDurationTicks());
        nbt.setInteger("hitBonusTicks", effect.getHitBonusTicks());
        nbt.setInteger("damagePenaltyTicks", effect.getDamagePenaltyTicks());
        nbt.setInteger("burstTimer", effect.getBurstTimer());
        nbt.setFloat("accumulatedDamage", effect.getAccumulatedDamage());
        nbt.setFloat("aoeAttackSize", effect.getAoeAttackSize());
        nbt.setInteger("pingPongTargetId", effect.getPingPongTargetId());
        nbt.setInteger("pingPongTimer", effect.getPingPongTimer());
        nbt.setInteger("pingPongPhase", effect.getPingPongPhase());
        
        Vec3d dir = effect.getPingPongDirection();
        if (dir != null) {
            nbt.setDouble("ppDirX", dir.x);
            nbt.setDouble("ppDirY", dir.y);
            nbt.setDouble("ppDirZ", dir.z);
        }
        return nbt;
    }

    public static void deserializeNBT(BloodPactEffect effect, NBTTagCompound nbt) {
        effect.setActive(nbt.getBoolean("active"));
        effect.setRemainingTicks(nbt.getInteger("remainingTicks"));
        effect.setMadness(nbt.getInteger("madness"));
        
        effect.getTargetEntityIds().clear();
        if (nbt.hasKey("targetEntityIds")) {
            for (int id : nbt.getIntArray("targetEntityIds")) {
                effect.getTargetEntityIds().add(id);
            }
        }
        
        effect.setRange(nbt.hasKey("range") ? nbt.getFloat("range") : 8.0f);
        effect.setDrainPercent(nbt.hasKey("drainPercent") ? nbt.getFloat("drainPercent") : 0.15f);
        effect.setTetherBreakDistance(nbt.hasKey("tetherBreakDistance") ? nbt.getFloat("tetherBreakDistance") : 12.0f);
        effect.setMaxTargets(nbt.hasKey("maxTargets") ? nbt.getInteger("maxTargets") : 3);
        effect.setFieldRadius(nbt.hasKey("fieldRadius") ? nbt.getFloat("fieldRadius") : 5.0f);
        effect.setRepulsionForce(nbt.hasKey("repulsionForce") ? nbt.getFloat("repulsionForce") : 0.20f);
        effect.setAttractionForce(nbt.hasKey("attractionForce") ? nbt.getFloat("attractionForce") : 0.03f);
        effect.setBaseDurationTicks(nbt.hasKey("baseDurationTicks") ? nbt.getInteger("baseDurationTicks") : 100);
        effect.setHitBonusTicks(nbt.hasKey("hitBonusTicks") ? nbt.getInteger("hitBonusTicks") : 40);
        effect.setDamagePenaltyTicks(nbt.hasKey("damagePenaltyTicks") ? nbt.getInteger("damagePenaltyTicks") : 40);
        effect.setBurstTimer(nbt.hasKey("burstTimer") ? nbt.getInteger("burstTimer") : 200);
        effect.setAccumulatedDamage(nbt.hasKey("accumulatedDamage") ? nbt.getFloat("accumulatedDamage") : 0.0F);
        effect.setAoeAttackSize(nbt.hasKey("aoeAttackSize") ? nbt.getFloat("aoeAttackSize") : 1.5f);
        effect.setPingPongTargetId(nbt.hasKey("pingPongTargetId") ? nbt.getInteger("pingPongTargetId") : -1);
        effect.setPingPongTimer(nbt.hasKey("pingPongTimer") ? nbt.getInteger("pingPongTimer") : 0);
        effect.setPingPongPhase(nbt.hasKey("pingPongPhase") ? nbt.getInteger("pingPongPhase") : 0);
        
        if (nbt.hasKey("ppDirX")) {
            effect.setPingPongDirection(new Vec3d(nbt.getDouble("ppDirX"), nbt.getDouble("ppDirY"), nbt.getDouble("ppDirZ")));
        } else {
            effect.setPingPongDirection(null);
        }
    }
}
