package com.x4yi.hammersunbound.config;

import com.google.gson.JsonObject;

public class ServerConfig {

    public static float warhammerAoeRadiusMultiplier = 1.0f;
    public static float warhammerAoeDamageMultiplier = 1.0f;
    public static float warhammerStunDurationMultiplier = 1.0f;
    public static boolean warhammerEnableAOE = true;
    public static boolean warhammerEnableStun = true;

    public static float spikehammerBleedingDamageMultiplier = 1.0f;
    public static float spikehammerBleedingDurationMultiplier = 1.0f;
    public static float spikehammerBloodPactRangeMultiplier = 1.0f;
    public static float spikehammerBloodPactDrainMultiplier = 1.0f;
    public static boolean spikehammerEnableBleeding = true;
    public static boolean spikehammerEnableBloodPact = true;

    public static void load() {
        JsonObject json = ConfigManager.loadServer();
        if (json == null) return;

        JsonObject warhammer = json.getAsJsonObject("warhammer");
        if (warhammer != null) {
            if (warhammer.has("aoeRadiusMultiplier")) warhammerAoeRadiusMultiplier = warhammer.get("aoeRadiusMultiplier").getAsFloat();
            if (warhammer.has("aoeDamageMultiplier")) warhammerAoeDamageMultiplier = warhammer.get("aoeDamageMultiplier").getAsFloat();
            if (warhammer.has("stunDurationMultiplier")) warhammerStunDurationMultiplier = warhammer.get("stunDurationMultiplier").getAsFloat();
            if (warhammer.has("enableAOE")) warhammerEnableAOE = warhammer.get("enableAOE").getAsBoolean();
            if (warhammer.has("enableStun")) warhammerEnableStun = warhammer.get("enableStun").getAsBoolean();
        }

        JsonObject spikehammer = json.getAsJsonObject("spikehammer");
        if (spikehammer != null) {
            if (spikehammer.has("bleedingDamageMultiplier")) spikehammerBleedingDamageMultiplier = spikehammer.get("bleedingDamageMultiplier").getAsFloat();
            if (spikehammer.has("bleedingDurationMultiplier")) spikehammerBleedingDurationMultiplier = spikehammer.get("bleedingDurationMultiplier").getAsFloat();
            if (spikehammer.has("bloodPactRangeMultiplier")) spikehammerBloodPactRangeMultiplier = spikehammer.get("bloodPactRangeMultiplier").getAsFloat();
            if (spikehammer.has("bloodPactDrainMultiplier")) spikehammerBloodPactDrainMultiplier = spikehammer.get("bloodPactDrainMultiplier").getAsFloat();
            if (spikehammer.has("enableBleeding")) spikehammerEnableBleeding = spikehammer.get("enableBleeding").getAsBoolean();
            if (spikehammer.has("enableBloodPact")) spikehammerEnableBloodPact = spikehammer.get("enableBloodPact").getAsBoolean();
        }
    }
}
