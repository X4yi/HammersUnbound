package com.x4yi.hammers.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = "hammersunbound", value = {Side.CLIENT})
public class ModModels {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(ModItems.WARHAMMER_WOOD, 0, new ModelResourceLocation("hammersunbound:warhammerwood", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.WARHAMMER_STONE, 0, new ModelResourceLocation("hammersunbound:warhammerstone", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.WARHAMMER_IRON, 0, new ModelResourceLocation("hammersunbound:warhammeriron", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.WARHAMMER_GOLD, 0, new ModelResourceLocation("hammersunbound:warhammergold", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.WARHAMMER_DIAMOND, 0, new ModelResourceLocation("hammersunbound:warhammerdiamond", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.SPIKEHAMMER_WOOD, 0, new ModelResourceLocation("hammersunbound:spikehammerwood", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.SPIKEHAMMER_STONE, 0, new ModelResourceLocation("hammersunbound:spikehammerstone", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.SPIKEHAMMER_IRON, 0, new ModelResourceLocation("hammersunbound:spikehammeriron", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.SPIKEHAMMER_GOLD, 0, new ModelResourceLocation("hammersunbound:spikehammergold", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.SPIKEHAMMER_DIAMOND, 0, new ModelResourceLocation("hammersunbound:spikehammerdiamond", "inventory"));


    }
}

