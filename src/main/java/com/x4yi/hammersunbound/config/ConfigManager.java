package com.x4yi.hammersunbound.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.*;

public class ConfigManager {

    private static final String CONFIG_DIR = "hammersunbound";
    private static final String ITEMS_CFG = "items.json";
    private static final String SERVER_CFG = "server.json";
    private static final String CLIENT_CFG = "client.json";

    private static File configDir;
    private static File itemsFile;
    private static File serverFile;
    private static File clientFile;

    public static void init(FMLPreInitializationEvent event) {
        configDir = new File(event.getModConfigurationDirectory(), CONFIG_DIR);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        itemsFile = new File(configDir, ITEMS_CFG);
        serverFile = new File(configDir, SERVER_CFG);
        clientFile = new File(configDir, CLIENT_CFG);

        copyDefaultsIfMissing();

        WarHammerConfig.load();
        SpikeHammerConfig.load();
        ServerConfig.load();
        ClientConfig.load();
    }

    private static void copyDefaultsIfMissing() {
        if (!itemsFile.exists()) {
            createDefaultItems();
        }
        createDefaultServer();
        createDefaultClient();
    }

    private static void createDefaultItems() {
        JsonObject items = new JsonObject();

        JsonObject warhammer = ConfigLoader.loadConfig("assets/hammersunbound/config/warhammer_stats.json");
        if (warhammer != null) {
            items.add("warhammer", warhammer);
        }

        JsonObject spikehammer = ConfigLoader.loadConfig("assets/hammersunbound/config/spikehammer_stats.json");
        if (spikehammer != null) {
            items.add("spikehammer", spikehammer);
        }

        saveJson(itemsFile, items);
    }

    private static void createDefaultServer() {
        if (!serverFile.exists()) {
            JsonObject server = new JsonObject();

            JsonObject warhammer = new JsonObject();
            warhammer.addProperty("aoeRadiusMultiplier", 1.0);
            warhammer.addProperty("aoeDamageMultiplier", 1.0);
            warhammer.addProperty("stunDurationMultiplier", 1.0);
            warhammer.addProperty("enableAOE", true);
            warhammer.addProperty("enableStun", true);
            server.add("warhammer", warhammer);

            JsonObject spikehammer = new JsonObject();
            spikehammer.addProperty("bleedingDamageMultiplier", 1.0);
            spikehammer.addProperty("bleedingDurationMultiplier", 1.0);
            spikehammer.addProperty("bloodPactRangeMultiplier", 1.0);
            spikehammer.addProperty("bloodPactDrainMultiplier", 1.0);
            spikehammer.addProperty("enableBleeding", true);
            spikehammer.addProperty("enableBloodPact", true);
            server.add("spikehammer", spikehammer);

            saveJson(serverFile, server);
        }
    }

    private static void createDefaultClient() {
        if (!clientFile.exists()) {
            JsonObject client = new JsonObject();

            JsonObject particles = new JsonObject();
            particles.addProperty("aoeParticleCount", 15);
            particles.addProperty("aoeParticleDensity", 1.0);
            particles.addProperty("aoeMaxHeight", 3.0);
            particles.addProperty("aoeEnabled", true);
            particles.addProperty("bloodPactEnabled", true);
            particles.addProperty("bloodPactParticleCount", 5);
            particles.addProperty("bleedingParticleEnabled", true);
            client.add("particles", particles);

            saveJson(clientFile, client);
        }
    }

    public static void reload() {
        WarHammerConfig.load();
        SpikeHammerConfig.load();
        ServerConfig.load();
        ClientConfig.load();
    }

    public static JsonObject loadItems() {
        if (itemsFile != null && !itemsFile.exists()) {
            createDefaultItems();
        }
        return loadJson(itemsFile);
    }

    public static JsonObject loadServer() {
        if (serverFile != null && !serverFile.exists()) {
            createDefaultServer();
        }
        return loadJson(serverFile);
    }

    public static JsonObject loadClient() {
        if (clientFile != null && !clientFile.exists()) {
            createDefaultClient();
        }
        return loadJson(clientFile);
    }

