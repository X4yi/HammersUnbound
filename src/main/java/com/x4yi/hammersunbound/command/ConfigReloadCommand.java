package com.x4yi.hammersunbound.command;

import com.x4yi.hammersunbound.config.ConfigManager;
import com.x4yi.hammersunbound.config.ServerConfig;
import com.x4yi.hammersunbound.config.SpikeHammerConfig;
import com.x4yi.hammersunbound.config.WarHammerConfig;
import com.x4yi.hammersunbound.network.ModNetworkHandler;
import com.x4yi.hammersunbound.network.PacketSyncConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class ConfigReloadCommand extends CommandBase {

    @Override
    public String getName() {
        return "hammersunbound";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/hammersunbound reload";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            // 1. Reload configs from file system
            ConfigManager.reload();
            ServerConfig.load();

            // 2. Serialize server configuration data
            JsonObject itemsJson = new JsonObject();
            JsonObject whMats = new JsonObject();
            for (java.util.Map.Entry<String, WarHammerConfig.WarHammerMaterialEntry> entry : WarHammerConfig.getAllMaterials().entrySet()) {
                whMats.add(entry.getKey(), entry.getValue().toJson());
            }
            JsonObject warhammerJson = new JsonObject();
            warhammerJson.add("materials", whMats);
            itemsJson.add("warhammer", warhammerJson);

            JsonObject shMats = new JsonObject();
            for (java.util.Map.Entry<String, SpikeHammerConfig.SpikeHammerMaterialEntry> entry : SpikeHammerConfig.getAllMaterials().entrySet()) {
                shMats.add(entry.getKey(), entry.getValue().toJson());
            }
            JsonObject spikehammerJson = new JsonObject();
            spikehammerJson.add("materials", shMats);
            itemsJson.add("spikehammer", spikehammerJson);

            JsonObject serverJson = new JsonObject();
            serverJson.addProperty("configVersion", com.x4yi.hammersunbound.HammersUnbound.VERSION);
            JsonObject whServer = new JsonObject();
            whServer.addProperty("stunDurationMultiplier", ServerConfig.warhammerStunDurationMultiplier);
            whServer.addProperty("enableAOE", ServerConfig.warhammerEnableAOE);
            whServer.addProperty("enableStun", ServerConfig.warhammerEnableStun);
            serverJson.add("warhammer", whServer);

            JsonObject shServer = new JsonObject();
            shServer.addProperty("bleedingDamageMultiplier", ServerConfig.spikehammerBleedingDamageMultiplier);
            shServer.addProperty("bleedingDurationMultiplier", ServerConfig.spikehammerBleedingDurationMultiplier);
            shServer.addProperty("bloodPactRangeMultiplier", ServerConfig.spikehammerBloodPactRangeMultiplier);
            shServer.addProperty("bloodPactDrainMultiplier", ServerConfig.spikehammerBloodPactDrainMultiplier);
            shServer.addProperty("enableBleeding", ServerConfig.spikehammerEnableBleeding);
            shServer.addProperty("enableBloodPact", ServerConfig.spikehammerEnableBloodPact);
            serverJson.add("spikehammer", shServer);

            Gson gson = new Gson();
            String itemsStr = gson.toJson(itemsJson);
            String serverStr = gson.toJson(serverJson);

            // 3. Broadcast sync packet to all connected players
            ModNetworkHandler.INSTANCE.sendToAll(new PacketSyncConfig(itemsStr, serverStr));

            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "[Hammers Unbound] Configuration reloaded and synchronized with all clients."));
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Usage: /hammersunbound reload"));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
