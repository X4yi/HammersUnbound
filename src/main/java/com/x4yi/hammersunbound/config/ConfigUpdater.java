package com.x4yi.hammersunbound.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Handles safe, incremental migration of config files between mod versions.
 *
 * Features:
 * - Schema versioning via "configVersion" field in each JSON root
 * - Automatic .bak backup before any modification
 * - Incremental patches: only changed/removed keys are touched
 * - Value sanitization (clamping to valid ranges)
 * - Rollback on write failure
 */
public class ConfigUpdater {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser PARSER = new JsonParser();

    /**
     * Entry point called by ConfigManager.init() before configs are loaded.
     */
    public static void updateConfigs(File configDir, String currentVersion) {
        if (configDir == null || !configDir.isDirectory()) return;

        updateServerConfig(new File(configDir, "server.json"), currentVersion);
        updateClientConfig(new File(configDir, "client.json"), currentVersion);
        updateItemsConfig(new File(configDir, "items.json"), currentVersion);
    }

    // ── Server Config Migration ──────────────────────────────────────────

    private static void updateServerConfig(File file, String currentVersion) {
        if (!file.exists()) return;

        JsonObject root = readJson(file);
        if (root == null) return;

        String configVersion = root.has("configVersion") ? root.get("configVersion").getAsString() : "";
        if (configVersion.equals(currentVersion)) return;

        if (!backupFile(file)) return;

        try {
            // Migration: remove deprecated warhammer multipliers
            if (root.has("warhammer")) {
                JsonObject warhammer = root.getAsJsonObject("warhammer");
                if (warhammer != null) {
                    warhammer.remove("aoeRadiusMultiplier");
                    warhammer.remove("aoeDamageMultiplier");
                }
            }

            root.addProperty("configVersion", currentVersion);

            if (writeJson(file, root)) {
                System.out.println("[Hammers Unbound] Successfully migrated server.json to " + currentVersion);
            } else {
                restoreBackup(file);
            }
        } catch (Exception e) {
            System.err.println("[Hammers Unbound] Error migrating server.json: " + e.getMessage());
            restoreBackup(file);
        }
    }

    // ── Client Config Migration ──────────────────────────────────────────

    private static void updateClientConfig(File file, String currentVersion) {
        if (!file.exists()) return;

        JsonObject root = readJson(file);
        if (root == null) return;

        String configVersion = root.has("configVersion") ? root.get("configVersion").getAsString() : "";
        if (configVersion.equals(currentVersion)) return;

        if (!backupFile(file)) return;

        try {
            if (root.has("particles")) {
                JsonObject particles = root.getAsJsonObject("particles");
                if (particles != null) {
                    // Migrate aoeParticleCount -> aoeParticleCountMultiplier
                    if (!particles.has("aoeParticleCountMultiplier")) {
                        if (particles.has("aoeParticleCount")) {
                            float multiplier = clamp(particles.get("aoeParticleCount").getAsFloat() / 15.0f, 0.1f, 5.0f);
                            particles.addProperty("aoeParticleCountMultiplier", multiplier);
                            particles.remove("aoeParticleCount");
                        } else {
                            particles.addProperty("aoeParticleCountMultiplier", 1.0f);
                        }
                    }

                    // Migrate aoeParticleDensity -> aoeParticleDensityMultiplier
                    if (!particles.has("aoeParticleDensityMultiplier")) {
                        if (particles.has("aoeParticleDensity")) {
                            float multiplier = clamp(particles.get("aoeParticleDensity").getAsFloat(), 0.1f, 5.0f);
                            particles.addProperty("aoeParticleDensityMultiplier", multiplier);
                            particles.remove("aoeParticleDensity");
                        } else {
                            particles.addProperty("aoeParticleDensityMultiplier", 1.0f);
                        }
                    }

                    // Migrate aoeMaxHeight / aoeVerticalVelocityMultiplier -> aoeParticleHeightMultiplier
                    if (!particles.has("aoeParticleHeightMultiplier")) {
                        if (particles.has("aoeMaxHeight")) {
                            float multiplier = clamp(particles.get("aoeMaxHeight").getAsFloat() / 3.0f, 0.1f, 5.0f);
                            particles.addProperty("aoeParticleHeightMultiplier", multiplier);
                            particles.remove("aoeMaxHeight");
                        } else if (particles.has("aoeVerticalVelocityMultiplier")) {
                            float multiplier = clamp(particles.get("aoeVerticalVelocityMultiplier").getAsFloat(), 0.1f, 5.0f);
                            particles.addProperty("aoeParticleHeightMultiplier", multiplier);
                            particles.remove("aoeVerticalVelocityMultiplier");
                        } else {
                            particles.addProperty("aoeParticleHeightMultiplier", 1.0f);
                        }
                    }

                    // Clean up any remaining old keys
                    particles.remove("aoeParticleCount");
                    particles.remove("aoeParticleDensity");
                    particles.remove("aoeMaxHeight");
                    particles.remove("aoeVerticalVelocityMultiplier");
                }
            }

            root.addProperty("configVersion", currentVersion);

            if (writeJson(file, root)) {
                System.out.println("[Hammers Unbound] Successfully migrated client.json to " + currentVersion);
            } else {
                restoreBackup(file);
            }
        } catch (Exception e) {
            System.err.println("[Hammers Unbound] Error migrating client.json: " + e.getMessage());
            restoreBackup(file);
        }
    }

    // ── Items Config Migration ───────────────────────────────────────────

    private static void updateItemsConfig(File file, String currentVersion) {
        if (!file.exists()) return;

        JsonObject root = readJson(file);
        if (root == null) return;

        String configVersion = root.has("configVersion") ? root.get("configVersion").getAsString() : "";
        if (configVersion.equals(currentVersion)) return;

        if (!backupFile(file)) return;

        try {
            // No items migrations for r1.0b2, just stamp version
            root.addProperty("configVersion", currentVersion);

            if (writeJson(file, root)) {
                System.out.println("[Hammers Unbound] Successfully migrated items.json to " + currentVersion);
            } else {
                restoreBackup(file);
            }
        } catch (Exception e) {
            System.err.println("[Hammers Unbound] Error migrating items.json: " + e.getMessage());
            restoreBackup(file);
        }
    }

    // ── Utility Methods ──────────────────────────────────────────────────

    private static JsonObject readJson(File file) {
        try (FileReader reader = new FileReader(file)) {
            return PARSER.parse(reader).getAsJsonObject();
        } catch (Exception e) {
            System.err.println("[Hammers Unbound] Failed to parse config: " + file.getName());
            return null;
        }
    }

    private static boolean writeJson(File file, JsonObject root) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
            return true;
        } catch (Exception e) {
            System.err.println("[Hammers Unbound] Failed to write config: " + file.getName());
            return false;
        }
    }

    private static boolean backupFile(File file) {
        try {
            File backup = new File(file.getAbsolutePath() + ".bak");
            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[Hammers Unbound] Config backup created: " + backup.getName());
            return true;
        } catch (Exception e) {
            System.err.println("[Hammers Unbound] Failed to create backup for: " + file.getName());
            return false;
        }
    }

    private static void restoreBackup(File file) {
        try {
            File backup = new File(file.getAbsolutePath() + ".bak");
            if (backup.exists()) {
                Files.copy(backup.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.err.println("[Hammers Unbound] Restored backup for: " + file.getName());
            }
        } catch (Exception e) {
            System.err.println("[Hammers Unbound] CRITICAL: Failed to restore backup for: " + file.getName());
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
