package com.x4yi.hammersunbound.init;
import com.x4yi.hammersunbound.HammersUnbound;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
public class ModCreativeTabs {
    public static final CreativeTabs HAMMERS_UNBOUND = new CreativeTabs(HammersUnbound.MODID + ".hammers") {
        @Override
        public ItemStack getTabIconItem() {
            Item warhammerDiamond = ForgeRegistries.ITEMS.getValue(
                    new ResourceLocation(HammersUnbound.MODID, "warhammer_diamond"));
            return warhammerDiamond != null ? new ItemStack(warhammerDiamond) : ItemStack.EMPTY;
        }
        @Override
        @SideOnly(Side.CLIENT)
        public void displayAllRelevantItems(NonNullList<ItemStack> items) {
            addIfNotNull(items, "hammer_forge");
            addIfNotNull(items, "warhammer_wood");
            addIfNotNull(items, "warhammer_stone");
            addIfNotNull(items, "warhammer_iron");
            addIfNotNull(items, "warhammer_gold");
            addIfNotNull(items, "warhammer_diamond");
            addIfNotNull(items, "spikehammer_wood");
            addIfNotNull(items, "spikehammer_stone");
            addIfNotNull(items, "spikehammer_iron");
            addIfNotNull(items, "spikehammer_gold");
            addIfNotNull(items, "spikehammer_diamond");
        }
        private void addIfNotNull(NonNullList<ItemStack> items, String name) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(HammersUnbound.MODID, name));
            if (item != null) {
                items.add(new ItemStack(item));
            }
        }
    };
}