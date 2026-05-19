package com.x4yi.hammersunbound.command;

import com.x4yi.hammersunbound.config.ConfigManager;
import com.x4yi.hammersunbound.config.ServerConfig;
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
            ConfigManager.reload();
            ServerConfig.load();
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "[Hammers Unbound] Configuration reloaded successfully."));
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Usage: /hammersunbound reload"));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
}
