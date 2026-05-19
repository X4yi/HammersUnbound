package com.x4yi.hammersunbound.config;

import com.google.gson.JsonObject;

public class ClientConfig {

    public static boolean aoeEnabled = true;
    public static int aoeParticleCount = 15;
    public static float aoeParticleDensity = 1.0f;
    public static float aoeMaxHeight = 3.0f;

    public static boolean bloodPactEnabled = true;
    public static int bloodPactParticleCount = 5;

    public static boolean bleedingParticleEnabled = true;

    public static void load() {
        JsonObject json = ConfigManager.loadClient();
        if (json == null) return;

        JsonObject particles = json.getAsJsonObject("particles");
        if (particles == null) return;

        if (particles.has("aoeEnabled")) aoeEnabled = particles.get("aoeEnabled").getAsBoolean();
        if (particles.has("aoeParticleCount")) aoeParticleCount = particles.get("aoeParticleCount").getAsInt();
        if (particles.has("aoeParticleDensity")) aoeParticleDensity = particles.get("aoeParticleDensity").getAsFloat();
        if (particles.has("aoeMaxHeight")) aoeMaxHeight = particles.get("aoeMaxHeight").getAsFloat();
        if (particles.has("bloodPactEnabled")) bloodPactEnabled = particles.get("bloodPactEnabled").getAsBoolean();
        if (particles.has("bloodPactParticleCount")) bloodPactParticleCount = particles.get("bloodPactParticleCount").getAsInt();
        if (particles.has("bleedingParticleEnabled")) bleedingParticleEnabled = particles.get("bleedingParticleEnabled").getAsBoolean();
    }
}
