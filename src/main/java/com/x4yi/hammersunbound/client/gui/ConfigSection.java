package com.x4yi.hammersunbound.client.gui;

import com.x4yi.hammersunbound.config.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        private final Runnable getter;
        private final java.util.function.Consumer<Float> floatSetter;
        private final java.util.function.Consumer<Integer> intSetter;
        private final java.util.function.Consumer<Boolean> boolSetter;

        public ConfigField(String key, String label, Type type, float minValue, float maxValue,
                          Runnable getter, java.util.function.Consumer<Float> floatSetter,
                          java.util.function.Consumer<Integer> intSetter,
                          java.util.function.Consumer<Boolean> boolSetter) {
            this.key = key;
            this.label = label;
            this.type = type;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.getter = getter;
            this.floatSetter = floatSetter;
            this.intSetter = intSetter;
            this.boolSetter = boolSetter;
        }

        public String getKey() { return key; }
        public String getLabel() { return label; }
        public Type getType() { return type; }
        public float getMinValue() { return minValue; }
        public float getMaxValue() { return maxValue; }

        public float getFloatValue() {
            return 0;
        }

        public void setFloatValue(float value) {
            if (floatSetter != null) floatSetter.accept(value);
        }

        public int getIntValue() {
            return 0;
        }

        public void setIntValue(int value) {
            if (intSetter != null) intSetter.accept(value);
        }

        public boolean getBoolValue() {
            return false;
        }

        public void setBoolValue(boolean value) {
            if (boolSetter != null) boolSetter.accept(value);
        }
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
            fields.add(new ConfigField("damage", "Damage", ConfigField.Type.FLOAT, 0, 100,
                    null, null, null, null));
            fields.add(new ConfigField("speed", "Attack Speed", ConfigField.Type.FLOAT, 0.1f, 4.0f,
                    null, null, null, null));
            fields.add(new ConfigField("durability", "Durability", ConfigField.Type.INT, 1, 10000,
                    null, null, null, null));
            fields.add(new ConfigField("skillCooldown", "Skill Cooldown (s)", ConfigField.Type.FLOAT, 0, 60,
                    null, null, null, null));
            fields.add(new ConfigField("aoeRadius", "AOE Radius", ConfigField.Type.FLOAT, 0.5f, 10,
                    null, null, null, null));
            fields.add(new ConfigField("aoeDamage", "AOE Damage", ConfigField.Type.FLOAT, 0, 50,
                    null, null, null, null));
            fields.add(new ConfigField("stunDuration", "Stun Duration (s)", ConfigField.Type.FLOAT, 0, 20,
                    null, null, null, null));
            fields.add(new ConfigField("aoeStunDuration", "AOE Stun Duration (s)", ConfigField.Type.FLOAT, 0, 20,
                    null, null, null, null));

            itemSubs.add(new SubSection("warhammer_" + material, "WarHammer " + capitalize(material), Section.ITEMS, fields));
        }

        for (String material : orderedMaterials) {
            SpikeHammerConfig.SpikeHammerMaterialEntry entry = SpikeHammerConfig.getMaterial(material);
            if (entry == null) continue;

            List<ConfigField> fields = new ArrayList<>();
            fields.add(new ConfigField("damage", "Damage", ConfigField.Type.FLOAT, 0, 100,
                    null, null, null, null));
            fields.add(new ConfigField("speed", "Attack Speed", ConfigField.Type.FLOAT, 0.1f, 4.0f,
                    null, null, null, null));
            fields.add(new ConfigField("durability", "Durability", ConfigField.Type.INT, 1, 10000,
                    null, null, null, null));
            fields.add(new ConfigField("skillCooldown", "Skill Cooldown (s)", ConfigField.Type.FLOAT, 0, 60,
                    null, null, null, null));
            fields.add(new ConfigField("bleedDamage", "Bleed Damage", ConfigField.Type.FLOAT, 0, 10,
                    null, null, null, null));
            fields.add(new ConfigField("bleedDuration", "Bleed Duration (s)", ConfigField.Type.FLOAT, 0, 60,
                    null, null, null, null));
            fields.add(new ConfigField("bleedTickInterval", "Bleed Tick Interval (s)", ConfigField.Type.FLOAT, 0.25f, 10,
                    null, null, null, null));
            fields.add(new ConfigField("bleedDecay", "Bleed Decay (s)", ConfigField.Type.FLOAT, 0, 60,
                    null, null, null, null));
            fields.add(new ConfigField("bloodPactRange", "Blood Pact Range", ConfigField.Type.FLOAT, 1, 32,
                    null, null, null, null));
            fields.add(new ConfigField("bloodPactDrainInterval", "Pact Drain Interval (s)", ConfigField.Type.FLOAT, 0.25f, 10,
                    null, null, null, null));

            itemSubs.add(new SubSection("spikehammer_" + material, "SpikeHammer " + capitalize(material), Section.ITEMS, fields));
        }
        sections.put(Section.ITEMS, itemSubs);

        List<SubSection> serverSubs = new ArrayList<>();
        List<ConfigField> whServerFields = new ArrayList<>();
        whServerFields.add(new ConfigField("stunDurationMult", "Stun Duration Multiplier", ConfigField.Type.FLOAT, 0, 5,
                null, null, null, null));
        whServerFields.add(new ConfigField("enableAOE", "Enable AOE", ConfigField.Type.BOOLEAN, 0, 1,
                null, null, null, null));
        whServerFields.add(new ConfigField("enableStun", "Enable Stun", ConfigField.Type.BOOLEAN, 0, 1,
                null, null, null, null));
        serverSubs.add(new SubSection("server_warhammer", "WarHammer (Server)", Section.SERVER, whServerFields));

        List<ConfigField> shServerFields = new ArrayList<>();
        shServerFields.add(new ConfigField("bleedDamageMult", "Bleed Damage Multiplier", ConfigField.Type.FLOAT, 0, 5,
                null, null, null, null));
        shServerFields.add(new ConfigField("bleedDurationMult", "Bleed Duration Multiplier", ConfigField.Type.FLOAT, 0, 5,
                null, null, null, null));
        shServerFields.add(new ConfigField("bloodPactRangeMult", "Blood Pact Range Multiplier", ConfigField.Type.FLOAT, 0, 5,
                null, null, null, null));
        shServerFields.add(new ConfigField("bloodPactDrainMult", "Blood Pact Drain Multiplier", ConfigField.Type.FLOAT, 0, 5,
                null, null, null, null));
        shServerFields.add(new ConfigField("enableBleeding", "Enable Bleeding", ConfigField.Type.BOOLEAN, 0, 1,
                null, null, null, null));
        shServerFields.add(new ConfigField("enableBloodPact", "Enable Blood Pact", ConfigField.Type.BOOLEAN, 0, 1,
                null, null, null, null));
        serverSubs.add(new SubSection("server_spikehammer", "SpikeHammer (Server)", Section.SERVER, shServerFields));
        sections.put(Section.SERVER, serverSubs);

        List<SubSection> clientSubs = new ArrayList<>();
        List<ConfigField> aoeParticleFields = new ArrayList<>();
        aoeParticleFields.add(new ConfigField("aoeEnabled", "AOE Particles Enabled", ConfigField.Type.BOOLEAN, 0, 1,
                null, null, null, null));
        aoeParticleFields.add(new ConfigField("aoeCountMultiplier", "AOE Particle Count Multiplier", ConfigField.Type.FLOAT, 0.1f, 5.0f,
                null, null, null, null));
        aoeParticleFields.add(new ConfigField("aoeDensityMultiplier", "AOE Particle Density Multiplier", ConfigField.Type.FLOAT, 0.1f, 5.0f,
                null, null, null, null));
        aoeParticleFields.add(new ConfigField("aoeHeightMultiplier", "AOE Particle Height Multiplier", ConfigField.Type.FLOAT, 0.1f, 5.0f,
                null, null, null, null));
        clientSubs.add(new SubSection("client_aoe_particles", "AOE Particles", Section.CLIENT, aoeParticleFields));

        List<ConfigField> combatVisualFields = new ArrayList<>();
        combatVisualFields.add(new ConfigField("bloodPactEnabled", "Blood Pact Visual", ConfigField.Type.BOOLEAN, 0, 1,
                null, null, null, null));
        combatVisualFields.add(new ConfigField("bleedingParticles", "Bleeding Particles", ConfigField.Type.BOOLEAN, 0, 1,
                null, null, null, null));
        clientSubs.add(new SubSection("client_combat_visuals", "Combat Visuals", Section.CLIENT, combatVisualFields));

        List<ConfigField> uiFields = new ArrayList<>();
        uiFields.add(new ConfigField("showChangelogButton", "Main Menu Changelog Button", ConfigField.Type.BOOLEAN, 0, 1,
                null, null, null, null));
        uiFields.add(new ConfigField("uiOverlayPosition", "UI Position (0-4)", ConfigField.Type.INT, 0, 4,
                null, null, null, null));
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
