package com.x4yi.hammersunbound;

import com.x4yi.hammersunbound.config.ConfigManager;
import com.x4yi.hammersunbound.config.ServerConfig;
import com.x4yi.hammersunbound.init.ModCapabilities;
import com.x4yi.hammersunbound.network.ModNetworkHandler;
import com.x4yi.hammersunbound.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = HammersUnbound.MODID, name = "Hammers Unbound", version = HammersUnbound.VERSION, guiFactory = "com.x4yi.hammersunbound.client.gui.GuiFactory")
public class HammersUnbound {

    public static final String MODID = "hammersunbound";
    public static final String VERSION = "r1.0b1";

    @SidedProxy(clientSide = "com.x4yi.hammersunbound.proxy.ClientProxy", serverSide = "com.x4yi.hammersunbound.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModCapabilities.register();
        ConfigManager.init(event);
        ServerConfig.load();
        ModNetworkHandler.init();
        proxy.preInit(event);

        if (event.getSide() == net.minecraftforge.fml.relauncher.Side.CLIENT) {
            com.x4yi.hammersunbound.util.UpdateChecker.check();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
