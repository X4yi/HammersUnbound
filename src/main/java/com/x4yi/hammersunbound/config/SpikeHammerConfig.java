package com.x4yi.hammersunbound.config;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class SpikeHammerConfig {

    private static final Map<String, SpikeHammerMaterialEntry> materials = new HashMap<>();

    public static void load() {
        parse(ConfigManager.loadItems());
    }

    public static void parse(JsonObject json) {
        materials.clear();
        if (json == null) {
            loadFromAssets();
            return;
        }

        JsonObject spikehammer = json.getAsJsonObject("spikehammer");
        if (spikehammer == null) {
            JsonObject mats = json.getAsJsonObject("materials");
            if (mats != null) {
                parseMaterials(json);
            } else {
                loadFromAssets();
            }
            return;
        }

        parseMaterials(spikehammer);
    }

    private static void loadFromAssets() {
        JsonObject json = ConfigLoader.loadConfig("assets/hammersunbound/config/spikehammer_stats.json");
        if (json == null) return;
        parseMaterials(json);
    }

    private static void parseMaterials(JsonObject json) {
        JsonObject mats = json.getAsJsonObject("materials");
        if (mats == null) return;

        for (Map.Entry<String, com.google.gson.JsonElement> entry : mats.entrySet()) {
            String name = entry.getKey();
            JsonObject matJson = entry.getValue().getAsJsonObject();

            HammerMaterialData data = HammerMaterialData.fromJson(name, matJson);
            BleedingConfig bleeding = BleedingConfig.fromJson(matJson.getAsJsonObject("bleeding"));
            BloodPactConfig bloodPact = BloodPactConfig.fromJson(matJson.getAsJsonObject("bloodPact"));

            materials.put(name, new SpikeHammerMaterialEntry(data, bleeding, bloodPact));
        }
    }

    public static SpikeHammerMaterialEntry getMaterial(String name) {
        return materials.get(name);
    }

    public static Map<String, SpikeHammerMaterialEntry> getAllMaterials() {
        return materials;
    }

    public static class SpikeHammerMaterialEntry {
        public HammerMaterialData data;
        public BleedingConfig bleeding;
        public BloodPactConfig bloodPact;

        public SpikeHammerMaterialEntry(HammerMaterialData data, BleedingConfig bleeding, BloodPactConfig bloodPact) {
            this.data = data;
            this.bleeding = bleeding;
            this.bloodPact = bloodPact;
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("baseDamage", data.baseDamage);
            json.addProperty("attackSpeed", HammerMaterialData.toConfigAttackSpeed(data.attackSpeed));
            json.addProperty("durability", data.durability);
            json.addProperty("skillCooldownSeconds", HammerMaterialData.ticksToSeconds(data.skillCooldown));

            json.add("bleeding", bleeding.toJson());
            json.add("bloodPact", bloodPact.toJson());

            return json;
        }
    }
}
