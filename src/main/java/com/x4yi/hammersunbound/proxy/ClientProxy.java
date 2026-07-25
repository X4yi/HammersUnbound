package com.x4yi.hammersunbound.proxy;
import com.x4yi.hammersunbound.client.gui.GuiConfigScreen;
import com.x4yi.hammersunbound.client.input.ModKeybinds;
import com.x4yi.hammersunbound.client.particle.HammerParticleFactory;
import com.x4yi.hammersunbound.command.ConfigReloadCommand;
import com.x4yi.hammersunbound.event.HammerClientHandler;
import com.x4yi.hammersunbound.init.ModItems;
import com.x4yi.hammersunbound.capability.IBleedingCapability;
import com.x4yi.hammersunbound.network.PacketBloodPactVisual;
import com.x4yi.hammersunbound.client.particle.AOEParticleSpawner;
import com.x4yi.hammersunbound.client.gui.overlay.GuiSkillOverlay;
import com.x4yi.hammersunbound.config.WarHammerConfig;
import com.x4yi.hammersunbound.config.SpikeHammerConfig;
import com.x4yi.hammersunbound.config.ServerConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleSimpleAnimated;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
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
        try {
            java.lang.reflect.Field field;
            try {
                field = net.minecraft.client.Minecraft.class.getDeclaredField("defaultResourcePacks");
            } catch (NoSuchFieldException e) {
                field = net.minecraft.client.Minecraft.class.getDeclaredField("field_110449_ao");
            }
            field.setAccessible(true);
            java.util.List<net.minecraft.client.resources.IResourcePack> packs =
                (java.util.List<net.minecraft.client.resources.IResourcePack>) field.get(net.minecraft.client.Minecraft.getMinecraft());
            packs.add(new com.x4yi.hammersunbound.client.resources.HammerResourcePack());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(new HammerClientHandler());
        MinecraftForge.EVENT_BUS.register(new GuiSkillOverlay());
        MinecraftForge.EVENT_BUS.register(ModKeybinds.class);
        registerParticles();
    }
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        for (Item item : ModItems.WARHAMMERS.values()) {
            if (item != null) {
                String domain = item.getRegistryName().getResourceDomain();
                String path = item.getRegistryName().getResourcePath().replace("_", "");
                ModelLoader.setCustomModelResourceLocation(item, 0,
                        new ModelResourceLocation(new net.minecraft.util.ResourceLocation(domain, path), "inventory"));
            }
        }
        for (Item item : ModItems.SPIKEHAMMERS.values()) {
            if (item != null) {
                String domain = item.getRegistryName().getResourceDomain();
                String path = item.getRegistryName().getResourcePath().replace("_", "");
                ModelLoader.setCustomModelResourceLocation(item, 0,
                        new ModelResourceLocation(new net.minecraft.util.ResourceLocation(domain, path), "inventory"));
            }
        }
    }
    private void registerParticles() {
        Minecraft.getMinecraft().effectRenderer.registerParticle(
                100, new HammerParticleFactory());
    }
    @Override
    public void handleBleedingSync(int entityId, int level) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) return;
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityId);
            if (entity != null && entity.hasCapability(IBleedingCapability.CAPABILITY, null)) {
                IBleedingCapability cap = entity.getCapability(IBleedingCapability.CAPABILITY, null);
                if (cap != null) {
                    cap.getBleedingEffect().setLevel(level);
                }
            }
        });
    }
    @Override
    public void handleBloodPactVisual(int playerEntityId, int[] targetEntityIds, boolean active) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (active) {
                PacketBloodPactVisual.addVisual(playerEntityId, targetEntityIds);
            } else {
                PacketBloodPactVisual.removeVisual(playerEntityId);
            }
        });
    }
    @Override
    public void handleAOEParticleSpawn(double posX, double posY, double posZ, float radius, int particleCount) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().world == null) return;
            Vec3d center = new Vec3d(posX, posY, posZ);
            AOEParticleSpawner.spawnAOEParticles(Minecraft.getMinecraft().world, center, radius, particleCount);
        });
    }
    @Override
    public void handleConfigSync(String itemsJson, String serverJson) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            try {
                JsonObject itemsObj = new JsonParser().parse(itemsJson).getAsJsonObject();
                JsonObject serverObj = new JsonParser().parse(serverJson).getAsJsonObject();
                WarHammerConfig.parse(itemsObj);
                SpikeHammerConfig.parse(itemsObj);
                ServerConfig.parse(serverObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}