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
    public static boolean showChangelogButton = true;
    public static String language = "es";

    public static void load() {
        JsonObject json = ConfigManager.loadClient();
        if (json == null) return;

        JsonObject particles = json.getAsJsonObject("particles");
        if (particles != null) {
            readAoeParticles(particles);
            readCombatVisuals(particles);
            if (particles.has("uiOverlayPosition")) uiOverlayPosition = particles.get("uiOverlayPosition").getAsInt();
        }

        JsonObject aoeParticles = json.getAsJsonObject("aoeParticles");
        if (aoeParticles != null) {
            readAoeParticles(aoeParticles);
        }

        JsonObject combatVisuals = json.getAsJsonObject("combatVisuals");
        if (combatVisuals != null) {
            readCombatVisuals(combatVisuals);
        }

        JsonObject ui = json.getAsJsonObject("ui");
        if (ui != null) {
            if (ui.has("uiOverlayPosition")) {
                uiOverlayPosition = ui.get("uiOverlayPosition").getAsInt();
            }
            if (ui.has("showDevWarning")) {
                showDevWarning = ui.get("showDevWarning").getAsBoolean();
            }
            if (ui.has("showChangelogButton")) {
                showChangelogButton = ui.get("showChangelogButton").getAsBoolean();
            }
            if (ui.has("language")) {
                language = ui.get("language").getAsString();
            }
        }
    }

    private static void readAoeParticles(JsonObject json) {
        if (json.has("aoeEnabled")) aoeEnabled = json.get("aoeEnabled").getAsBoolean();

        if (json.has("aoeParticleCountMultiplier")) {
            aoeParticleCountMultiplier = json.get("aoeParticleCountMultiplier").getAsFloat();
        } else if (json.has("aoeParticleCount")) {
            aoeParticleCountMultiplier = json.get("aoeParticleCount").getAsFloat() / 15.0f;
        }

        if (json.has("aoeParticleDensityMultiplier")) {
            aoeParticleDensityMultiplier = json.get("aoeParticleDensityMultiplier").getAsFloat();
        } else if (json.has("aoeParticleDensity")) {
            aoeParticleDensityMultiplier = json.get("aoeParticleDensity").getAsFloat();
        }

        if (json.has("aoeParticleHeightMultiplier")) {
            aoeParticleHeightMultiplier = json.get("aoeParticleHeightMultiplier").getAsFloat();
        } else if (json.has("aoeMaxHeight")) {
            aoeParticleHeightMultiplier = json.get("aoeMaxHeight").getAsFloat() / 3.0f;
        } else if (json.has("aoeVerticalVelocityMultiplier")) {
            aoeParticleHeightMultiplier = (float) json.get("aoeVerticalVelocityMultiplier").getAsDouble();
        }
    }

    private static void readCombatVisuals(JsonObject json) {
        if (json.has("bloodPactEnabled")) bloodPactEnabled = json.get("bloodPactEnabled").getAsBoolean();
        if (json.has("bloodPactParticleCount")) bloodPactParticleCount = json.get("bloodPactParticleCount").getAsInt();
        if (json.has("bleedingParticleEnabled")) bleedingParticleEnabled = json.get("bleedingParticleEnabled").getAsBoolean();
    }
}
