package com.x4yi.hammersunbound.init;

import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.config.HammerMaterialData;
import com.x4yi.hammersunbound.config.SpikeHammerConfig;
import com.x4yi.hammersunbound.config.WarHammerConfig;
import com.x4yi.hammersunbound.item.base.HammerMaterialType;
import com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem;
import com.x4yi.hammersunbound.item.warhammer.WarHammerItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.EnumMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = HammersUnbound.MODID)
public class ModItems {

    public static final Map<HammerMaterialType, WarHammerItem> WARHAMMERS = new EnumMap<>(HammerMaterialType.class);
    public static final Map<HammerMaterialType, SpikeHammerItem> SPIKEHAMMERS = new EnumMap<>(HammerMaterialType.class);

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerWarHammers(registry);
        registerSpikeHammers(registry);
    }

    private static void registerWarHammers(IForgeRegistry<Item> registry) {
        Map<String, WarHammerConfig.WarHammerMaterialEntry> configMap = WarHammerConfig.getAllMaterials();

        for (HammerMaterialType material : HammerMaterialType.values()) {
            WarHammerConfig.WarHammerMaterialEntry entry = configMap.get(material.getName());
            if (entry == null) continue;

            HammerMaterialData data = entry.data;
            String registryName = "warhammer_" + material.getName();

            WarHammerItem item = new WarHammerItem(material.getName(), data);
            item.setRegistryName(registryName);
            item.setUnlocalizedName(HammersUnbound.MODID + ".warhammer_" + material.getName());

            registry.register(item);
            WARHAMMERS.put(material, item);
        }
    }

    private static void registerSpikeHammers(IForgeRegistry<Item> registry) {
        Map<String, SpikeHammerConfig.SpikeHammerMaterialEntry> configMap = SpikeHammerConfig.getAllMaterials();

        for (HammerMaterialType material : HammerMaterialType.values()) {
            SpikeHammerConfig.SpikeHammerMaterialEntry entry = configMap.get(material.getName());
            if (entry == null) continue;

            HammerMaterialData data = entry.data;
            String registryName = "spikehammer_" + material.getName();

            SpikeHammerItem item = new SpikeHammerItem(material.getName(), data);
            item.setRegistryName(registryName);
            item.setUnlocalizedName(HammersUnbound.MODID + ".spikehammer_" + material.getName());

            registry.register(item);
            SPIKEHAMMERS.put(material, item);
        }
    }
}
