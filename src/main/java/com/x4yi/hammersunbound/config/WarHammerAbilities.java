package com.x4yi.hammersunbound.config;

import com.google.gson.JsonObject;

public class WarHammerAbilities {

    public int stunDuration;
    public int stunAmplifier;
    public float aoeRadius;
    public float aoeDamage;
    public int aoeStunDuration;
    public int aoeStunAmplifier;

    public WarHammerAbilities(int stunDuration, int stunAmplifier, float aoeRadius, float aoeDamage, int aoeStunDuration, int aoeStunAmplifier) {
        this.stunDuration = stunDuration;
        this.stunAmplifier = stunAmplifier;
        this.aoeRadius = aoeRadius;
        this.aoeDamage = aoeDamage;
        this.aoeStunDuration = aoeStunDuration;
        this.aoeStunAmplifier = aoeStunAmplifier;
    }

    public static WarHammerAbilities fromJson(JsonObject json) {
        int stunDuration = json.has("stunDuration") ? json.get("stunDuration").getAsInt() : 20;
        int stunAmplifier = json.has("stunAmplifier") ? json.get("stunAmplifier").getAsInt() : 3;
        float aoeRadius = json.has("aoeRadius") ? json.get("aoeRadius").getAsFloat() : 2.5f;
        float aoeDamage = json.has("aoeDamage") ? json.get("aoeDamage").getAsFloat() : 6.0f;
        int aoeStunDuration = json.has("aoeStunDuration") ? json.get("aoeStunDuration").getAsInt() : 40;
        int aoeStunAmplifier = json.has("aoeStunAmplifier") ? json.get("aoeStunAmplifier").getAsInt() : 1;
        return new WarHammerAbilities(stunDuration, stunAmplifier, aoeRadius, aoeDamage, aoeStunDuration, aoeStunAmplifier);
    }
}
