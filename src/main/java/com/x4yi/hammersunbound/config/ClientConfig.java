package com.x4yi.hammersunbound.config;

import com.google.gson.JsonObject;

public class ClientConfig {

    public static boolean aoeEnabled = true;
    public static float aoeParticleCountMultiplier = 1.0f;
    public static float aoeParticleDensityMultiplier = 1.0f;
    public static float aoeParticleHeightMultiplier = 1.0f;

    public static boolean bloodPactEnabled = true;
    public static int bloodPactParticleCount = 5;

    public static boolean bleedingParticleEnabled = true;

    // 0: Bottom Right, 1: Bottom Center, 2: Bottom Left, 3: Top Right, 4: Top Left
    public static int uiOverlayPosition = 0;
    public static boolean showDevWarning = true;

    public static void load() {
        JsonObject json = ConfigManager.loadClient();
        if (json == null) return;

        JsonObject particles = json.getAsJsonObject("particles");
        if (particles != null) {
            if (particles.has("aoeEnabled")) aoeEnabled = particles.get("aoeEnabled").getAsBoolean();

            if (particles.has("aoeParticleCountMultiplier")) {
                aoeParticleCountMultiplier = particles.get("aoeParticleCountMultiplier").getAsFloat();
            } else if (particles.has("aoeParticleCount")) {
                aoeParticleCountMultiplier = particles.get("aoeParticleCount").getAsFloat() / 15.0f;
            }

            if (particles.has("aoeParticleDensityMultiplier")) {
                aoeParticleDensityMultiplier = particles.get("aoeParticleDensityMultiplier").getAsFloat();
            } else if (particles.has("aoeParticleDensity")) {
                aoeParticleDensityMultiplier = particles.get("aoeParticleDensity").getAsFloat();
            }

            if (particles.has("aoeParticleHeightMultiplier")) {
                aoeParticleHeightMultiplier = particles.get("aoeParticleHeightMultiplier").getAsFloat();
            } else if (particles.has("aoeMaxHeight")) {
                aoeParticleHeightMultiplier = particles.get("aoeMaxHeight").getAsFloat() / 3.0f;
            } else if (particles.has("aoeVerticalVelocityMultiplier")) {
                aoeParticleHeightMultiplier = (float) particles.get("aoeVerticalVelocityMultiplier").getAsDouble();
            }

            if (particles.has("bloodPactEnabled")) bloodPactEnabled = particles.get("bloodPactEnabled").getAsBoolean();
            if (particles.has("bloodPactParticleCount")) bloodPactParticleCount = particles.get("bloodPactParticleCount").getAsInt();
            if (particles.has("bleedingParticleEnabled")) bleedingParticleEnabled = particles.get("bleedingParticleEnabled").getAsBoolean();
            if (particles.has("uiOverlayPosition")) uiOverlayPosition = particles.get("uiOverlayPosition").getAsInt();
        }

        JsonObject ui = json.getAsJsonObject("ui");
        if (ui != null && ui.has("showDevWarning")) {
            showDevWarning = ui.get("showDevWarning").getAsBoolean();
        }
    }
}
