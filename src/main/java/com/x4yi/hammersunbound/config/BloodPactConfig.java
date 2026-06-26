package com.x4yi.hammersunbound.config;
import com.google.gson.JsonObject;
public class BloodPactConfig {
    public float range;
    public float drainPercent;
    public float tetherBreakDistance;
    public int maxTargets;
    public float fieldRadius;
    public float repulsionForce;
    public float attractionForce;
    public int baseDurationTicks;
    public int hitBonusTicks;
    public int damagePenaltyTicks;
    public float aoeAttackSize;

    public BloodPactConfig(float range, float drainPercent, float tetherBreakDistance,
                           int maxTargets, float fieldRadius, float repulsionForce, float attractionForce,
                           int baseDurationTicks, int hitBonusTicks, int damagePenaltyTicks, float aoeAttackSize) {
        this.range = range;
        this.drainPercent = drainPercent;
        this.tetherBreakDistance = tetherBreakDistance;
        this.maxTargets = maxTargets;
        this.fieldRadius = fieldRadius;
        this.repulsionForce = repulsionForce;
        this.attractionForce = attractionForce;
        this.baseDurationTicks = baseDurationTicks;
        this.hitBonusTicks = hitBonusTicks;
        this.damagePenaltyTicks = damagePenaltyTicks;
        this.aoeAttackSize = aoeAttackSize;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("range", range);
        json.addProperty("drainPercent", drainPercent);
        json.addProperty("tetherBreakDistance", tetherBreakDistance);
        json.addProperty("maxTargets", maxTargets);
        json.addProperty("fieldRadius", fieldRadius);
        json.addProperty("repulsionForce", repulsionForce);
        json.addProperty("attractionForce", attractionForce);
        json.addProperty("baseDurationSeconds", HammerMaterialData.ticksToSeconds(baseDurationTicks));
        json.addProperty("hitBonusSeconds", HammerMaterialData.ticksToSeconds(hitBonusTicks));
        json.addProperty("damagePenaltySeconds", HammerMaterialData.ticksToSeconds(damagePenaltyTicks));
        json.addProperty("aoeAttackSize", aoeAttackSize);
        return json;
    }

    public static BloodPactConfig fromJson(JsonObject json) {
        float range = json.has("range") ? json.get("range").getAsFloat() : 8.0f;
        float drainPercent = json.has("drainPercent") ? json.get("drainPercent").getAsFloat() : 0.15f;
        float tetherBreakDistance = json.has("tetherBreakDistance") ? json.get("tetherBreakDistance").getAsFloat() : 12.0f;
        int maxTargets = json.has("maxTargets") ? json.get("maxTargets").getAsInt() : 3;
        float fieldRadius = json.has("fieldRadius") ? json.get("fieldRadius").getAsFloat() : 5.0f;
        float repulsionForce = json.has("repulsionForce") ? json.get("repulsionForce").getAsFloat() : 0.20f;
        float attractionForce = json.has("attractionForce") ? json.get("attractionForce").getAsFloat() : 0.03f;
        int baseDurationTicks = json.has("baseDurationSeconds")
                ? HammerMaterialData.secondsToTicks(json.get("baseDurationSeconds").getAsFloat())
                : 100;
        int hitBonusTicks = json.has("hitBonusSeconds")
                ? HammerMaterialData.secondsToTicks(json.get("hitBonusSeconds").getAsFloat())
                : 40;
        int damagePenaltyTicks = json.has("damagePenaltySeconds")
                ? HammerMaterialData.secondsToTicks(json.get("damagePenaltySeconds").getAsFloat())
                : 40;
        float aoeAttackSize = json.has("aoeAttackSize") ? json.get("aoeAttackSize").getAsFloat() : 1.5f;
        return new BloodPactConfig(range, drainPercent, tetherBreakDistance,
                maxTargets, fieldRadius, repulsionForce, attractionForce,
                baseDurationTicks, hitBonusTicks, damagePenaltyTicks, aoeAttackSize);
    }
}