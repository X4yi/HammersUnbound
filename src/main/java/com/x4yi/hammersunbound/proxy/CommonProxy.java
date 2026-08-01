package com.x4yi.hammersunbound.proxy;
import com.x4yi.hammersunbound.event.HammerCombatHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
    }
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new HammerCombatHandler());
    }
    public void postInit(FMLPostInitializationEvent event) {
        com.x4yi.hammersunbound.crafting.HammerRecipeManager.loadRecipes();
    }
    public void serverStarting(FMLServerStartingEvent event) {
        net.minecraft.command.ServerCommandManager manager = (net.minecraft.command.ServerCommandManager) event.getServer().getCommandManager();
        manager.registerCommand(new com.x4yi.hammersunbound.command.ConfigReloadCommand());
    }
    public void handleBleedingSync(int entityId, int level) {
    }
    public void handleBloodPactVisual(int playerEntityId, int[] targetEntityIds, boolean active) {
    }
    public void handleAOEParticleSpawn(double posX, double posY, double posZ, float radius, int particleCount) {
    }
    public void handleConfigSync(String itemsJson, String serverJson) {
    }
}