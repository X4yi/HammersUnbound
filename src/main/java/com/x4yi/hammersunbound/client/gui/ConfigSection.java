package com.x4yi.hammersunbound.client.gui;
import com.x4yi.hammersunbound.config.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
public class ConfigSection {
    public enum Section {
        ITEMS("Items"),
        SERVER("Server"),
        CLIENT("Client");
        private final String displayName;
        Section(String displayName) {
            this.displayName = displayName;
        }
        public String getDisplayName() {
            return displayName;
        }
    }
    public static class SubSection {
        private final String id;
        private final String displayName;
        private final Section parentSection;
        private final List<ConfigField> fields;
        public SubSection(String id, String displayName, Section parentSection, List<ConfigField> fields) {
            this.id = id;
            this.displayName = displayName;
            this.parentSection = parentSection;
            this.fields = fields;
        }
        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public Section getParentSection() { return parentSection; }
        public List<ConfigField> getFields() { return fields; }
    }
    public static class ConfigField {
        public enum Type { FLOAT, INT, BOOLEAN }
        private final String key;
        private final String label;
        private final Type type;
        private final float minValue;
        private final float maxValue;
        private Supplier<Float> floatGetter;
        private Consumer<Float> floatSetter;
        private Supplier<Integer> intGetter;
        private Consumer<Integer> intSetter;
        private Supplier<Boolean> boolGetter;
        private Consumer<Boolean> boolSetter;
        private Runnable resetter;
        private ConfigField(String key, String label, Type type, float minValue, float maxValue) {
            this.key = key;
            this.label = label;
            this.type = type;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
        public static ConfigField floatField(String key, String label, float min, float max, Supplier<Float> getter, Consumer<Float> setter, Runnable resetter) {
            ConfigField f = new ConfigField(key, label, Type.FLOAT, min, max);
            f.floatGetter = getter;
            f.floatSetter = setter;
            f.resetter = resetter;
            return f;
        }
        public static ConfigField intField(String key, String label, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter, Runnable resetter) {
            ConfigField f = new ConfigField(key, label, Type.INT, min, max);
            f.intGetter = getter;
            f.intSetter = setter;
            f.resetter = resetter;
            return f;
        }
        public static ConfigField boolField(String key, String label, Supplier<Boolean> getter, Consumer<Boolean> setter, Runnable resetter) {
            ConfigField f = new ConfigField(key, label, Type.BOOLEAN, 0, 1);
            f.boolGetter = getter;
            f.boolSetter = setter;
            f.resetter = resetter;
            return f;
        }
        public String getKey() { return key; }
        public String getLabel() { return label; }
        public Type getType() { return type; }
        public float getMinValue() { return minValue; }
        public float getMaxValue() { return maxValue; }
        public float getFloatValue() {
            if (type == Type.INT) return getIntValue();
            return floatGetter != null ? floatGetter.get() : 0f;
        }
        public void setFloatValue(float value) {
            if (type == Type.INT) {
                setIntValue(Math.round(value));
            } else if (floatSetter != null) {
                floatSetter.accept(value);
            }
        }
        public int getIntValue() { return intGetter != null ? intGetter.get() : 0; }
        public void setIntValue(int value) { if (intSetter != null) intSetter.accept(value); }
        public boolean getBoolValue() { return boolGetter != null ? boolGetter.get() : false; }
        public void setBoolValue(boolean value) { if (boolSetter != null) boolSetter.accept(value); }
        public void resetToDefault() { if (resetter != null) resetter.run(); }
    }
    private static final Map<Section, List<SubSection>> sections = new LinkedHashMap<>();
    public static void buildSections() {
        sections.clear();
        com.google.gson.JsonObject defaultWh = ConfigLoader.loadConfig("assets/hammersunbound/config/warhammer_stats.json");
        com.google.gson.JsonObject defaultSh = ConfigLoader.loadConfig("assets/hammersunbound/config/spikehammer_stats.json");
        List<SubSection> itemSubs = new ArrayList<>();
        String[] orderedMaterials = {"wood", "stone", "iron", "gold", "diamond"};
        for (String material : orderedMaterials) {
            WarHammerConfig.WarHammerMaterialEntry entry = WarHammerConfig.getMaterial(material);
            if (entry == null) continue;
            com.google.gson.JsonObject matJson = defaultWh != null ? defaultWh.getAsJsonObject("materials").getAsJsonObject(material) : null;
            HammerMaterialData defData = matJson != null ? HammerMaterialData.fromJson(material, matJson) : entry.data;
            WarHammerAbilities defAbilities = matJson != null ? WarHammerAbilities.fromJson(matJson.getAsJsonObject("abilities")) : entry.abilities;
            List<ConfigField> fields = new ArrayList<>();
            fields.add(ConfigField.floatField("damage", "Damage", 0, 100, () -> entry.data.baseDamage, v -> entry.data.baseDamage = v, () -> entry.data.baseDamage = defData.baseDamage));
            fields.add(ConfigField.floatField("speed", "Attack Speed", 0.1f, 4.0f, () -> HammerMaterialData.toConfigAttackSpeed(entry.data.attackSpeed), v -> entry.data.attackSpeed = HammerMaterialData.fromConfigAttackSpeed(v), () -> entry.data.attackSpeed = defData.attackSpeed));
            fields.add(ConfigField.intField("durability", "Durability", 1, 10000, () -> entry.data.durability, v -> entry.data.durability = v, () -> entry.data.durability = defData.durability));
            fields.add(ConfigField.floatField("skillCooldown", "Skill Cooldown (s)", 0, 60, () -> HammerMaterialData.ticksToSeconds(entry.data.skillCooldown), v -> entry.data.skillCooldown = HammerMaterialData.secondsToTicks(v), () -> entry.data.skillCooldown = defData.skillCooldown));
            fields.add(ConfigField.floatField("aoeRadius", "AOE Radius", 0.5f, 10, () -> entry.abilities.aoeRadius, v -> entry.abilities.aoeRadius = v, () -> entry.abilities.aoeRadius = defAbilities.aoeRadius));
            fields.add(ConfigField.floatField("aoeDamage", "AOE Damage", 0, 50, () -> entry.abilities.aoeDamage, v -> entry.abilities.aoeDamage = v, () -> entry.abilities.aoeDamage = defAbilities.aoeDamage));
            fields.add(ConfigField.floatField("stunDuration", "Stun Duration (s)", 0, 20, () -> HammerMaterialData.ticksToSeconds(entry.abilities.stunDuration), v -> entry.abilities.stunDuration = HammerMaterialData.secondsToTicks(v), () -> entry.abilities.stunDuration = defAbilities.stunDuration));
            fields.add(ConfigField.floatField("aoeStunDuration", "AOE Stun Duration (s)", 0, 20, () -> HammerMaterialData.ticksToSeconds(entry.abilities.aoeStunDuration), v -> entry.abilities.aoeStunDuration = HammerMaterialData.secondsToTicks(v), () -> entry.abilities.aoeStunDuration = defAbilities.aoeStunDuration));
            itemSubs.add(new SubSection("warhammer_" + material, "WarHammer " + capitalize(material), Section.ITEMS, fields));
        }
        for (String material : orderedMaterials) {
            SpikeHammerConfig.SpikeHammerMaterialEntry entry = SpikeHammerConfig.getMaterial(material);
            if (entry == null) continue;
            com.google.gson.JsonObject matJson = defaultSh != null ? defaultSh.getAsJsonObject("materials").getAsJsonObject(material) : null;
            HammerMaterialData defData = matJson != null ? HammerMaterialData.fromJson(material, matJson) : entry.data;
            com.x4yi.hammersunbound.config.BleedingConfig defBleed = matJson != null ? com.x4yi.hammersunbound.config.BleedingConfig.fromJson(matJson.getAsJsonObject("bleeding")) : entry.bleeding;
            com.x4yi.hammersunbound.config.BloodPactConfig defPact = matJson != null ? com.x4yi.hammersunbound.config.BloodPactConfig.fromJson(matJson.getAsJsonObject("bloodPact")) : entry.bloodPact;
            List<ConfigField> fields = new ArrayList<>();
            fields.add(ConfigField.floatField("damage", "Damage", 0, 100, () -> entry.data.baseDamage, v -> entry.data.baseDamage = v, () -> entry.data.baseDamage = defData.baseDamage));
            fields.add(ConfigField.floatField("speed", "Attack Speed", 0.1f, 4.0f, () -> HammerMaterialData.toConfigAttackSpeed(entry.data.attackSpeed), v -> entry.data.attackSpeed = HammerMaterialData.fromConfigAttackSpeed(v), () -> entry.data.attackSpeed = defData.attackSpeed));
            fields.add(ConfigField.intField("durability", "Durability", 1, 10000, () -> entry.data.durability, v -> entry.data.durability = v, () -> entry.data.durability = defData.durability));
            fields.add(ConfigField.floatField("skillCooldown", "Skill Cooldown (s)", 0, 60, () -> HammerMaterialData.ticksToSeconds(entry.data.skillCooldown), v -> entry.data.skillCooldown = HammerMaterialData.secondsToTicks(v), () -> entry.data.skillCooldown = defData.skillCooldown));
            fields.add(ConfigField.floatField("bleedDamage", "Bleed Damage", 0, 10, () -> entry.bleeding.damagePerLevel, v -> entry.bleeding.damagePerLevel = v, () -> entry.bleeding.damagePerLevel = defBleed.damagePerLevel));
            fields.add(ConfigField.floatField("bleedDuration", "Bleed Duration (s)", 0, 60, () -> HammerMaterialData.ticksToSeconds(entry.bleeding.baseDuration), v -> entry.bleeding.baseDuration = HammerMaterialData.secondsToTicks(v), () -> entry.bleeding.baseDuration = defBleed.baseDuration));
            fields.add(ConfigField.floatField("bleedTickInterval", "Bleed Tick Interval (s)", 0.25f, 10, () -> HammerMaterialData.ticksToSeconds(entry.bleeding.tickInterval), v -> entry.bleeding.tickInterval = HammerMaterialData.secondsToTicks(v), () -> entry.bleeding.tickInterval = defBleed.tickInterval));
            fields.add(ConfigField.floatField("bleedDecay", "Bleed Decay (s)", 0, 60, () -> HammerMaterialData.ticksToSeconds(entry.bleeding.decayTicks), v -> entry.bleeding.decayTicks = HammerMaterialData.secondsToTicks(v), () -> entry.bleeding.decayTicks = defBleed.decayTicks));
            fields.add(ConfigField.floatField("bloodPactRange", "Blood Pact Range", 1, 32, () -> entry.bloodPact.range, v -> entry.bloodPact.range = v, () -> entry.bloodPact.range = defPact.range));
            fields.add(ConfigField.floatField("bloodPactDrainPercent", "Pact Drain Percent", 0.0f, 1.0f, () -> entry.bloodPact.drainPercent, v -> entry.bloodPact.drainPercent = v, () -> entry.bloodPact.drainPercent = defPact.drainPercent));
            fields.add(ConfigField.floatField("bloodPactTetherBreakDistance", "Pact Break Distance", 1, 64, () -> entry.bloodPact.tetherBreakDistance, v -> entry.bloodPact.tetherBreakDistance = v, () -> entry.bloodPact.tetherBreakDistance = defPact.tetherBreakDistance));
            fields.add(ConfigField.intField("bloodPactMaxTargets", "Pact Max Targets", 1, 10, () -> entry.bloodPact.maxTargets, v -> entry.bloodPact.maxTargets = v, () -> entry.bloodPact.maxTargets = defPact.maxTargets));
            fields.add(ConfigField.floatField("bloodPactFieldRadius", "Pact Field Radius", 1, 20, () -> entry.bloodPact.fieldRadius, v -> entry.bloodPact.fieldRadius = v, () -> entry.bloodPact.fieldRadius = defPact.fieldRadius));
            fields.add(ConfigField.floatField("bloodPactRepulsionForce", "Pact Repulsion Force", 0, 5, () -> entry.bloodPact.repulsionForce, v -> entry.bloodPact.repulsionForce = v, () -> entry.bloodPact.repulsionForce = defPact.repulsionForce));
            fields.add(ConfigField.floatField("bloodPactAttractionForce", "Pact Attraction Force", 0, 5, () -> entry.bloodPact.attractionForce, v -> entry.bloodPact.attractionForce = v, () -> entry.bloodPact.attractionForce = defPact.attractionForce));
            fields.add(ConfigField.floatField("bloodPactBaseDuration", "Pact Base Duration (s)", 1, 60, () -> HammerMaterialData.ticksToSeconds(entry.bloodPact.baseDurationTicks), v -> entry.bloodPact.baseDurationTicks = HammerMaterialData.secondsToTicks(v), () -> entry.bloodPact.baseDurationTicks = defPact.baseDurationTicks));
            fields.add(ConfigField.floatField("bloodPactHitBonus", "Pact Hit Bonus (s)", 0, 10, () -> HammerMaterialData.ticksToSeconds(entry.bloodPact.hitBonusTicks), v -> entry.bloodPact.hitBonusTicks = HammerMaterialData.secondsToTicks(v), () -> entry.bloodPact.hitBonusTicks = defPact.hitBonusTicks));
            fields.add(ConfigField.floatField("bloodPactDamagePenalty", "Pact Damage Penalty (s)", 0, 10, () -> HammerMaterialData.ticksToSeconds(entry.bloodPact.damagePenaltyTicks), v -> entry.bloodPact.damagePenaltyTicks = HammerMaterialData.secondsToTicks(v), () -> entry.bloodPact.damagePenaltyTicks = defPact.damagePenaltyTicks));
            fields.add(ConfigField.floatField("bloodPactAoeAttackSize", "Pact AoE Attack Size", 0.1f, 10, () -> entry.bloodPact.aoeAttackSize, v -> entry.bloodPact.aoeAttackSize = v, () -> entry.bloodPact.aoeAttackSize = defPact.aoeAttackSize));
            itemSubs.add(new SubSection("spikehammer_" + material, "SpikeHammer " + capitalize(material), Section.ITEMS, fields));
        }
        sections.put(Section.ITEMS, itemSubs);
        List<SubSection> serverSubs = new ArrayList<>();
        List<ConfigField> whServerFields = new ArrayList<>();
        whServerFields.add(ConfigField.floatField("stunDurationMult", "Stun Duration Multiplier", 0.1f, 5, () -> ServerConfig.warhammerStunDurationMultiplier, v -> ServerConfig.warhammerStunDurationMultiplier = v, () -> ServerConfig.warhammerStunDurationMultiplier = 1.0f));
        whServerFields.add(ConfigField.boolField("enableAOE", "Enable AOE", () -> ServerConfig.warhammerEnableAOE, v -> ServerConfig.warhammerEnableAOE = v, () -> ServerConfig.warhammerEnableAOE = true));
        whServerFields.add(ConfigField.boolField("enableStun", "Enable Stun", () -> ServerConfig.warhammerEnableStun, v -> ServerConfig.warhammerEnableStun = v, () -> ServerConfig.warhammerEnableStun = true));
        whServerFields.add(ConfigField.floatField("serverAoeParticleSyncDistance", "AOE Particle Sync Distance", 1.0f, 256.0f, () -> (float)ServerConfig.serverAoeParticleSyncDistance, v -> ServerConfig.serverAoeParticleSyncDistance = (double)v, () -> ServerConfig.serverAoeParticleSyncDistance = 64.0));
        serverSubs.add(new SubSection("server_warhammer", "WarHammer (Server)", Section.SERVER, whServerFields));
        List<ConfigField> shServerFields = new ArrayList<>();
        shServerFields.add(ConfigField.floatField("bleedDamageMult", "Bleed Damage Multiplier", 0.1f, 5, () -> ServerConfig.spikehammerBleedingDamageMultiplier, v -> ServerConfig.spikehammerBleedingDamageMultiplier = v, () -> ServerConfig.spikehammerBleedingDamageMultiplier = 1.0f));
        shServerFields.add(ConfigField.floatField("bleedDurationMult", "Bleed Duration Multiplier", 0.1f, 5, () -> ServerConfig.spikehammerBleedingDurationMultiplier, v -> ServerConfig.spikehammerBleedingDurationMultiplier = v, () -> ServerConfig.spikehammerBleedingDurationMultiplier = 1.0f));
        shServerFields.add(ConfigField.floatField("bloodPactRangeMult", "Blood Pact Range Multiplier", 0.1f, 5, () -> ServerConfig.spikehammerBloodPactRangeMultiplier, v -> ServerConfig.spikehammerBloodPactRangeMultiplier = v, () -> ServerConfig.spikehammerBloodPactRangeMultiplier = 1.0f));
        shServerFields.add(ConfigField.floatField("bloodPactDrainMult", "Blood Pact Drain Multiplier", 0.1f, 5, () -> ServerConfig.spikehammerBloodPactDrainMultiplier, v -> ServerConfig.spikehammerBloodPactDrainMultiplier = v, () -> ServerConfig.spikehammerBloodPactDrainMultiplier = 1.0f));
        shServerFields.add(ConfigField.boolField("enableBleeding", "Enable Bleeding", () -> ServerConfig.spikehammerEnableBleeding, v -> ServerConfig.spikehammerEnableBleeding = v, () -> ServerConfig.spikehammerEnableBleeding = true));
        shServerFields.add(ConfigField.boolField("enableBloodPact", "Enable Blood Pact", () -> ServerConfig.spikehammerEnableBloodPact, v -> ServerConfig.spikehammerEnableBloodPact = v, () -> ServerConfig.spikehammerEnableBloodPact = true));
        serverSubs.add(new SubSection("server_spikehammer", "SpikeHammer (Server)", Section.SERVER, shServerFields));
        sections.put(Section.SERVER, serverSubs);
        List<SubSection> clientSubs = new ArrayList<>();
        List<ConfigField> aoeParticleFields = new ArrayList<>();
        aoeParticleFields.add(ConfigField.boolField("aoeEnabled", "AOE Particles Enabled", () -> ClientConfig.aoeEnabled, v -> ClientConfig.aoeEnabled = v, () -> ClientConfig.aoeEnabled = true));
        aoeParticleFields.add(ConfigField.floatField("aoeCountMultiplier", "AOE Particle Count Multiplier", 0.1f, 5.0f, () -> ClientConfig.aoeParticleCountMultiplier, v -> ClientConfig.aoeParticleCountMultiplier = v, () -> ClientConfig.aoeParticleCountMultiplier = 1.0f));
        aoeParticleFields.add(ConfigField.floatField("aoeDensityMultiplier", "AOE Particle Density Multiplier", 0.1f, 5.0f, () -> ClientConfig.aoeParticleDensityMultiplier, v -> ClientConfig.aoeParticleDensityMultiplier = v, () -> ClientConfig.aoeParticleDensityMultiplier = 1.0f));
        aoeParticleFields.add(ConfigField.floatField("aoeHeightMultiplier", "AOE Particle Height Multiplier", 0.1f, 5.0f, () -> ClientConfig.aoeParticleHeightMultiplier, v -> ClientConfig.aoeParticleHeightMultiplier = v, () -> ClientConfig.aoeParticleHeightMultiplier = 1.0f));
        clientSubs.add(new SubSection("client_aoe_particles", "AOE Particles", Section.CLIENT, aoeParticleFields));
        List<ConfigField> combatVisualFields = new ArrayList<>();
        combatVisualFields.add(ConfigField.boolField("bloodPactEnabled", "Blood Pact Visual", () -> ClientConfig.bloodPactEnabled, v -> ClientConfig.bloodPactEnabled = v, () -> ClientConfig.bloodPactEnabled = true));
        combatVisualFields.add(ConfigField.intField("bloodPactParticleCount", "Pact Particle Density", 1, 100, () -> ClientConfig.bloodPactParticleCount, v -> ClientConfig.bloodPactParticleCount = v, () -> ClientConfig.bloodPactParticleCount = 5));
        combatVisualFields.add(ConfigField.boolField("bleedingParticles", "Bleeding Particles", () -> ClientConfig.bleedingParticleEnabled, v -> ClientConfig.bleedingParticleEnabled = v, () -> ClientConfig.bleedingParticleEnabled = true));
        clientSubs.add(new SubSection("client_combat_visuals", "Combat Visuals", Section.CLIENT, combatVisualFields));
        List<ConfigField> uiFields = new ArrayList<>();
        uiFields.add(ConfigField.boolField("showChangelogButton", "Main Menu Changelog Button", () -> ClientConfig.showChangelogButton, v -> ClientConfig.showChangelogButton = v, () -> ClientConfig.showChangelogButton = true));
        uiFields.add(ConfigField.intField("uiOverlayPosition", "UI Position (0-4)", 0, 4, () -> ClientConfig.uiOverlayPosition, v -> ClientConfig.uiOverlayPosition = v, () -> ClientConfig.uiOverlayPosition = 0));
        uiFields.add(ConfigField.boolField("showDevWarning", "Show Dev Warning Popup", () -> ClientConfig.showDevWarning, v -> ClientConfig.showDevWarning = v, () -> ClientConfig.showDevWarning = true));
        clientSubs.add(new SubSection("client_ui", "UI", Section.CLIENT, uiFields));
        sections.put(Section.CLIENT, clientSubs);
    }
    public static Map<Section, List<SubSection>> getSections() {
        return sections;
    }
    public static List<SubSection> getSubSections(Section section) {
        return sections.getOrDefault(section, new ArrayList<SubSection>());
    }
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}