
package com.x4yi.hammers.init;

import com.x4yi.hammers.items.ItemSpikeHammer;
import com.x4yi.hammers.items.ItemWarHammer;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ModItems {

    public static final Item WARHAMMER_WOOD =
            new ItemWarHammer(ItemWarHammer.Type.WOOD)
                    .setRegistryName("warhammerwood")
                    .setUnlocalizedName("warhammerwood");

    public static final Item WARHAMMER_STONE =
            new ItemWarHammer(ItemWarHammer.Type.STONE)
                    .setRegistryName("warhammerstone")
                    .setUnlocalizedName("warhammerstone");

    public static final Item WARHAMMER_IRON =
            new ItemWarHammer(ItemWarHammer.Type.IRON)
                    .setRegistryName("warhammeriron")
                    .setUnlocalizedName("warhammeriron");

    public static final Item WARHAMMER_GOLD =
            new ItemWarHammer(ItemWarHammer.Type.GOLD)
                    .setRegistryName("warhammergold")
                    .setUnlocalizedName("warhammergold");

    public static final Item WARHAMMER_DIAMOND =
            new ItemWarHammer(ItemWarHammer.Type.DIAMOND)
                    .setRegistryName("warhammerdiamond")
                    .setUnlocalizedName("warhammerdiamond");

    public static final Item SPIKEHAMMER_WOOD =
            new ItemSpikeHammer(ItemSpikeHammer.MaterialType.WOOD)
                    .setRegistryName("spikehammerwood")
                    .setUnlocalizedName("spikehammerwood");
    public static final Item SPIKEHAMMER_STONE =
            new ItemSpikeHammer(ItemSpikeHammer.MaterialType.STONE)
                    .setRegistryName("spikehammerstone")
                    .setUnlocalizedName("spikehammerstone");
    public static final Item SPIKEHAMMER_IRON =
            new ItemSpikeHammer(ItemSpikeHammer.MaterialType.IRON)
                    .setRegistryName("spikehammeriron")
                    .setUnlocalizedName("spikehammeriron");
    public static final Item SPIKEHAMMER_GOLD =
            new ItemSpikeHammer(ItemSpikeHammer.MaterialType.GOLD)
                    .setRegistryName("spikehammergold")
                    .setUnlocalizedName("spikehammergold");
    public static final Item SPIKEHAMMER_DIAMOND =
            new ItemSpikeHammer(ItemSpikeHammer.MaterialType.DIAMOND)
                    .setRegistryName("spikehammerdiamond")
                    .setUnlocalizedName("spikehammerdiamond");

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                WARHAMMER_WOOD,
                WARHAMMER_STONE,
                WARHAMMER_IRON,
                WARHAMMER_GOLD,
                WARHAMMER_DIAMOND,

                SPIKEHAMMER_WOOD,
                SPIKEHAMMER_STONE,
                SPIKEHAMMER_IRON,
                SPIKEHAMMER_GOLD,
                SPIKEHAMMER_DIAMOND
        );
    }
}
