package com.x4yi.hammers.config;

import net.minecraftforge.common.config.Config;

@Config(
        modid = "hammersunbound",
        name = "HammersUnbound/Items"
)
public class Items {

    public static HammersUnboundItems.WarMaterials warhammer = new HammersUnboundItems.WarMaterials();
    public static HammersUnboundItems.SpikeMaterials spikehammer = new HammersUnboundItems.SpikeMaterials();
}