    private static JsonObject loadJson(File file) {
        if (file == null || !file.exists()) return null;
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null) {
                throw new IOException("JSON structure is empty");
            }
            return json;
        } catch (Exception e) {
            System.err.println("[Hammers Unbound] Failed to load config file: " + file.getAbsolutePath());
            e.printStackTrace();
            try {
                File corruptFile = new File(file.getParentFile(), file.getName() + ".corrupt");
                if (corruptFile.exists()) {
                    corruptFile.delete();
                }
                if (file.renameTo(corruptFile)) {
                    System.err.println("[Hammers Unbound] Renamed corrupt file to: " + corruptFile.getName());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private static void saveJson(File file, JsonObject json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(gson.toJson(json));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        if (configDir == null || itemsFile == null || serverFile == null || clientFile == null) {
            File baseDir = new File("config");
            configDir = new File(baseDir, CONFIG_DIR);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            itemsFile = new File(configDir, ITEMS_CFG);
            serverFile = new File(configDir, SERVER_CFG);
            clientFile = new File(configDir, CLIENT_CFG);
        }

        JsonObject items = new JsonObject();

        JsonObject warhammerJson = new JsonObject();
        JsonObject whMats = new JsonObject();
        for (java.util.Map.Entry<String, WarHammerConfig.WarHammerMaterialEntry> entry : WarHammerConfig.getAllMaterials().entrySet()) {
            whMats.add(entry.getKey(), entry.getValue().toJson());
        }
        warhammerJson.add("materials", whMats);
        items.add("warhammer", warhammerJson);

        JsonObject spikehammerJson = new JsonObject();
        JsonObject shMats = new JsonObject();
        for (java.util.Map.Entry<String, SpikeHammerConfig.SpikeHammerMaterialEntry> entry : SpikeHammerConfig.getAllMaterials().entrySet()) {
            shMats.add(entry.getKey(), entry.getValue().toJson());
        }
        spikehammerJson.add("materials", shMats);
        items.add("spikehammer", spikehammerJson);

        saveJson(itemsFile, items);

        JsonObject server = new JsonObject();
        JsonObject whServer = new JsonObject();
        whServer.addProperty("aoeRadiusMultiplier", ServerConfig.warhammerAoeRadiusMultiplier);
        whServer.addProperty("aoeDamageMultiplier", ServerConfig.warhammerAoeDamageMultiplier);
        whServer.addProperty("stunDurationMultiplier", ServerConfig.warhammerStunDurationMultiplier);
        whServer.addProperty("enableAOE", ServerConfig.warhammerEnableAOE);
        whServer.addProperty("enableStun", ServerConfig.warhammerEnableStun);
        server.add("warhammer", whServer);

        JsonObject shServer = new JsonObject();
        shServer.addProperty("bleedingDamageMultiplier", ServerConfig.spikehammerBleedingDamageMultiplier);
        shServer.addProperty("bleedingDurationMultiplier", ServerConfig.spikehammerBleedingDurationMultiplier);
        shServer.addProperty("bloodPactRangeMultiplier", ServerConfig.spikehammerBloodPactRangeMultiplier);
        shServer.addProperty("bloodPactDrainMultiplier", ServerConfig.spikehammerBloodPactDrainMultiplier);
        shServer.addProperty("enableBleeding", ServerConfig.spikehammerEnableBleeding);
        shServer.addProperty("enableBloodPact", ServerConfig.spikehammerEnableBloodPact);
        server.add("spikehammer", shServer);

        saveJson(serverFile, server);

        JsonObject client = new JsonObject();
        JsonObject particles = new JsonObject();
        particles.addProperty("aoeParticleCount", ClientConfig.aoeParticleCount);
        particles.addProperty("aoeParticleDensity", ClientConfig.aoeParticleDensity);
        particles.addProperty("aoeMaxHeight", ClientConfig.aoeMaxHeight);
        particles.addProperty("aoeEnabled", ClientConfig.aoeEnabled);
        particles.addProperty("bloodPactEnabled", ClientConfig.bloodPactEnabled);
        particles.addProperty("bloodPactParticleCount", ClientConfig.bloodPactParticleCount);
        particles.addProperty("bleedingParticleEnabled", ClientConfig.bleedingParticleEnabled);
        client.add("particles", particles);

        saveJson(clientFile, client);
    }

    public static File getConfigDir() {
        return configDir;
    }

    public static File getItemsFile() {
        return itemsFile;
    }

    public static File getServerFile() {
        return serverFile;
    }

    public static File getClientFile() {
        return clientFile;
    }
}
