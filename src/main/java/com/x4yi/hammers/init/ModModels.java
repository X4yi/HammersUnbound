package com.x4yi.hammers.init;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid="hammersunbound", value={Side.CLIENT})
public class ModModels {
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation((Item)ModItems.WARHAMMER_WOOD, (int)0, (ModelResourceLocation)new ModelResourceLocation("hammersunbound:warhammerwood", "inventory"));
        ModelLoader.setCustomModelResourceLocation((Item)ModItems.WARHAMMER_STONE, (int)0, (ModelResourceLocation)new ModelResourceLocation("hammersunbound:warhammerstone", "inventory"));
        ModelLoader.setCustomModelResourceLocation((Item)ModItems.WARHAMMER_IRON, (int)0, (ModelResourceLocation)new ModelResourceLocation("hammersunbound:warhammeriron", "inventory"));
        ModelLoader.setCustomModelResourceLocation((Item)ModItems.WARHAMMER_GOLD, (int)0, (ModelResourceLocation)new ModelResourceLocation("hammersunbound:warhammergold", "inventory"));
        ModelLoader.setCustomModelResourceLocation((Item)ModItems.WARHAMMER_DIAMOND, (int)0, (ModelResourceLocation)new ModelResourceLocation("hammersunbound:warhammerdiamond", "inventory"));
    }
}

