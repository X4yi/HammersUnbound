package com.x4yi.hammersunbound.init;

import com.x4yi.hammersunbound.HammersUnbound;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModCreativeTabs {

    public static final CreativeTabs HAMMERS_UNBOUND = new CreativeTabs(HammersUnbound.MODID + ".hammers") {
        @Override
        public ItemStack getTabIconItem() {
            Item warhammerDiamond = ForgeRegistries.ITEMS.getValue(
                    new ResourceLocation(HammersUnbound.MODID, "warhammer_diamond"));
            return warhammerDiamond != null ? new ItemStack(warhammerDiamond) : ItemStack.EMPTY;
        }
    };
}
