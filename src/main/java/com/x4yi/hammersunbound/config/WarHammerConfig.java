package com.x4yi.hammersunbound.config;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class WarHammerConfig {

    private static final Map<String, WarHammerMaterialEntry> materials = new HashMap<>();

    public static void load() {
        parse(ConfigManager.loadItems());
    }

    public static void parse(JsonObject json) {
        materials.clear();
        if (json == null) {
            loadFromAssets();
            return;
        }

        JsonObject warhammer = json.getAsJsonObject("warhammer");
        if (warhammer == null) {
            JsonObject mats = json.getAsJsonObject("materials");
            if (mats != null) {
                parseMaterials(json);
            } else {
                loadFromAssets();
            }
            return;
        }

        parseMaterials(warhammer);
    }

    private static void loadFromAssets() {
        JsonObject json = ConfigLoader.loadConfig("assets/hammersunbound/config/warhammer_stats.json");
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
            WarHammerAbilities abilities = WarHammerAbilities.fromJson(matJson.getAsJsonObject("abilities"));

            materials.put(name, new WarHammerMaterialEntry(data, abilities));
        }
    }

    public static WarHammerMaterialEntry getMaterial(String name) {
        return materials.get(name);
    }

    public static Map<String, WarHammerMaterialEntry> getAllMaterials() {
        return materials;
    }

    public static class WarHammerMaterialEntry {
        public HammerMaterialData data;
        public WarHammerAbilities abilities;

        public WarHammerMaterialEntry(HammerMaterialData data, WarHammerAbilities abilities) {
            this.data = data;
            this.abilities = abilities;
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("baseDamage", data.baseDamage);
            json.addProperty("attackSpeed", data.attackSpeed);
            json.addProperty("durability", data.durability);
            json.addProperty("skillCooldown", data.skillCooldown);

            JsonObject ab = new JsonObject();
            ab.addProperty("stunDuration", abilities.stunDuration);
            ab.addProperty("stunAmplifier", abilities.stunAmplifier);
            ab.addProperty("aoeRadius", abilities.aoeRadius);
            ab.addProperty("aoeDamage", abilities.aoeDamage);
            ab.addProperty("aoeStunDuration", abilities.aoeStunDuration);
            ab.addProperty("aoeStunAmplifier", abilities.aoeStunAmplifier);
            json.add("abilities", ab);

            return json;
        }
    }
}
