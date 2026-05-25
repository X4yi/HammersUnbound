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
        json.addProperty("baseDuration", baseDuration);
        json.addProperty("damagePerLevel", damagePerLevel);
        json.addProperty("tickInterval", tickInterval);
        json.addProperty("decayTicks", decayTicks);
        return json;
    }

    public static BleedingConfig fromJson(JsonObject json) {
        int maxLevels = json.has("maxLevels") ? json.get("maxLevels").getAsInt() : 5;
        int baseDuration = json.has("baseDuration") ? json.get("baseDuration").getAsInt() : 60;
        float damagePerLevel = json.has("damagePerLevel") ? json.get("damagePerLevel").getAsFloat() : 1.0f;
        int tickInterval = json.has("tickInterval") ? json.get("tickInterval").getAsInt() : 20;
        int decayTicks = json.has("decayTicks") ? json.get("decayTicks").getAsInt() : 200;
        return new BleedingConfig(maxLevels, baseDuration, damagePerLevel, tickInterval, decayTicks);
    }
}
