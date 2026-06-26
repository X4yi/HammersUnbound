package com.x4yi.hammersunbound.config;
import com.google.gson.JsonObject;
public class BleedingConfig {
    public int maxLevels;
    public int baseDuration;
    public float damagePerLevel;
    public int tickInterval;
    public int decayTicks;
    public BleedingConfig(int maxLevels, int baseDuration, float damagePerLevel, int tickInterval, int decayTicks) {
        this.maxLevels = maxLevels;
        this.baseDuration = baseDuration;
        this.damagePerLevel = damagePerLevel;
        this.tickInterval = tickInterval;
        this.decayTicks = decayTicks;
    }
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("maxLevels", maxLevels);
        json.addProperty("baseDurationSeconds", HammerMaterialData.ticksToSeconds(baseDuration));
        json.addProperty("damagePerLevel", damagePerLevel);
        json.addProperty("tickIntervalSeconds", HammerMaterialData.ticksToSeconds(tickInterval));
        json.addProperty("decaySeconds", HammerMaterialData.ticksToSeconds(decayTicks));
        return json;
    }
    public static BleedingConfig fromJson(JsonObject json) {
        int maxLevels = json.has("maxLevels") ? json.get("maxLevels").getAsInt() : 5;
        int baseDuration = json.has("baseDurationSeconds")
                ? HammerMaterialData.secondsToTicks(json.get("baseDurationSeconds").getAsFloat())
                : (json.has("baseDuration") ? json.get("baseDuration").getAsInt() : 60);
        float damagePerLevel = json.has("damagePerLevel") ? json.get("damagePerLevel").getAsFloat() : 1.0f;
        int tickInterval = json.has("tickIntervalSeconds")
                ? HammerMaterialData.secondsToTicks(json.get("tickIntervalSeconds").getAsFloat())
                : (json.has("tickInterval") ? json.get("tickInterval").getAsInt() : 20);
        int decayTicks = json.has("decaySeconds")
                ? HammerMaterialData.secondsToTicks(json.get("decaySeconds").getAsFloat())
                : (json.has("decayTicks") ? json.get("decayTicks").getAsInt() : 200);
        return new BleedingConfig(maxLevels, baseDuration, damagePerLevel, tickInterval, decayTicks);
    }
}