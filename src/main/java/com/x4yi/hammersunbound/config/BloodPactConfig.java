package com.x4yi.hammersunbound.config;

import com.google.gson.JsonObject;

public class BloodPactConfig {

    public float range;
    public float drainPercent;
    public float tetherBreakDistance;
    public int drainInterval;

    public BloodPactConfig(float range, float drainPercent, float tetherBreakDistance, int drainInterval) {
        this.range = range;
        this.drainPercent = drainPercent;
        this.tetherBreakDistance = tetherBreakDistance;
        this.drainInterval = drainInterval;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("range", range);
        json.addProperty("drainPercent", drainPercent);
        json.addProperty("tetherBreakDistance", tetherBreakDistance);
        json.addProperty("drainInterval", drainInterval);
        return json;
    }

    public static BloodPactConfig fromJson(JsonObject json) {
        float range = json.has("range") ? json.get("range").getAsFloat() : 8.0f;
        float drainPercent = json.has("drainPercent") ? json.get("drainPercent").getAsFloat() : 0.15f;
        float tetherBreakDistance = json.has("tetherBreakDistance") ? json.get("tetherBreakDistance").getAsFloat() : 12.0f;
        int drainInterval = json.has("drainInterval") ? json.get("drainInterval").getAsInt() : 10;
        return new BloodPactConfig(range, drainPercent, tetherBreakDistance, drainInterval);
    }
}
