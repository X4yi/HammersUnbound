package com.x4yi.hammersunbound.proxy;

import com.x4yi.hammersunbound.client.gui.GuiConfigScreen;
import com.x4yi.hammersunbound.client.input.ModKeybinds;
import com.x4yi.hammersunbound.client.particle.HammerParticleFactory;
import com.x4yi.hammersunbound.command.ConfigReloadCommand;
import com.x4yi.hammersunbound.event.HammerClientHandler;
import com.x4yi.hammersunbound.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(this);
        ModKeybinds.register();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(new HammerClientHandler());
        MinecraftForge.EVENT_BUS.register(ModKeybinds.class);
        registerParticles();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        super.serverStarting(event);
        ServerCommandManager manager = (ServerCommandManager) event.getServer().getCommandManager();
        manager.registerCommand(new ConfigReloadCommand());
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        for (Item item : ModItems.WARHAMMERS.values()) {
            if (item != null) {
                ModelLoader.setCustomModelResourceLocation(item, 0,
                        new ModelResourceLocation(item.getRegistryName(), "inventory"));
            }
        }
        for (Item item : ModItems.SPIKEHAMMERS.values()) {
            if (item != null) {
                ModelLoader.setCustomModelResourceLocation(item, 0,
                        new ModelResourceLocation(item.getRegistryName(), "inventory"));
            }
        }
    }

    private void registerParticles() {
        Minecraft.getMinecraft().effectRenderer.registerParticle(
                100, new HammerParticleFactory());
    }
}
