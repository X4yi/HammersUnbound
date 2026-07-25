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

        private ConfigField(String key, String label, Type type, float minValue, float maxValue) {
            this.key = key;
            this.label = label;
            this.type = type;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public static ConfigField floatField(String key, String label, float min, float max, Supplier<Float> getter, Consumer<Float> setter) {
            ConfigField f = new ConfigField(key, label, Type.FLOAT, min, max);
            f.floatGetter = getter;
            f.floatSetter = setter;
            return f;
        }

        public static ConfigField intField(String key, String label, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
            ConfigField f = new ConfigField(key, label, Type.INT, min, max);
            f.intGetter = getter;
            f.intSetter = setter;
            return f;
        }

        public static ConfigField boolField(String key, String label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
            ConfigField f = new ConfigField(key, label, Type.BOOLEAN, 0, 1);
            f.boolGetter = getter;
            f.boolSetter = setter;
            return f;
        }

        public String getKey() { return key; }
        public String getLabel() { return label; }
        public Type getType() { return type; }
        public float getMinValue() { return minValue; }
        public float getMaxValue() { return maxValue; }

        public float getFloatValue() { return floatGetter != null ? floatGetter.get() : 0f; }
        public void setFloatValue(float value) { if (floatSetter != null) floatSetter.accept(value); }

        public int getIntValue() { return intGetter != null ? intGetter.get() : 0; }
        public void setIntValue(int value) { if (intSetter != null) intSetter.accept(value); }

        public boolean getBoolValue() { return boolGetter != null ? boolGetter.get() : false; }
        public void setBoolValue(boolean value) { if (boolSetter != null) boolSetter.accept(value); }
    }

    private static final Map<Section, List<SubSection>> sections = new LinkedHashMap<>();

    public static void buildSections() {
        sections.clear();
        List<SubSection> itemSubs = new ArrayList<>();
        String[] orderedMaterials = {"wood", "stone", "iron", "gold", "diamond"};
        
        for (String material : orderedMaterials) {
            WarHammerConfig.WarHammerMaterialEntry entry = WarHammerConfig.getMaterial(material);
            if (entry == null) continue;
            List<ConfigField> fields = new ArrayList<>();
            fields.add(ConfigField.floatField("damage", "Damage", 0, 100, () -> entry.data.baseDamage, v -> entry.data.baseDamage = v));
            fields.add(ConfigField.floatField("speed", "Attack Speed", 0.1f, 4.0f, () -> HammerMaterialData.toConfigAttackSpeed(entry.data.attackSpeed), v -> entry.data.attackSpeed = HammerMaterialData.fromConfigAttackSpeed(v)));
            fields.add(ConfigField.intField("durability", "Durability", 1, 10000, () -> entry.data.durability, v -> entry.data.durability = v));
            fields.add(ConfigField.floatField("skillCooldown", "Skill Cooldown (s)", 0, 60, () -> HammerMaterialData.ticksToSeconds(entry.data.skillCooldown), v -> entry.data.skillCooldown = HammerMaterialData.secondsToTicks(v)));
            fields.add(ConfigField.floatField("aoeRadius", "AOE Radius", 0.5f, 10, () -> entry.abilities.aoeRadius, v -> entry.abilities.aoeRadius = v));
            fields.add(ConfigField.floatField("aoeDamage", "AOE Damage", 0, 50, () -> entry.abilities.aoeDamage, v -> entry.abilities.aoeDamage = v));
            fields.add(ConfigField.floatField("stunDuration", "Stun Duration (s)", 0, 20, () -> HammerMaterialData.ticksToSeconds(entry.abilities.stunDuration), v -> entry.abilities.stunDuration = HammerMaterialData.secondsToTicks(v)));
            fields.add(ConfigField.floatField("aoeStunDuration", "AOE Stun Duration (s)", 0, 20, () -> HammerMaterialData.ticksToSeconds(entry.abilities.aoeStunDuration), v -> entry.abilities.aoeStunDuration = HammerMaterialData.secondsToTicks(v)));
            itemSubs.add(new SubSection("warhammer_" + material, "WarHammer " + capitalize(material), Section.ITEMS, fields));
        }
        
        for (String material : orderedMaterials) {
            SpikeHammerConfig.SpikeHammerMaterialEntry entry = SpikeHammerConfig.getMaterial(material);
            if (entry == null) continue;
            List<ConfigField> fields = new ArrayList<>();
            fields.add(ConfigField.floatField("damage", "Damage", 0, 100, () -> entry.data.baseDamage, v -> entry.data.baseDamage = v));
            fields.add(ConfigField.floatField("speed", "Attack Speed", 0.1f, 4.0f, () -> HammerMaterialData.toConfigAttackSpeed(entry.data.attackSpeed), v -> entry.data.attackSpeed = HammerMaterialData.fromConfigAttackSpeed(v)));
            fields.add(ConfigField.intField("durability", "Durability", 1, 10000, () -> entry.data.durability, v -> entry.data.durability = v));
            fields.add(ConfigField.floatField("skillCooldown", "Skill Cooldown (s)", 0, 60, () -> HammerMaterialData.ticksToSeconds(entry.data.skillCooldown), v -> entry.data.skillCooldown = HammerMaterialData.secondsToTicks(v)));
            fields.add(ConfigField.floatField("bleedDamage", "Bleed Damage", 0, 10, () -> entry.bleeding.damagePerLevel, v -> entry.bleeding.damagePerLevel = v));
            fields.add(ConfigField.floatField("bleedDuration", "Bleed Duration (s)", 0, 60, () -> HammerMaterialData.ticksToSeconds(entry.bleeding.baseDuration), v -> entry.bleeding.baseDuration = HammerMaterialData.secondsToTicks(v)));
            fields.add(ConfigField.floatField("bleedTickInterval", "Bleed Tick Interval (s)", 0.25f, 10, () -> HammerMaterialData.ticksToSeconds(entry.bleeding.tickInterval), v -> entry.bleeding.tickInterval = HammerMaterialData.secondsToTicks(v)));
            fields.add(ConfigField.floatField("bleedDecay", "Bleed Decay (s)", 0, 60, () -> HammerMaterialData.ticksToSeconds(entry.bleeding.decayTicks), v -> entry.bleeding.decayTicks = HammerMaterialData.secondsToTicks(v)));
            fields.add(ConfigField.floatField("bloodPactRange", "Blood Pact Range", 1, 32, () -> entry.bloodPact.range, v -> entry.bloodPact.range = v));
            fields.add(ConfigField.floatField("bloodPactDrainPercent", "Pact Drain Percent", 0.0f, 1.0f, () -> entry.bloodPact.drainPercent, v -> entry.bloodPact.drainPercent = v));
            fields.add(ConfigField.floatField("bloodPactTetherBreakDistance", "Pact Break Distance", 1, 64, () -> entry.bloodPact.tetherBreakDistance, v -> entry.bloodPact.tetherBreakDistance = v));
            fields.add(ConfigField.intField("bloodPactMaxTargets", "Pact Max Targets", 1, 10, () -> entry.bloodPact.maxTargets, v -> entry.bloodPact.maxTargets = v));
            fields.add(ConfigField.floatField("bloodPactFieldRadius", "Pact Field Radius", 1, 20, () -> entry.bloodPact.fieldRadius, v -> entry.bloodPact.fieldRadius = v));
            fields.add(ConfigField.floatField("bloodPactRepulsionForce", "Pact Repulsion Force", 0, 5, () -> entry.bloodPact.repulsionForce, v -> entry.bloodPact.repulsionForce = v));
            fields.add(ConfigField.floatField("bloodPactAttractionForce", "Pact Attraction Force", 0, 5, () -> entry.bloodPact.attractionForce, v -> entry.bloodPact.attractionForce = v));
            fields.add(ConfigField.floatField("bloodPactBaseDuration", "Pact Base Duration (s)", 1, 60, () -> HammerMaterialData.ticksToSeconds(entry.bloodPact.baseDurationTicks), v -> entry.bloodPact.baseDurationTicks = HammerMaterialData.secondsToTicks(v)));
            fields.add(ConfigField.floatField("bloodPactHitBonus", "Pact Hit Bonus (s)", 0, 10, () -> HammerMaterialData.ticksToSeconds(entry.bloodPact.hitBonusTicks), v -> entry.bloodPact.hitBonusTicks = HammerMaterialData.secondsToTicks(v)));
            fields.add(ConfigField.floatField("bloodPactDamagePenalty", "Pact Damage Penalty (s)", 0, 10, () -> HammerMaterialData.ticksToSeconds(entry.bloodPact.damagePenaltyTicks), v -> entry.bloodPact.damagePenaltyTicks = HammerMaterialData.secondsToTicks(v)));
            fields.add(ConfigField.floatField("bloodPactAoeAttackSize", "Pact AoE Attack Size", 0.1f, 10, () -> entry.bloodPact.aoeAttackSize, v -> entry.bloodPact.aoeAttackSize = v));
            itemSubs.add(new SubSection("spikehammer_" + material, "SpikeHammer " + capitalize(material), Section.ITEMS, fields));
        }
        sections.put(Section.ITEMS, itemSubs);

        List<SubSection> serverSubs = new ArrayList<>();
        List<ConfigField> whServerFields = new ArrayList<>();
        whServerFields.add(ConfigField.floatField("stunDurationMult", "Stun Duration Multiplier", 0.1f, 5, () -> ServerConfig.warhammerStunDurationMultiplier, v -> ServerConfig.warhammerStunDurationMultiplier = v));
        whServerFields.add(ConfigField.boolField("enableAOE", "Enable AOE", () -> ServerConfig.warhammerEnableAOE, v -> ServerConfig.warhammerEnableAOE = v));
        whServerFields.add(ConfigField.boolField("enableStun", "Enable Stun", () -> ServerConfig.warhammerEnableStun, v -> ServerConfig.warhammerEnableStun = v));
        whServerFields.add(ConfigField.floatField("serverAoeParticleSyncDistance", "AOE Particle Sync Distance", 1.0f, 256.0f, () -> (float)ServerConfig.serverAoeParticleSyncDistance, v -> ServerConfig.serverAoeParticleSyncDistance = (double)v));
        serverSubs.add(new SubSection("server_warhammer", "WarHammer (Server)", Section.SERVER, whServerFields));
        
        List<ConfigField> shServerFields = new ArrayList<>();
        shServerFields.add(ConfigField.floatField("bleedDamageMult", "Bleed Damage Multiplier", 0.1f, 5, () -> ServerConfig.spikehammerBleedingDamageMultiplier, v -> ServerConfig.spikehammerBleedingDamageMultiplier = v));
        shServerFields.add(ConfigField.floatField("bleedDurationMult", "Bleed Duration Multiplier", 0.1f, 5, () -> ServerConfig.spikehammerBleedingDurationMultiplier, v -> ServerConfig.spikehammerBleedingDurationMultiplier = v));
        shServerFields.add(ConfigField.floatField("bloodPactRangeMult", "Blood Pact Range Multiplier", 0.1f, 5, () -> ServerConfig.spikehammerBloodPactRangeMultiplier, v -> ServerConfig.spikehammerBloodPactRangeMultiplier = v));
        shServerFields.add(ConfigField.floatField("bloodPactDrainMult", "Blood Pact Drain Multiplier", 0.1f, 5, () -> ServerConfig.spikehammerBloodPactDrainMultiplier, v -> ServerConfig.spikehammerBloodPactDrainMultiplier = v));
        shServerFields.add(ConfigField.boolField("enableBleeding", "Enable Bleeding", () -> ServerConfig.spikehammerEnableBleeding, v -> ServerConfig.spikehammerEnableBleeding = v));
        shServerFields.add(ConfigField.boolField("enableBloodPact", "Enable Blood Pact", () -> ServerConfig.spikehammerEnableBloodPact, v -> ServerConfig.spikehammerEnableBloodPact = v));
        serverSubs.add(new SubSection("server_spikehammer", "SpikeHammer (Server)", Section.SERVER, shServerFields));
        sections.put(Section.SERVER, serverSubs);

        List<SubSection> clientSubs = new ArrayList<>();
        List<ConfigField> aoeParticleFields = new ArrayList<>();
        aoeParticleFields.add(ConfigField.boolField("aoeEnabled", "AOE Particles Enabled", () -> ClientConfig.aoeEnabled, v -> ClientConfig.aoeEnabled = v));
        aoeParticleFields.add(ConfigField.floatField("aoeCountMultiplier", "AOE Particle Count Multiplier", 0.1f, 5.0f, () -> ClientConfig.aoeParticleCountMultiplier, v -> ClientConfig.aoeParticleCountMultiplier = v));
        aoeParticleFields.add(ConfigField.floatField("aoeDensityMultiplier", "AOE Particle Density Multiplier", 0.1f, 5.0f, () -> ClientConfig.aoeParticleDensityMultiplier, v -> ClientConfig.aoeParticleDensityMultiplier = v));
        aoeParticleFields.add(ConfigField.floatField("aoeHeightMultiplier", "AOE Particle Height Multiplier", 0.1f, 5.0f, () -> ClientConfig.aoeParticleHeightMultiplier, v -> ClientConfig.aoeParticleHeightMultiplier = v));
        clientSubs.add(new SubSection("client_aoe_particles", "AOE Particles", Section.CLIENT, aoeParticleFields));
        
        List<ConfigField> combatVisualFields = new ArrayList<>();
        combatVisualFields.add(ConfigField.boolField("bloodPactEnabled", "Blood Pact Visual", () -> ClientConfig.bloodPactEnabled, v -> ClientConfig.bloodPactEnabled = v));
        combatVisualFields.add(ConfigField.intField("bloodPactParticleCount", "Pact Particle Density", 1, 100, () -> ClientConfig.bloodPactParticleCount, v -> ClientConfig.bloodPactParticleCount = v));
        combatVisualFields.add(ConfigField.boolField("bleedingParticles", "Bleeding Particles", () -> ClientConfig.bleedingParticleEnabled, v -> ClientConfig.bleedingParticleEnabled = v));
        clientSubs.add(new SubSection("client_combat_visuals", "Combat Visuals", Section.CLIENT, combatVisualFields));
        
        List<ConfigField> uiFields = new ArrayList<>();
        uiFields.add(ConfigField.boolField("showChangelogButton", "Main Menu Changelog Button", () -> ClientConfig.showChangelogButton, v -> ClientConfig.showChangelogButton = v));
        uiFields.add(ConfigField.intField("uiOverlayPosition", "UI Position (0-4)", 0, 4, () -> ClientConfig.uiOverlayPosition, v -> ClientConfig.uiOverlayPosition = v));
        uiFields.add(ConfigField.boolField("showDevWarning", "Show Dev Warning Popup", () -> ClientConfig.showDevWarning, v -> ClientConfig.showDevWarning = v));
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