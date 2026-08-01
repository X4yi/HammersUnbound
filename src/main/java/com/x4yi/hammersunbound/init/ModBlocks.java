package com.x4yi.hammersunbound.init;
import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.block.BlockHammerForge;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import java.util.ArrayList;
import java.util.List;
@Mod.EventBusSubscriber(modid = HammersUnbound.MODID)
public class ModBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final BlockHammerForge HAMMER_FORGE = new BlockHammerForge();
    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(HAMMER_FORGE);
    }
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(HAMMER_FORGE).setRegistryName(HAMMER_FORGE.getRegistryName()));
    }
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(HAMMER_FORGE), 0, new ModelResourceLocation(HAMMER_FORGE.getRegistryName(), "inventory"));
    }
}